package top.jingbh.zhixuehelper.initializer

import android.content.Context
import android.webkit.CookieManager
import androidx.startup.Initializer

class CookieInitializer : Initializer<CookieManager> {
    override fun create(context: Context): CookieManager {
        return CookieManager.getInstance().apply {
            acceptCookie()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
