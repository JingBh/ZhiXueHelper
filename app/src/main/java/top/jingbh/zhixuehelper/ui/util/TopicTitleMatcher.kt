package top.jingbh.zhixuehelper.ui.util

import android.content.res.Resources
import androidx.annotation.StringRes
import top.jingbh.zhixuehelper.R

class TopicTitleMatcher private constructor(
    val short: String,
    private val nMain: String,
    private val nSub: String = "",
    @StringRes private val format: Int = -1
) {
    fun getFormatted(resources: Resources): String {
        val format = if (this.format == -1) {
            if (nSub == "") R.string.paper_topic_title else R.string.paper_topic_title_sub
        } else this.format

        return if (format > 0) {
            resources.getString(format, nMain, nSub)
        } else nMain
    }

    companion object {
        fun of(topicTitle: String): TopicTitleMatcher {
            Regex("([\\u4e00\\u4e8c\\u4e09\\u56db\\u4e94\\u516d\\u4e03\\u516b\\u4e5d]|[1-9][0-9]*)(?: ?[(\\uff08]([1-9][0-9]*)[)\\uff09]|\\.([1-9][0-9]*))?")
                .find(topicTitle)
                ?.run {
                    val groups = this.groups.filterNotNull()

                    if (groups.size == 2) {
                        return TopicTitleMatcher(
                            groups[1].value,
                            groups[1].value
                        )
                    }

                    if (groups.size == 3) {
                        return TopicTitleMatcher(
                            groups[1].value,
                            groups[1].value,
                            groups[2].value
                        )
                    }
                }

            return TopicTitleMatcher(
                if (topicTitle.length > 2) {
                    topicTitle.substring(0 until 2)
                } else topicTitle,
                topicTitle,
                "",
                0
            )
        }
    }
}
