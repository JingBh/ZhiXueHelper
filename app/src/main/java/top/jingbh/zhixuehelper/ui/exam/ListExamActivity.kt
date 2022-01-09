package top.jingbh.zhixuehelper.ui.exam

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.databinding.ActivityListExamBinding
import top.jingbh.zhixuehelper.ui.auth.LoginActivity

@AndroidEntryPoint
class ListExamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListExamBinding

    private val viewModel: ListExamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListExamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.switch_account -> toLogin()
            }
            true
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.reload()
        }

        viewModel.isLoading().observe(this) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }

        viewModel.isLoginNeeded().observe(this) { isLoginNeeded ->
            if (isLoginNeeded) toLogin()
        }
    }

    override fun onResume() {
        super.onResume()

        if (viewModel.isLoading().value != true && viewModel.isLoaded().value != true) {
            viewModel.reload()
        }
    }

    private fun toLogin() {
        viewModel.wentToLogin()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
