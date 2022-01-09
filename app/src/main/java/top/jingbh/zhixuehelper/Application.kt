package top.jingbh.zhixuehelper

import android.app.Application
import android.webkit.CookieManager
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@Suppress("unused")
@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        CookieManager.getInstance().setAcceptCookie(true)

        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
