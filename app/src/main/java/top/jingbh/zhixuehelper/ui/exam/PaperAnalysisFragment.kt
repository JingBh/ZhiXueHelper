package top.jingbh.zhixuehelper.ui.exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.ColorRoles
import com.google.android.material.color.MaterialColors
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.transition.platform.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.data.exam.ExamPaperTopic
import top.jingbh.zhixuehelper.data.util.CustomRequestQueue
import top.jingbh.zhixuehelper.databinding.FragmentPaperAnalysisBinding
import top.jingbh.zhixuehelper.databinding.ItemPaperAnalysisIndexBinding
import top.jingbh.zhixuehelper.ui.util.TopicTitleMatcher
import top.jingbh.zhixuehelper.ui.util.VerticalSpaceItemDecoration
import top.jingbh.zhixuehelper.ui.util.dpToPx
import javax.inject.Inject

@AndroidEntryPoint
class PaperAnalysisFragment : Fragment() {
    @Inject
    lateinit var requestQueue: CustomRequestQueue

    private lateinit var binding: FragmentPaperAnalysisBinding

    private val viewModel: PaperAnalysisViewModel by viewModels()

    private val args: PaperAnalysisFragmentArgs by navArgs()

    private val indexAdapter = IndexAdapter()
    private val topicAdapter = TopicAdapter()

    private lateinit var colorCorrectRoles: ColorRoles
    private lateinit var colorHalfCorrectRoles: ColorRoles
    private lateinit var colorWrongRoles: ColorRoles

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaperAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity()

        viewModel.initSetPaper(args.paper)

        binding.index.apply {
            adapter = indexAdapter

            val gutter = VerticalSpaceItemDecoration(dpToPx(4))
            addItemDecoration(gutter)
        }

        binding.list.apply {
            layoutManager = object : LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            ) {
                override fun calculateExtraLayoutSpace(
                    state: RecyclerView.State,
                    extraLayoutSpace: IntArray
                ) {
                    extraLayoutSpace[0] = height
                    extraLayoutSpace[1] = height
                }
            }
            adapter = topicAdapter

            val divider = MaterialDividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            divider.setDividerColorResource(activity, R.color.material_divider_color)
            addItemDecoration(divider)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val position = layoutManager.findFirstVisibleItemPosition()

                    indexAdapter.setSelected(position)
                }
            })
        }

        colorCorrectRoles =
            MaterialColors.getColorRoles(activity, activity.getColor(R.color.md_seed_success))
        colorHalfCorrectRoles =
            MaterialColors.getColorRoles(activity, activity.getColor(R.color.md_seed_warning))
        colorWrongRoles =
            MaterialColors.getColorRoles(activity, activity.getColor(R.color.md_seed_error))

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .map { it.isLoading }
                    .distinctUntilChanged()
                    .collect {
                        binding.progress.visibility = if (it) View.VISIBLE else View.GONE
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.paper
                    .map { it.fullName }
                    .distinctUntilChanged()
                    .collectLatest { paperName ->
                        val actionBar = (activity as AppCompatActivity).supportActionBar
                        actionBar?.subtitle = paperName
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.analysis
                .collectLatest { analysis ->
                    indexAdapter.submitList(analysis)
                    topicAdapter.submitList(analysis)
                }
        }
    }

    private inner class TopicAdapter : ListAdapter<ExamPaperTopic, PaperAnalysisTopicViewHolder>(
        object : DiffUtil.ItemCallback<ExamPaperTopic>() {
            override fun areItemsTheSame(
                oldItem: ExamPaperTopic,
                newItem: ExamPaperTopic
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ExamPaperTopic,
                newItem: ExamPaperTopic
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PaperAnalysisTopicViewHolder {
            return PaperAnalysisTopicViewHolder.create(
                layoutInflater,
                parent,
                requestQueue.imageLoader
            )
        }

        override fun onBindViewHolder(holder: PaperAnalysisTopicViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    private inner class IndexAdapter : ListAdapter<ExamPaperTopic, IndexViewHolder>(
        object : DiffUtil.ItemCallback<ExamPaperTopic>() {
            override fun areItemsTheSame(
                oldItem: ExamPaperTopic,
                newItem: ExamPaperTopic
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ExamPaperTopic,
                newItem: ExamPaperTopic
            ): Boolean {
                return oldItem.id == newItem.id && oldItem.getCorrectness() == newItem.getCorrectness()
            }
        }
    ) {
        private var selected: Int = 0

        fun setSelected(index: Int) {
            val oldIndex = selected
            selected = index
            notifyItemChanged(index)
            notifyItemChanged(oldIndex)
            binding.index.smoothScrollToPosition(index)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndexViewHolder {
            val binding = ItemPaperAnalysisIndexBinding.inflate(layoutInflater, parent, false)
            return IndexViewHolder(binding)
        }

        override fun onBindViewHolder(holder: IndexViewHolder, position: Int) {
            holder.bind(getItem(position), position == selected)
        }
    }

    private inner class IndexViewHolder(
        private val binding: ItemPaperAnalysisIndexBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: ExamPaperTopic, selected: Boolean = false) {
            binding.text.apply {
                text = TopicTitleMatcher.of(topic.title).short

                setTextColor(
                    when (topic.getCorrectness()) {
                        ExamPaperTopic.Correctness.CORRECT -> colorCorrectRoles.accent
                        ExamPaperTopic.Correctness.HALF_CORRECT -> colorHalfCorrectRoles.accent
                        ExamPaperTopic.Correctness.WRONG -> colorWrongRoles.accent
                    }
                )
            }

            binding.root.isSelected = selected
            if (!selected) binding.root.setOnClickListener {
                this@PaperAnalysisFragment.binding.list
                    .smoothScrollToPosition(bindingAdapterPosition)
            }
        }
    }
}
