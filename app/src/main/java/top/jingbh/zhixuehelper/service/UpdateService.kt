package top.jingbh.zhixuehelper.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.data.distribute.Release
import top.jingbh.zhixuehelper.data.distribute.ReleaseRepository
import javax.inject.Inject

@AndroidEntryPoint
class UpdateService : Service() {
    private val binder = UpdateBinder()

    @Inject
    lateinit var releaseRepository: ReleaseRepository

    private var activityContext: AppCompatActivity? = null

    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + job)

    private var isSilent = true
    private var isRunning = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isSilent = intent?.getBooleanExtra(EXTRA_MODE_SILENT, true) ?: true

        doCheckUpdate()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        activityContext = null

        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
        isRunning = false
    }

    private fun doCheckUpdate() {
        if (isRunning) return
        isRunning = true

        serviceScope.launch {
            val result = releaseRepository.getLatestRelease()
            val release = result.data

            if (release != null) {
                if (release.isNewer()) {
                    notifyRelease(release)
                } else {
                    notifyLatest()
                }
            } else {
                if (result.message == R.string.update_latest) {
                    notifyLatest()
                } else {
                    notifyFailed(result.message)
                }
            }

            isRunning = false
            stopSelf()
        }
    }

    private fun notifyRelease(release: Release) {
        val context = activityContext ?: return
        val markwon = Markwon.create(context)

        MaterialAlertDialogBuilder(context)
            .setIcon(R.drawable.ic_round_update_24)
            .setTitle(R.string.update_found_title)
            .setMessage(markwon.toMarkdown(release.getMarkdown()))
            .setNeutralButton(R.string.view) { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = Uri.parse(release.viewUrl)

                startActivity(intent)
            }
            .setNegativeButton(R.string.ignore, null)
            .setPositiveButton(R.string.download) { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = Uri.parse(release.downloadUrl)

                startActivity(intent)
            }
            .show()
    }

    private fun notifyLatest() {
        if (isSilent) return

        Toast.makeText(this, R.string.update_latest, Toast.LENGTH_SHORT)
            .show()
    }

    private fun notifyFailed(@StringRes message: Int = R.string.update_failed) {
        if (isSilent) return
        val context = activityContext ?: return

        MaterialAlertDialogBuilder(context)
            .setIcon(R.drawable.ic_round_update_24)
            .setTitle(R.string.update_failed_title)
            .setMessage(message)
            .setPositiveButton(R.string.okay, null)
            .show()
    }

    inner class UpdateBinder : Binder() {
        fun checkUpdate(activityContext: AppCompatActivity, silent: Boolean = true) {
            this@UpdateService.activityContext = activityContext

            val intent = Intent(activityContext, UpdateService::class.java)
            intent.putExtra(EXTRA_MODE_SILENT, silent)

            startService(intent)
        }
    }

    companion object {
        const val EXTRA_MODE_SILENT = "top.jingbh.zhixuehelper.extra.MODE_SILENT"
    }
}
