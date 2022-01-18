package top.jingbh.zhixuehelper.ui.page

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import top.jingbh.zhixuehelper.databinding.ActivityAgreementsBinding
import top.jingbh.zhixuehelper.ui.exam.ExamListActivity
import top.jingbh.zhixuehelper.ui.util.Agreements
import javax.inject.Inject

@AndroidEntryPoint
class AgreementsActivity : AppCompatActivity() {
    @Inject
    lateinit var agreements: Agreements

    private lateinit var binding: ActivityAgreementsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAgreementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val html = agreements.getAgreementsHtml()
        binding.content.movementMethod = LinkMovementMethod.getInstance()
        binding.content.text = html

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            binding.nextButton.isEnabled = isChecked
        }

        binding.nextButton.setOnClickListener {
            if (binding.checkbox.isChecked) {
                agreements.agreeAgreements()

                val intent = Intent(this, ExamListActivity::class.java)
                startActivity(intent)

                finish()
            }
        }
    }
}
