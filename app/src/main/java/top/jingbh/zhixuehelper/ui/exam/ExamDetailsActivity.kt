package top.jingbh.zhixuehelper.ui.exam

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.data.exam.Exam
import top.jingbh.zhixuehelper.databinding.ActivityExamDetailsBinding

@AndroidEntryPoint
class ExamDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExamDetailsBinding

    private lateinit var adapter: FragmentStateAdapter

    private val viewModel: ExamDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val exam = intent.getSerializableExtra(EXTRA_EXAM) as Exam
        viewModel.initSetExam(exam)

        binding = ActivityExamDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener { finish() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .map { it.isLoading }
                    .distinctUntilChanged()
                    .collect {
                        binding.progress.visibility = if (it) View.VISIBLE else View.GONE
                        binding.tabLayout.visibility = if (it) View.GONE else View.VISIBLE
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.exam
                    .distinctUntilChangedBy { it.id }
                    .collect {
                        binding.topAppBar.title = it.name
                    }
            }
        }

        lifecycleScope.launch {
            viewModel.papers
                .distinctUntilChanged()
                .collectLatest { papers ->
                    adapter = object : FragmentStateAdapter(this@ExamDetailsActivity) {
                        override fun getItemCount(): Int {
                            val count = papers.count()
                            return if (count == 0) 1 else count
                        }

                        override fun createFragment(position: Int): Fragment {
                            val fragment = PaperDetailsFragment()
                            fragment.arguments = Bundle().apply {
                                putSerializable(
                                    PaperDetailsFragment.ARGUMENT_PAPER,
                                    papers[position]
                                )
                            }

                            return fragment
                        }
                    }

                    binding.viewPager.adapter = adapter

                    TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                        if (papers.isEmpty() && position == 0) {
                            tab.setText(R.string.subject_all)
                        } else tab.text = papers[position].name
                    }.attach()
                }
        }
    }

    companion object {
        const val EXTRA_EXAM = "top.jingbh.zhixuehelper.extra.EXAM"
    }
}
