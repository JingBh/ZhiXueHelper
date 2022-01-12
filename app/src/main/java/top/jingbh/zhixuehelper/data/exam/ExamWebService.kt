package top.jingbh.zhixuehelper.data.exam

import android.util.Log
import com.android.volley.Request
import top.jingbh.zhixuehelper.data.util.CustomRequestQueue
import top.jingbh.zhixuehelper.data.util.Pagination
import top.jingbh.zhixuehelper.data.util.ZhiXueRequest
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ExamWebService @Inject constructor(
    private val requestQueue: CustomRequestQueue
) : ExamApi {
    override suspend fun getExamList(
        token: String,
        pageIndex: Int,
        pageSize: Int
    ): Pagination<Exam> = suspendCoroutine { continuation ->
        val request = ZhiXueRequest(token, Request.Method.POST, {
            appendPath("zhixuebao")
            appendPath("report")
            appendPath("getPageAllExamList")
            appendQueryParameter("reportType", "exam")
            appendQueryParameter("pageIndex", pageIndex.toString())
            appendQueryParameter("pageSize", pageSize.toString())
        }, null, { response ->
            val data = response.result?.optJSONArray("examInfoList")?.let { jsonData ->
                val result = arrayListOf<Exam>()

                for (i in 0 until jsonData.length()) {
                    val jsonExam = jsonData.optJSONObject(i)
                    result.add(
                        Exam(
                            jsonExam.getString("examId"),
                            jsonExam.getString("examName"),
                            ExamType.ofString(jsonExam.getString("examType")),
                            Date(jsonExam.getLong("examCreateDateTime"))
                        )
                    )
                }

                result
            } ?: listOf()

            val totalPages = if (response.result?.getBoolean("hasNextPage") == true) {
                pageIndex + 1
            } else pageIndex

            continuation.resume(Pagination(pageIndex, data, pageSize, totalPages))
        }, { error ->
            Log.e(TAG, "Request exam list failed", error)
            throw error
        })

        requestQueue.addToRequestQueue(request)
    }

    companion object {
        private const val TAG = "ExamWebService"
    }
}
