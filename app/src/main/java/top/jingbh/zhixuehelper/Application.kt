package top.jingbh.zhixuehelper

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.android.volley.VolleyLog
import com.google.android.material.color.DynamicColors
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import dagger.hilt.android.HiltAndroidApp
import top.jingbh.zhixuehelper.ui.misc.AgreementsActivity
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
        initAppCenter()
        initDynamicColors()
        initDebug()
    }

    private fun initAgreements() {
        this.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) =
                verifyAgreed(activity)

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) = verifyAgreed(activity)

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}

            private fun verifyAgreed(activity: Activity) {
                val isAgreementsActivity =
                    activity::class.qualifiedName == AgreementsActivity::class.qualifiedName
                if (!isAgreementsActivity && !agreements.isAgreementsAgreed()) {
                    val intent = Intent(activity, AgreementsActivity::class.java)
                    val flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.addFlags(flags)
                    startActivity(intent)

                    activity.finish()
                }
            }
        })
    }

    private fun initAppCenter() {
        AppCenter.configure(this, "d87cf6b8-ef94-4741-a14a-5c31e1ab0037")

        if (!BuildConfig.DEBUG) {
            AppCenter.start(Crashes::class.java)
            AppCenter.start(Analytics::class.java)
        }

        Analytics.setEnabled(agreements.isAgreementsAgreed())
    }

    private fun initDynamicColors() {
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    private fun initDebug() {
        if (BuildConfig.DEBUG) {
            VolleyLog.DEBUG = true
        }
    }
}
