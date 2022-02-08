package top.jingbh.zhixuehelper.data.util.zhixue

import org.json.JSONObject

data class StringResponse(
    val errorCode: Int,
    val errorInfo: String,
    val result: String?
) {
    companion object {
        fun ofJson(jsonObject: JSONObject): StringResponse {
            val errorCode = jsonObject.optInt("errorCode", 1)

            return StringResponse(
                errorCode,
                jsonObject.optString("errorInfo", "Failed to decode JSON"),
                if (errorCode == 0) {
                    jsonObject.optString("result")
                } else null
            )
        }
    }
}
