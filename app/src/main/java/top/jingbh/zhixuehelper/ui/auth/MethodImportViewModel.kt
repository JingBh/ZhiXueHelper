package top.jingbh.zhixuehelper.ui.auth

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import top.jingbh.zhixuehelper.BuildConfig
import top.jingbh.zhixuehelper.R
import java.io.File

class MethodImportViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MethodImportUiState())

    val uiState = _uiState.asStateFlow()

    fun setFailedMessage(@StringRes newMessage: Int) {
        _uiState.update { state ->
            state.copy(failedMessage = newMessage)
        }
    }

    private val _token = MutableStateFlow<String?>(null)

    val token = _token.asStateFlow()

    var userDir: String? = null

    fun checkPermissions(myDataDirPath: String?, packageName: String) {
        _uiState.update { state ->
            state.copy(
                isCheckingPermissions = true,
                checkPermissionStatus = null,
                needsRequestPermission = false,
                failedMessage = null
            )
        }

        if (myDataDirPath != null) {
            try {
                val dataDir = File(myDataDirPath.replace(BuildConfig.APPLICATION_ID, packageName))

                val userDir = dataDir.resolve("files/iflytek/${packageName}/user/")
                this.userDir = userDir.absolutePath

                if (dataDir.canRead()) {
                    val file = userDir.resolve("user")

                    Log.d(TAG, "File path: $file")

                    if (file.exists()) {
                        try {
                            _token.update {
                                parseUserFile(file)
                            }

                            _uiState.update { state ->
                                state.copy(
                                    checkPermissionStatus = true,
                                    isCheckingPermissions = false
                                )
                            }

                            return
                        } catch (e: JSONException) {
                            setFailedMessage(R.string.import_fail_json)
                            Log.e(TAG, "Parse user file failed", e)
                        }
                    } else {
                        setFailedMessage(R.string.import_fail_not_found)
                        Log.i(TAG, "File does not exists")
                    }
                } else _uiState.update { state ->
                    state.copy(needsRequestPermission = true)
                }
            } catch (e: SecurityException) {
                setFailedMessage(R.string.import_fail_system)
                Log.e(TAG, "Check permissions failed", e)
            }
        } else setFailedMessage(R.string.import_fail_not_found)

        _uiState.update { state ->
            state.copy(
                checkPermissionStatus = false,
                isCheckingPermissions = false
            )
        }
    }

    private fun parseUserFile(file: File): String {
        val json = JSONTokener(file.readText()).nextValue() as JSONObject
        return json.getString("token")
    }

    data class MethodImportUiState(
        val isCheckingPermissions: Boolean = false,
        val checkPermissionStatus: Boolean? = null,
        val needsRequestPermission: Boolean = false,
        @StringRes val failedMessage: Int? = null
    )

    companion object {
        private const val TAG = "MethodImportViewModel"
    }
}
