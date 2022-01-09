package top.jingbh.zhixuehelper.ui.auth

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import top.jingbh.zhixuehelper.BuildConfig
import top.jingbh.zhixuehelper.R
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MethodImportViewModel @Inject constructor(

) : ViewModel() {
    private val isCheckingPermissions = MutableLiveData(false)

    fun isCheckingPermissions(): LiveData<Boolean> = isCheckingPermissions

    private val checkPermissionStatus = MutableLiveData<Boolean?>(null)

    fun getCheckPermissionStatus(): LiveData<Boolean?> = checkPermissionStatus

    private val needsRequestPermission = MutableLiveData(false)

    fun needsRequestPermission(): LiveData<Boolean> = needsRequestPermission

    private val failedMessage = MutableLiveData<Int?>(null)

    fun getFailedMessage(): LiveData<Int?> = failedMessage

    fun setFailedMessage(@StringRes newMessage: Int) {
        failedMessage.value = newMessage
    }

    private val token = MutableLiveData<String>()

    fun getToken(): LiveData<String> = token

    private val userDir = MutableLiveData<String>()

    fun getUserDir(): LiveData<String> = userDir

    fun checkPermissions(myDataDirPath: String?, packageName: String) {
        isCheckingPermissions.value = true
        checkPermissionStatus.value = null
        needsRequestPermission.value = false
        failedMessage.value = null

        if (myDataDirPath != null) {
            try {
                val dataDir = File(myDataDirPath.replace(BuildConfig.APPLICATION_ID, packageName))

                val userDir = dataDir.resolve("files/iflytek/${packageName}/user/")
                this.userDir.value = userDir.absolutePath

                if (dataDir.canRead()) {
                    val file = userDir.resolve("user")

                    Log.d(TAG, "File path: $file")

                    if (file.exists()) {
                        try {
                            token.value = parseUserFile(file)

                            checkPermissionStatus.value = true
                            isCheckingPermissions.value = false

                            return
                        } catch (e: JSONException) {
                            failedMessage.value = R.string.import_fail_json
                            Log.e(TAG, "Parse user file failed", e)
                        }
                    } else {
                        failedMessage.value = R.string.import_fail_not_found
                        Log.i(TAG, "File does not exists")
                    }
                } else needsRequestPermission.value = true
            } catch (e: SecurityException) {
                failedMessage.value = R.string.import_fail_system
                Log.e(TAG, "Check permissions failed", e)
            }
        } else failedMessage.value = R.string.import_fail_not_found

        checkPermissionStatus.value = false
        isCheckingPermissions.value = false
    }

    private fun parseUserFile(file: File): String {
        val json = JSONTokener(file.readText()).nextValue() as JSONObject
        return json.getString("token")
    }

    companion object {
        private const val TAG = "MethodImportViewModel"
    }
}
