package top.jingbh.zhixuehelper.ui.misc

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.MaterialColors
import top.jingbh.zhixuehelper.BuildConfig
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener { finish() }

        if (!BuildConfig.IS_STABLE) {
            val warningColorSeed = getColor(R.color.md_seed_warning)
            val warningColors = MaterialColors.getColorRoles(this, warningColorSeed)
            binding.unstableCard.setCardBackgroundColor(warningColors.accentContainer)

            binding.unstableCard.visibility = View.VISIBLE
        }

        if (BuildConfig.DEBUG) {
            binding.debugCard.visibility = View.VISIBLE
        }

        binding.version.text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

        binding.viewSource.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://github.com/JingBh/ZhiXueHelper")
            startActivity(intent)
        }
    }
}
