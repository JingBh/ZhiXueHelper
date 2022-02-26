package top.jingbh.zhixuehelper.ui.exam

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.paging.filter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.lists.TwoLineItemMetaTextViewHolder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.data.exam.Exam
import top.jingbh.zhixuehelper.data.exam.ExamType
import top.jingbh.zhixuehelper.databinding.FragmentExamListBinding
import top.jingbh.zhixuehelper.ui.util.DateFormatter
import top.jingbh.zhixuehelper.ui.util.makeLoadingSnackbar
import javax.inject.Inject

@AndroidEntryPoint
class ExamListFragment : Fragment() {
    @Inject
    lateinit var dateFormatter: DateFormatter

    private lateinit var binding: FragmentExamListBinding

    private var loadingSnackbar: Snackbar? = null

    private val viewModel: ExamListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExamListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.exam_list, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val types = viewModel.uiState.value.examTypes

        menu.findItem(R.id.filter_weekly).isChecked =
            types.contains(ExamType.WEEKLY)
        menu.findItem(R.id.filter_monthly).isChecked =
            types.contains(ExamType.MONTHLY)
        menu.findItem(R.id.filter_midterm).isChecked =
            types.contains(ExamType.MIDTERM)
        menu.findItem(R.id.filter_terminal).isChecked =
            types.contains(ExamType.TERMINAL)
        menu.findItem(R.id.filter_others).isChecked =
            types.contains(ExamType.OTHERS)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity()

        val adapter = Adapter()
        binding.list.adapter = adapter

        val divider = MaterialDividerItemDecoration(activity, LinearLayoutManager.VERTICAL)
        divider.setDividerColorResource(activity, R.color.material_divider_color)
        binding.list.addItemDecoration(divider)

        viewLifecycleOwner.lifecycleScope.launch {
            val typesFlow = viewModel.uiState
                .map { it.examTypes }
                .distinctUntilChanged()

            viewModel.pagingFlow
                .combine(typesFlow) { pagingData, types ->
                    if (types.isNotEmpty()) {
                        pagingData.filter { types.contains(it.type) }
                    } else pagingData
                }
                .collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { states ->
                    if (states.refresh is LoadState.Loading && loadingSnackbar == null) {
                        loadingSnackbar =
                            makeLoadingSnackbar(binding.root, R.string.exam_list_loading)
                        loadingSnackbar!!.show()
                    } else if (loadingSnackbar != null) {
                        loadingSnackbar!!.dismiss()
                        loadingSnackbar = null
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_weekly -> viewModel.filterExamType(ExamType.WEEKLY)
            R.id.filter_monthly -> viewModel.filterExamType(ExamType.MONTHLY)
            R.id.filter_midterm -> viewModel.filterExamType(ExamType.MIDTERM)
            R.id.filter_terminal -> viewModel.filterExamType(ExamType.TERMINAL)
            R.id.filter_others -> viewModel.filterExamType(ExamType.OTHERS)
            else -> return false
        }

        requireActivity().invalidateOptionsMenu()

        return true
    }

    private inner class Adapter : PagingDataAdapter<Exam, TwoLineItemMetaTextViewHolder>(
        object : DiffUtil.ItemCallback<Exam>() {
            override fun areItemsTheSame(oldItem: Exam, newItem: Exam): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Exam, newItem: Exam): Boolean {
                return oldItem == newItem
            }
        }
    ) {
        override fun onBindViewHolder(holder: TwoLineItemMetaTextViewHolder, position: Int) {
            val item = getItem(position)
            if (item != null) {
                holder.text.text = item.name
                holder.secondaryText.text = dateFormatter.mediumFormatter.format(item.createdAt)
                holder.metaText.text = getString(item.type.toStringRes())

                holder.root.setOnClickListener {
                    val action = ExamListFragmentDirections.examListToDetails(item)
                    findNavController().navigate(action)
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): TwoLineItemMetaTextViewHolder {
            return TwoLineItemMetaTextViewHolder.create(parent)
        }
    }
}
