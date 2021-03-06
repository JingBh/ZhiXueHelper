package top.jingbh.zhixuehelper.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.databinding.ActivityLoginBinding
import top.jingbh.zhixuehelper.ui.exam.ExamListActivity
import top.jingbh.zhixuehelper.ui.misc.AboutActivity
import top.jingbh.zhixuehelper.ui.util.makeLoadingSnackbar
import java.io.FileNotFoundException

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var navController: NavController

    private var loadingSnackbar: Snackbar? = null

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.topAppBar.setupWithNavController(navController, appBarConfiguration)

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.help -> MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.help)
                    .setMessage(R.string.login_help)
                    .setPositiveButton(R.string.okay, null)
                    .show()
                R.id.about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .map { it.isLoading }
                    .collect { isLoading ->
                        if (isLoading && loadingSnackbar == null) {
                            loadingSnackbar =
                                makeLoadingSnackbar(binding.root, R.string.login_loading)
                            loadingSnackbar!!.show()
                        } else if (loadingSnackbar != null) {
                            loadingSnackbar!!.dismiss()
                            loadingSnackbar = null
                        }
                    }
            }
        }

        // On login success
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .map { it.isLoggedIn }
                    .filter { it }
                    .collect {
                        val intent =
                            Intent(this@LoginActivity, ExamListActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
            }
        }

        // On login fail
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .map { it.errorMessage }
                    .filterNotNull()
                    .collect { message ->
                        MaterialAlertDialogBuilder(this@LoginActivity)
                            .setTitle(R.string.login_failed)
                            .setMessage(message)
                            .setPositiveButton(R.string.okay, null)
                            .setOnDismissListener {
                                viewModel.clearLoginFailure()
                            }
                            .show()
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        receiveIntent()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        receiveIntent(intent)
    }

    private fun receiveIntent(intent: Intent? = null) {
        val uri = (intent ?: getIntent()).data
        if (uri != null) {
            Log.d(TAG, "Intent data: $uri")

            try {
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    try {
                        val reader = inputStream.bufferedReader()
                        val json = reader.readText()

                        val jsonObject = JSONTokener(json).nextValue() as JSONObject
                        val token = jsonObject.getString("token")

                        viewModel.updateToken(token, true)

                        reader.close()
                    } catch (e: JSONException) {
                        Log.e(TAG, "Parse JSON failed", e)
                    }
                    inputStream.close()
                }
            } catch (e: FileNotFoundException) {
                Log.e(TAG, "File not found", e)
            }
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
