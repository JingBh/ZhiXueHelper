package top.jingbh.zhixuehelper.ui.exam

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.NetworkImageView
import com.google.android.material.color.MaterialColors
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONTokener
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.data.exam.ExamPaperTopic
import top.jingbh.zhixuehelper.data.exam.ExamPaperTopicType
import top.jingbh.zhixuehelper.data.util.CustomRequestQueue
import top.jingbh.zhixuehelper.databinding.FragmentPaperAnalysisTopicBinding
import top.jingbh.zhixuehelper.ui.util.emitDigits
import javax.inject.Inject

@AndroidEntryPoint
class PaperAnalysisTopicFragment : Fragment() {
    @Inject
    lateinit var requestQueue: CustomRequestQueue

    private lateinit var binding: FragmentPaperAnalysisTopicBinding

    private lateinit var topic: ExamPaperTopic

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        topic = arguments?.getSerializable(ARGUMENT_PAPER_TOPIC) as ExamPaperTopic

        binding = FragmentPaperAnalysisTopicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = getString(R.string.paper_topic_title, topic.id)

        binding.userAnswerCard.apply {
            val answerLayout = when (topic.type) {
                ExamPaperTopicType.TEXT -> R.layout.layout_topic_text_answer
                ExamPaperTopicType.IMAGE -> R.layout.layout_topic_image_answer
            }

            removeAllViews()
            layoutInflater.inflate(answerLayout, binding.userAnswerCard)

            findViewById<MaterialTextView>(R.id.answerTitle)
                .setText(R.string.paper_topic_answer_mine)
        }

        when (topic.type) {
            ExamPaperTopicType.TEXT -> {
                binding.userAnswerCard.apply {
                    findViewById<MaterialTextView>(R.id.answerText).text =
                        if (topic.userAnswer == "X") {
                            getString(R.string.paper_topic_answer_not_selected)
                        } else if (topic.userAnswer.isBlank()) {
                            getString(R.string.paper_topic_answer_not_filled)
                        } else topic.userAnswer
                }
            }

            ExamPaperTopicType.IMAGE -> {
                binding.userAnswerCard
                    .findViewById<LinearLayoutCompat>(R.id.answerImages).apply {
                        removeAllViews()

                        val imagesJson = JSONTokener(topic.userAnswer).nextValue() as JSONArray
                        if (imagesJson.length() == 0) binding.userAnswerCard.visibility = View.GONE

                        for (i in 0 until imagesJson.length()) {
                            val imageView = NetworkImageView(context)

                            addView(
                                imageView,
                                ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                            )

                            imageView.setImageUrl(imagesJson.getString(i), requestQueue.imageLoader)
                        }
                    }
            }
        }

        if (topic.standardAnswer != null) {
            binding.standardAnswerCard.apply {
                removeAllViews()

                visibility = View.VISIBLE

                val answerUri = Uri.parse(topic.standardAnswer)
                if (answerUri.isAbsolute) {
                    // is url
                    layoutInflater.inflate(
                        R.layout.layout_topic_image_answer,
                        binding.standardAnswerCard
                    )

                    findViewById<LinearLayoutCompat>(R.id.answerImages).apply {
                        removeAllViews()

                        val imageView = NetworkImageView(context)

                        addView(
                            imageView,
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                        )

                        imageView.setImageUrl(topic.standardAnswer, requestQueue.imageLoader)
                    }
                } else {
                    // not url
                    layoutInflater.inflate(
                        R.layout.layout_topic_text_answer,
                        binding.standardAnswerCard
                    )

                    findViewById<MaterialTextView>(R.id.answerText).text = topic.standardAnswer
                }

                findViewById<MaterialTextView>(R.id.answerTitle)
                    .setText(R.string.paper_topic_answer_standard)
            }
        } else binding.standardAnswerCard.visibility = View.GONE

        binding.userScore.apply {
            text = topic.userScore.emitDigits()

            val correctnessColor = context.getColor(
                when (topic.getCorrectness()) {
                    ExamPaperTopic.Correctness.CORRECT -> R.color.md_seed_success
                    ExamPaperTopic.Correctness.HALF_CORRECT -> R.color.md_seed_warning
                    ExamPaperTopic.Correctness.WRONG -> R.color.md_seed_error
                }
            )
            val correctnessColorRoles = MaterialColors.getColorRoles(context, correctnessColor)
            setTextColor(correctnessColorRoles.accent)
        }

        binding.fullScore.text = " / ${topic.fullScore.emitDigits()}"
    }

    companion object {
        const val ARGUMENT_PAPER_TOPIC = "top.jingbh.zhixuehelper.argument.PAPER_TOPIC"
    }
}
