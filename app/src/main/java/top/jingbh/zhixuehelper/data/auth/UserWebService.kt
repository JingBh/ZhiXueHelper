package top.jingbh.zhixuehelper.data.auth

import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import top.jingbh.zhixuehelper.data.util.CustomRequestQueue
import top.jingbh.zhixuehelper.data.util.ZhiXueResponse
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserWebService @Inject constructor(
    private val requestQueue: CustomRequestQueue
) : UserApi {
    override suspend fun getUserId(token: String): String? = suspendCoroutine { continuation ->
        val request = object : JsonObjectRequest(URL_BASE_USER_INFO, { result ->
            val response = ZhiXueResponse.ofJson(result)
            val userId = response.result?.optString("userId")
            Log.d(TAG, "Got userId $userId")
            continuation.resume(if (userId.isNullOrBlank()) null else userId)
        }, { error ->
            Log.e(TAG, "Request userId failed", error)
            continuation.resume(null)
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("XToken" to token)
            }
        }

        requestQueue.addToRequestQueue(request)
    }

    companion object {
        private const val TAG = "UserWebService"

        private const val URL_BASE_USER_INFO =
            "https://www.zhixue.com/zhixuebao/base/common/getUserInfo"
    }
}
