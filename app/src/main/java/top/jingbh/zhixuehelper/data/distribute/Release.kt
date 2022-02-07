package top.jingbh.zhixuehelper.data.distribute

import top.jingbh.zhixuehelper.BuildConfig

data class Release(
    val tag: String,
    val title: String?,
    val body: String?,
    val versionCode: Int,
    val versionName: String,
    val viewUrl: String,
    val downloadUrl: String
) {
    fun isNewer(): Boolean {
        return versionCode > BuildConfig.VERSION_CODE ||
            (versionCode == BuildConfig.VERSION_CODE && versionName != BuildConfig.VERSION_NAME)
    }

    fun getMarkdown(): String {
        val title = this.title ?: this.tag

        var markdown = "# $title"
        if (body != null) markdown += "\n\n${body}"

        return markdown
    }
}
