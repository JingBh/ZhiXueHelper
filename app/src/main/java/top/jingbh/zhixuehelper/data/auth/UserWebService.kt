package top.jingbh.zhixuehelper.data.auth

import android.util.Log
import com.android.volley.Request
import top.jingbh.zhixuehelper.data.util.CustomRequestQueue
import top.jingbh.zhixuehelper.data.util.zhixue.CookieRequest
import top.jingbh.zhixuehelper.data.util.zhixue.StringResponse
import top.jingbh.zhixuehelper.data.util.zhixue.TokenRequest
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserWebService @Inject constructor(
    private val requestQueue: CustomRequestQueue
) : UserApi {
    override suspend fun getToken(cookie: String): String? = suspendCoroutine { continuation ->
        val request = CookieRequest(cookie, Request.Method.GET, {
            appendPath("addon")
            appendPath("error")
            appendPath("book")
            appendPath("index")
        }, null, { response ->
            val data = StringResponse.ofJson(response)

            val token = data.result.takeIf { it?.isNotBlank() == true }

            continuation.resume(token)
        }, { error ->
            Log.e(TAG, "Request token failed", error)
            continuation.resume(null)
        })

        requestQueue.addToRequestQueue(request)
    }

    override suspend fun getUserId(token: String): String? = suspendCoroutine { continuation ->
        val request = TokenRequest(token, Request.Method.GET, {
            appendPath("base")
            appendPath("common")
            appendPath("getUserInfo")
        }, null, { response ->
            val userId = response.result?.optString("userId")
                .takeIf { it?.isNotBlank() == true }

            continuation.resume(userId)
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
