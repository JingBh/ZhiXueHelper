package top.jingbh.zhixuehelper.data.exam

enum class ExamPaperTopicType {
    TEXT,
    IMAGE;

    companion object {
        fun ofString(value: String): ExamPaperTopicType {
            return when (value) {
                "s01Text" -> TEXT
                "s02Image" -> IMAGE
                else -> throw IllegalArgumentException("Unknown answer type value: $value")
            }
        }
    }
}
