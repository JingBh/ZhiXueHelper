package top.jingbh.zhixuehelper.data.exam

import androidx.annotation.StringRes
import top.jingbh.zhixuehelper.R

enum class ExamType {
    WEEKLY,
    MONTHLY,
    MIDTERM,
    TERMINAL,
    OTHERS;

    @StringRes
    fun toStringRes(): Int {
        return when (this) {
            WEEKLY -> R.string.exam_type_weekly
            MONTHLY -> R.string.exam_type_monthly
            MIDTERM -> R.string.exam_type_midterm
            TERMINAL -> R.string.exam_type_terminal
            else -> R.string.others
        }
    }

    companion object {
        fun ofString(string: String): ExamType {
            return when (string) {
                "weeklyExam" -> WEEKLY
                "monthlyExam" -> MONTHLY
                "midtermExam" -> MIDTERM
                "terminalExam" -> TERMINAL
                else -> OTHERS
            }
        }
    }
}
