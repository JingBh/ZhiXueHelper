package top.jingbh.zhixuehelper.ui.misc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ImageViewCompat
import com.google.android.material.color.MaterialColors
import top.jingbh.zhixuehelper.BuildConfig
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.data.distribute.GitHubWebService
import top.jingbh.zhixuehelper.databinding.ActivityAboutBinding
import top.jingbh.zhixuehelper.service.UpdateService

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    private lateinit var binder: UpdateService.UpdateBinder
    private var connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as UpdateService.UpdateBinder
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener { finish() }

        if (!BuildConfig.IS_STABLE) {
            val warningColorSeed = getColor(R.color.md_seed_warning)
            val warningColors = MaterialColors.getColorRoles(this, warningColorSeed)
            binding.unstableCard.setCardBackgroundColor(warningColors.accentContainer)
            ImageViewCompat.setImageTintList(
                binding.unstableIcon,
                ColorStateList.valueOf(warningColors.onAccentContainer)
            )
            binding.unstableTitle.setTextColor(warningColors.onAccentContainer)
            binding.unstableHelp.setTextColor(warningColors.onAccentContainer)

            binding.unstableCard.visibility = View.VISIBLE
        }

        if (BuildConfig.DEBUG) {
            binding.debugCard.visibility = View.VISIBLE
        }

        binding.version.text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

        if (BuildConfig.DEBUG) {
            binding.checkUpdate.visibility = View.GONE
        } else binding.checkUpdate.setOnClickListener {
            binder.checkUpdate(this, false)
        }

        binding.viewSource.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse(GitHubWebService.getWebUrl())

            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, UpdateService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()

        unbindService(connection)
    }
}
