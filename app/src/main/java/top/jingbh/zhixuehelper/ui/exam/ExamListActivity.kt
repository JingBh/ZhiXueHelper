package top.jingbh.zhixuehelper.ui.exam

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.lists.TwoLineItemMetaTextViewHolder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.data.exam.Exam
import top.jingbh.zhixuehelper.databinding.ActivityExamListBinding
import top.jingbh.zhixuehelper.ui.auth.LoginActivity
import top.jingbh.zhixuehelper.ui.misc.AboutActivity
import top.jingbh.zhixuehelper.ui.util.DateFormatter
import top.jingbh.zhixuehelper.ui.util.makeLoadingSnackbar
import javax.inject.Inject

@AndroidEntryPoint
class ExamListActivity : AppCompatActivity() {
    @Inject
    lateinit var dateFormatter: DateFormatter

    private lateinit var binding: ActivityExamListBinding

    private var loadingSnackbar: Snackbar? = null

    private val viewModel: ExamListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExamListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.switch_account -> toLogin()
                R.id.about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        val adapter = Adapter()
        binding.list.adapter = adapter

        val divider = MaterialDividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        divider.setDividerColorResource(this, R.color.material_divider_color)
        binding.list.addItemDecoration(divider)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .map { it.isLoginNeeded }
                    .distinctUntilChanged()
                    .filter { it }
                    .collect { toLogin(true) }
            }
        }

        lifecycleScope.launch {
            viewModel.pagingFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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

    override fun onResume() {
        super.onResume()

        val state = viewModel.uiState.value
        if (!state.isLoggedIn) viewModel.checkLogin()
    }

    private fun toLogin(finish: Boolean = false) {
        viewModel.wentToLogin()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

        if (finish) finish()
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
                    val context = this@ExamListActivity
                    val intent = Intent(context, ExamDetailsActivity::class.java)
                    intent.putExtra(ExamDetailsActivity.EXTRA_EXAM, item)

                    startActivity(intent)
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
