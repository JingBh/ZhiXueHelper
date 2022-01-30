package top.jingbh.zhixuehelper.data.subject

enum class Subject {
    CHINESE,
    MATHEMATICS,
    ENGLISH,
    PHYSICS,
    CHEMISTRY,
    POLITICS,
    HISTORY,
    GEOGRAPHY,
    BIOLOGY,
    ART,
    MUSIC,
    INFORMATION_TECHNOLOGY,
    GENERAL_TECHNOLOGY,
    PHYSICAL_EDUCATION,
    ALL_,
    UNKNOWN_;

    var variation: String? = ""

    /*
    Defined by zhixue.com
    Reference: https://www.zhixue.com/zhixuebao/report/exam/reportList/baseInfo
     */
    fun getSubjectCode(): String? {
        val code = when (this) {
            CHINESE -> "01"
            MATHEMATICS -> "02"
            ENGLISH -> "03"
            MUSIC -> "04"
            PHYSICS -> "05"
            CHEMISTRY -> "06"
            ART -> "09"
            GENERAL_TECHNOLOGY -> "102"
            PHYSICAL_EDUCATION -> "113"
            HISTORY -> "12"
            BIOLOGY -> "13"
            GEOGRAPHY -> "14"
            INFORMATION_TECHNOLOGY -> "26"
            POLITICS -> "27"
            ALL_ -> ""
            UNKNOWN_ -> null
        }

        return when (code) {
            null -> null
            "" -> ""
            else -> code + variation
        }
    }

    companion object {
        fun ofSubjectCode(subjectCode: String): Subject {
            var result = UNKNOWN_

            run loop@{
                values().forEach { subject ->
                    val thisSubjectCode = subject.getSubjectCode()

                    if (subjectCode == thisSubjectCode) {
                        result = subject
                        return@loop
                    } else if (!thisSubjectCode.isNullOrBlank()) {
                        val variation = subjectCode.substringAfter(thisSubjectCode, "")
                        if (variation != "") {
                            result = subject
                            result.variation = variation
                            return@loop
                        }
                    }
                }
            }

            return result
        }
    }
}
