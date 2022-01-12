package top.jingbh.zhixuehelper.data.auth

import android.util.Log
import com.android.volley.Request
import top.jingbh.zhixuehelper.data.util.CustomRequestQueue
import top.jingbh.zhixuehelper.data.util.ZhiXueRequest
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserWebService @Inject constructor(
    private val requestQueue: CustomRequestQueue
) : UserApi {
    override suspend fun getUserId(token: String): String? = suspendCoroutine { continuation ->
        val request = ZhiXueRequest(token, Request.Method.GET, {
            appendPath("zhixuebao")
            appendPath("base")
            appendPath("common")
            appendPath("getUserInfo")
        }, null, { response ->
            val userId = response.result?.optString("userId")
            continuation.resume(if (userId.isNullOrBlank()) null else userId)
        }, { error ->
            Log.e(TAG, "Request userId failed", error)
            continuation.resume(null)
        })

        requestQueue.addToRequestQueue(request)
    }

    companion object {
        private const val TAG = "UserWebService"
    }
}
