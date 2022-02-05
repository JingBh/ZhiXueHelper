package top.jingbh.zhixuehelper.ui.exam

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.ColorRoles
import com.google.android.material.color.MaterialColors
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.data.exam.ExamPaper
import top.jingbh.zhixuehelper.data.exam.ExamPaperTopic
import top.jingbh.zhixuehelper.data.util.CustomRequestQueue
import top.jingbh.zhixuehelper.databinding.ActivityPaperAnalysisBinding
import top.jingbh.zhixuehelper.databinding.ItemPaperAnalysisIndexBinding
import top.jingbh.zhixuehelper.ui.util.VerticalSpaceItemDecoration
import top.jingbh.zhixuehelper.ui.util.dpToPx
import javax.inject.Inject
import top.jingbh.zhixuehelper.ui.exam.PaperAnalysisTopicViewHolder as TopicViewHolder

@AndroidEntryPoint
class PaperAnalysisActivity : AppCompatActivity() {
    @Inject
    lateinit var requestQueue: CustomRequestQueue

    private lateinit var binding: ActivityPaperAnalysisBinding

    private val viewModel: PaperAnalysisViewModel by viewModels()

    private val indexAdapter = IndexAdapter()
    private val topicAdapter = TopicAdapter()

    private lateinit var colorCorrectRoles: ColorRoles
    private lateinit var colorHalfCorrectRoles: ColorRoles
    private lateinit var colorWrongRoles: ColorRoles

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val paper = intent.getSerializableExtra(EXTRA_PAPER) as ExamPaper
        viewModel.initSetPaper(paper)

        binding = ActivityPaperAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener { finish() }

        binding.index.apply {
            adapter = indexAdapter

            val gutter = VerticalSpaceItemDecoration(dpToPx(4))
            addItemDecoration(gutter)
        }

        binding.list.apply {
            layoutManager = object : LinearLayoutManager(
                this@PaperAnalysisActivity,
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

            val context = this@PaperAnalysisActivity
            val divider = MaterialDividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            divider.setDividerColorResource(context, R.color.material_divider_color)
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
            MaterialColors.getColorRoles(this, getColor(R.color.md_seed_success))
        colorHalfCorrectRoles =
            MaterialColors.getColorRoles(this, getColor(R.color.md_seed_warning))
        colorWrongRoles =
            MaterialColors.getColorRoles(this, getColor(R.color.md_seed_error))

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .map { it.isLoading }
                    .distinctUntilChanged()
                    .collect {
                        binding.progress.visibility = if (it) View.VISIBLE else View.GONE
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.paper
                    .map { it.fullName }
                    .distinctUntilChanged()
                    .collectLatest { paperName ->
                        binding.topAppBar.subtitle = paperName
                    }
            }
        }

        /*lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .map { it.selectedIndex }
                    .filter { it >= 0 }
                    .distinctUntilChanged()
                    .collectLatest {
                        indexAdapter.notifyItemChanged(it)
                        binding.list.smoothScrollToPosition(it)
                    }
            }
        }*/

        lifecycleScope.launch {
            viewModel.analysis
                .collectLatest { analysis ->
                    indexAdapter.submitList(analysis)
                    topicAdapter.submitList(analysis)
                }
        }
    }

    private inner class TopicAdapter : ListAdapter<ExamPaperTopic, TopicViewHolder>(
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
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
            return TopicViewHolder.create(layoutInflater, parent, requestQueue.imageLoader)
        }

        override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
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
            binding.text.text = topic.id.toString()
            binding.text.setTextColor(
                when (topic.getCorrectness()) {
                    ExamPaperTopic.Correctness.CORRECT -> colorCorrectRoles.accent
                    ExamPaperTopic.Correctness.HALF_CORRECT -> colorHalfCorrectRoles.accent
                    ExamPaperTopic.Correctness.WRONG -> colorWrongRoles.accent
                }
            )

            binding.root.isSelected = selected
            if (!selected) binding.root.setOnClickListener {
                this@PaperAnalysisActivity.binding.list
                    .smoothScrollToPosition(bindingAdapterPosition)
            }
        }
    }

    companion object {
        const val EXTRA_PAPER = "top.jingbh.zhixuehelper.extra.PAPER"
    }
}
