package top.jingbh.zhixuehelper.ui.exam

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.data.exam.Exam
import top.jingbh.zhixuehelper.databinding.ActivityExamDetailsBinding

@AndroidEntryPoint
class ExamDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExamDetailsBinding

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
                    .distinctUntilChanged()
                    .collect {
                        binding.topAppBar.title = it.name
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.papers
                    .distinctUntilChanged()
                    .collect { papers ->
                        binding.tabLayout.removeAllTabs()

                        if (papers.isEmpty()) {
                            val tab = binding.tabLayout.newTab()
                            tab.setText(R.string.subject_all)

                            binding.tabLayout.addTab(tab)
                        } else {
                            papers.forEach { paper ->
                                val tab = binding.tabLayout.newTab()
                                tab.text = paper.name

                                binding.tabLayout.addTab(tab)
                            }
                        }
                    }
            }
        }
    }

    companion object {
        const val EXTRA_EXAM = "top.jingbh.zhixuehelper.extra.EXAM"
    }
}
