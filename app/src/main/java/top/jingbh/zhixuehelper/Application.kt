package top.jingbh.zhixuehelper

import android.app.Application
import android.webkit.CookieManager
import com.android.volley.VolleyLog
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@Suppress("unused")
@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        initDynamicColors()
        initWebViews()
        initDebug()
    }

    private fun initDynamicColors() {
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    private fun initWebViews() {
        CookieManager.getInstance().setAcceptCookie(true)
    }

    private fun initDebug() {
        if (BuildConfig.DEBUG) {
            VolleyLog.DEBUG = true
        }
    }
}
