package top.jingbh.zhixuehelper.data.util

import android.net.Uri
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class ZhiXueRequest(
    private val token: String,
    method: Int,
    buildUrl: Uri.Builder.() -> Uri.Builder,
    body: JSONObject?,
    listener: Response.Listener<ZhiXueResponse>,
    errorListener: Response.ErrorListener?
) : JsonObjectRequest(
    method,
    BASE_URL.buildUpon().buildUrl().build().toString(),
    body,
    { listener.onResponse(ZhiXueResponse.ofJson(it)) },
    errorListener
) {
    override fun getHeaders(): MutableMap<String, String> {
        return mutableMapOf("XToken" to token)
    }

    companion object {
        private val BASE_URL = Uri.parse("https://www.zhixue.com/")
    }
}
