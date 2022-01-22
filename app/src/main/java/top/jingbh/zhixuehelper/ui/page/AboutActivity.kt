package top.jingbh.zhixuehelper.ui.page

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import top.jingbh.zhixuehelper.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener { finish() }
    }
}
