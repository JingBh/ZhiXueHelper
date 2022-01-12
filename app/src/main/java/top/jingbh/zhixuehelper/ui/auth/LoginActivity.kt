package top.jingbh.zhixuehelper.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.databinding.ActivityLoginBinding
import top.jingbh.zhixuehelper.ui.exam.ListExamActivity
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
            }
            true
        }

        viewModel.isLoading().observe(this) { isLoading ->
            if (isLoading && loadingSnackbar == null) {
                loadingSnackbar = makeLoadingSnackbar(binding.root, R.string.login_loading)
                loadingSnackbar!!.show()
            } else if (loadingSnackbar != null) {
                loadingSnackbar!!.dismiss()
                loadingSnackbar = null
            }
        }

        // On login fail
        viewModel.isLoginFailed().observe(this) { isLoginFailed ->
            if (isLoginFailed == true) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.login_failed)
                    .setMessage(R.string.login_failed_help)
                    .setPositiveButton(R.string.okay, null)
                    .setOnDismissListener {
                        viewModel.clearLoginFailed()
                    }
                    .show()
            }
        }

        // On login success
        viewModel.isLoggedIn().observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                val intent = Intent(this, ListExamActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
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

                        viewModel.updateToken(token)

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
