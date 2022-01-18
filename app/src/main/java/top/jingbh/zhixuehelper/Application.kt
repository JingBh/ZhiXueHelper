package top.jingbh.zhixuehelper

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import com.android.volley.VolleyLog
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import top.jingbh.zhixuehelper.ui.page.AgreementsActivity
import top.jingbh.zhixuehelper.ui.util.Agreements
import javax.inject.Inject

@Suppress("unused")
@HiltAndroidApp
class Application : Application() {
    @Inject
    lateinit var agreements: Agreements

    override fun onCreate() {
        super.onCreate()

        initAgreements()
        initDynamicColors()
        initWebViews()
        initDebug()
    }

    private fun initAgreements() {
        this.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
                val isAgreementsActivity =
                    activity::class.qualifiedName == AgreementsActivity::class.qualifiedName
                if (!agreements.isAgreementsAgreed() && !isAgreementsActivity) {
                    val intent = Intent(this@Application, AgreementsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                    activity.finish()
                }
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
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
