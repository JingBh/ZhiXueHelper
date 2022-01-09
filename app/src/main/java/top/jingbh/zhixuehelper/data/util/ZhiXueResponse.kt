package top.jingbh.zhixuehelper.data.util

import org.json.JSONObject

data class ZhiXueResponse(
    val errorCode: Int,
    val errorInfo: String,
    val result: JSONObject?
) {
    companion object {
        fun ofJson(jsonObject: JSONObject): ZhiXueResponse {
            val errorCode = jsonObject.optInt("errorCode", 1)

            return ZhiXueResponse(
                jsonObject.optInt("errorCode", 1),
                jsonObject.optString("errorInfo", "Failed to decode JSON"),
                if (errorCode == 0) jsonObject.optJSONObject("result") else null
            )
        }
    }
}
