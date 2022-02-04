package top.jingbh.zhixuehelper.data.exam

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class ExamPaperTopic(
    val id: Int,
    val type: ExamPaperTopicType,
    val title: String,
    val standardAnswer: String?,
    val userAnswer: String,
    val fullScore: Double,
    val userScore: Double
) : Serializable {
    fun getCorrectness(): Correctness {
        return if (userScore >= fullScore) {
            Correctness.CORRECT
        } else if (userScore > 0.0) {
            Correctness.HALF_CORRECT
        } else {
            Correctness.WRONG
        }
    }

    enum class Correctness {
        CORRECT,
        HALF_CORRECT,
        WRONG
    }
}
