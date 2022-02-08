package top.jingbh.zhixuehelper.data.util.zhixue

import android.net.Uri
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class CookieRequest(
    private val cookie: String,
    method: Int,
    buildUrl: Uri.Builder.() -> Uri.Builder,
    body: JSONObject?,
    listener: Response.Listener<JSONObject>,
    errorListener: Response.ErrorListener?
) : JsonObjectRequest(
    method,
    BASE_URL.buildUpon().buildUrl().build().toString(),
    body,
    { listener.onResponse(it) },
    errorListener
) {
    override fun getHeaders(): MutableMap<String, String> {
        return mutableMapOf("Cookie" to "tlsysSessionId=$cookie")
    }

    companion object {
        private val BASE_URL = Uri.parse("https://www.zhixue.com/")
    }
}
