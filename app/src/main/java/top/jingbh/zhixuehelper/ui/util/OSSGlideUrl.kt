package top.jingbh.zhixuehelper.ui.util

import android.net.Uri
import com.bumptech.glide.load.model.GlideUrl

class OSSGlideUrl private constructor(private val uri: Uri) : GlideUrl(uri.toString()) {
    override fun getCacheKey(): String {
        var builder = uri.buildUpon().clearQuery()

        uri.getQueryParameter("x-oss-process")?.let {
            builder = builder.appendQueryParameter("x-oss-process", it)
        }

        return builder.toString()
    }

    companion object {
        fun of(url: String): OSSGlideUrl {
            val uri = Uri.parse(url)
            return of(uri)
        }

        fun of(uri: Uri): OSSGlideUrl {
            return OSSGlideUrl(uri)
        }
    }
}
