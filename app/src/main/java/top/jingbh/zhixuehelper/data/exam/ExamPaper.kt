package top.jingbh.zhixuehelper.data.exam

import androidx.annotation.Keep
import top.jingbh.zhixuehelper.data.subject.Subject
import java.io.Serializable

@Keep
data class ExamPaper(
    val id: String,
    val name: String,
    val fullName: String,
    val subject: Subject
) : Serializable
