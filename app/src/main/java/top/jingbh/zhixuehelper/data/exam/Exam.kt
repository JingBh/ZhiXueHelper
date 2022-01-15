package top.jingbh.zhixuehelper.data.exam

import androidx.annotation.Keep
import java.io.Serializable
import java.util.*

@Keep
data class Exam(
    val id: String,
    val name: String,
    val type: ExamType,
    val createdAt: Date
) : Serializable
