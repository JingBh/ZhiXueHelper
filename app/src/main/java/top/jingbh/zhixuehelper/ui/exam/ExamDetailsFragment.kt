package top.jingbh.zhixuehelper.ui.exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.platform.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.databinding.FragmentExamDetailsBinding
import top.jingbh.zhixuehelper.ui.util.EmptyFragment

@AndroidEntryPoint
class ExamDetailsFragment : Fragment() {
    private lateinit var binding: FragmentExamDetailsBinding

    private lateinit var adapter: FragmentStateAdapter

    private val viewModel: ExamDetailsViewModel by viewModels()

    private val args: ExamDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExamDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity()

        viewModel.initSetExam(args.exam)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .map { it.isLoading }
                    .distinctUntilChanged()
                    .collect {
                        binding.progress.visibility = if (it) View.VISIBLE else View.GONE
                        binding.tabLayout.visibility = if (it) View.GONE else View.VISIBLE
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.exam
                    .distinctUntilChangedBy { it.id }
                    .collect {
                        findNavController().currentDestination?.label = it.name
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.papers
                .distinctUntilChanged()
                .collectLatest { papers ->
                    binding.tabLayout.visibility =
                        if (papers.isEmpty()) View.GONE else View.VISIBLE

                    adapter = object : FragmentStateAdapter(activity) {
                        override fun getItemCount(): Int {
                            val count = papers.count()
                            return if (count == 0) 1 else count
                        }

                        override fun createFragment(position: Int): Fragment {
                            return if (papers.isEmpty()) {
                                EmptyFragment()
                            } else {
                                PaperDetailsFragment().apply {
                                    arguments = Bundle().apply {
                                        putSerializable(
                                            PaperDetailsFragment.ARGUMENT_PAPER,
                                            papers[position]
                                        )
                                    }
                                }
                            }
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
}
