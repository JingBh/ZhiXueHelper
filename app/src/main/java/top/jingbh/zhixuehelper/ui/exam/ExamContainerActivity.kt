package top.jingbh.zhixuehelper.ui.exam

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.databinding.ActivityExamContainerBinding
import top.jingbh.zhixuehelper.ui.auth.LoginActivity
import top.jingbh.zhixuehelper.ui.misc.AboutActivity

@AndroidEntryPoint
class ExamContainerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExamContainerBinding

    private lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel: ExamContainerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExamContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(binding.topAppBar)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .map { it.isLoginNeeded }
                    .distinctUntilChanged()
                    .filter { it }
                    .collect { toLogin(true) }
            }
        }

        navController.addOnDestinationChangedListener { _, _, _ ->
            supportActionBar?.subtitle = null
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.exam, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> return onSupportNavigateUp()
            R.id.switch_account -> toLogin()
            R.id.about -> {
                val intent = Intent(this@ExamContainerActivity, AboutActivity::class.java)
                startActivity(intent)
            }
            else -> return false
        }

        return true
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
}
