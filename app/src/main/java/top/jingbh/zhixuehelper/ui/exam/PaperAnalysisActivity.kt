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
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.color.ColorRoles
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.data.exam.ExamPaper
import top.jingbh.zhixuehelper.data.exam.ExamPaperTopic
import top.jingbh.zhixuehelper.databinding.ActivityPaperAnalysisBinding
import top.jingbh.zhixuehelper.databinding.ItemPaperAnalysisIndexBinding
import top.jingbh.zhixuehelper.ui.util.VerticalSpaceItemDecoration
import top.jingbh.zhixuehelper.ui.util.dpToPx

@AndroidEntryPoint
class PaperAnalysisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaperAnalysisBinding

    private val viewModel: PaperAnalysisViewModel by viewModels()

    private val indexAdapter = IndexAdapter()
    private lateinit var topicAdapter: FragmentStateAdapter

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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .map { it.selectedIndex }
                    .filter { it >= 0 }
                    .distinctUntilChanged()
                    .collectLatest {
                        indexAdapter.notifyItemChanged(it)

                        if (binding.viewPager.currentItem != it)
                            binding.viewPager.currentItem = it
                    }
            }
        }

        lifecycleScope.launch {
            viewModel.analysis
                .collectLatest { analysis ->
                    topicAdapter = object : FragmentStateAdapter(this@PaperAnalysisActivity) {
                        override fun getItemCount(): Int = analysis.size

                        override fun createFragment(position: Int): PaperAnalysisTopicFragment {
                            val fragment = PaperAnalysisTopicFragment()
                            fragment.arguments = Bundle().apply {
                                putSerializable(
                                    PaperAnalysisTopicFragment.ARGUMENT_PAPER_TOPIC,
                                    analysis[position]
                                )
                            }

                            return fragment
                        }
                    }
                    binding.viewPager.apply {
                        adapter = topicAdapter
                        offscreenPageLimit = topicAdapter.itemCount
                        requestDisallowInterceptTouchEvent(true)
                    }

                    indexAdapter.submitList(analysis)
                }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val oldPosition = viewModel.uiState.value.selectedIndex
                if (oldPosition != position) {
                    indexAdapter.notifyItemChanged(oldPosition)
                    viewModel.updateSelectedIndex(position)
                }
            }
        })
    }

    private inner class IndexViewHolder(
        private val binding: ItemPaperAnalysisIndexBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: ExamPaperTopic) {
            binding.text.text = topic.id.toString()
            binding.text.setTextColor(
                when (topic.getCorrectness()) {
                    ExamPaperTopic.Correctness.CORRECT -> colorCorrectRoles.accent
                    ExamPaperTopic.Correctness.HALF_CORRECT -> colorHalfCorrectRoles.accent
                    ExamPaperTopic.Correctness.WRONG -> colorWrongRoles.accent
                }
            )

            val isSelected = viewModel.uiState.value.selectedIndex == absoluteAdapterPosition
            binding.root.isSelected = isSelected

            if (!isSelected) binding.root.setOnClickListener {
                indexAdapter.notifyItemChanged(viewModel.uiState.value.selectedIndex)
                viewModel.updateSelectedIndex(absoluteAdapterPosition)
            }
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
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndexViewHolder {
            val binding = ItemPaperAnalysisIndexBinding.inflate(layoutInflater, parent, false)
            return IndexViewHolder(binding)
        }

        override fun onBindViewHolder(holder: IndexViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    companion object {
        const val EXTRA_PAPER = "top.jingbh.zhixuehelper.extra.PAPER"
    }
}
