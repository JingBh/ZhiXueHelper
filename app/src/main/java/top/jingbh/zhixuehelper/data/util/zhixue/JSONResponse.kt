package top.jingbh.zhixuehelper.data.util.zhixue

import org.json.JSONObject
import org.json.JSONTokener

data class JSONResponse(
    val errorCode: Int,
    val errorInfo: String,
    val result: JSONObject?
) {
    companion object {
        fun ofJson(jsonObject: JSONObject): JSONResponse {
            val errorCode = jsonObject.optInt("errorCode", 1)

            return JSONResponse(
                errorCode,
                jsonObject.optString("errorInfo", "Failed to decode JSON"),
                if (errorCode == 0) {
                    val result = jsonObject.optJSONObject("result")
                    if (result == null) {
                        val resultJson = jsonObject.optString("result")
                        if (resultJson.isNotBlank()) {
                            JSONTokener(resultJson).nextValue() as JSONObject
                        } else null
                    } else result
                } else null
            )
        }
    }
}
