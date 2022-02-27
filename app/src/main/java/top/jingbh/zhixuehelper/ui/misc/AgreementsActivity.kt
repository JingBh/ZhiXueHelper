package top.jingbh.zhixuehelper.ui.misc

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.appcenter.analytics.Analytics
import dagger.hilt.android.AndroidEntryPoint
import top.jingbh.zhixuehelper.databinding.ActivityAgreementsBinding
import top.jingbh.zhixuehelper.ui.exam.ExamContainerActivity
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

        val beforeAgreed = agreements.isAgreementsAgreed()
        binding.checkbox.isChecked = beforeAgreed

        if (beforeAgreed) {
            binding.checkbox.visibility = View.GONE
            binding.nextButton.visibility = View.GONE
        } else {
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                binding.nextButton.isEnabled = isChecked
            }

            binding.nextButton.setOnClickListener {
                if (binding.checkbox.isChecked) {
                    agreements.agreeAgreements()

                    Analytics.setEnabled(true)

                    val intent = Intent(this, ExamContainerActivity::class.java)
                    startActivity(intent)

                    finish()
                }
            }
        }
    }
}
