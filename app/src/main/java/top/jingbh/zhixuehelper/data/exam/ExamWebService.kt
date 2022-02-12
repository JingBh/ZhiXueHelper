package top.jingbh.zhixuehelper.data.exam

import android.net.Uri
import android.util.Log
import com.android.volley.Request
import org.json.JSONArray
import org.json.JSONTokener
import top.jingbh.zhixuehelper.data.subject.Subject
import top.jingbh.zhixuehelper.data.util.CustomRequestQueue
import top.jingbh.zhixuehelper.data.util.Pagination
import top.jingbh.zhixuehelper.data.util.zhixue.TokenRequest
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ExamWebService @Inject constructor(
    private val requestQueue: CustomRequestQueue
) : ExamApi {
    override suspend fun getExamList(
        token: String,
        pageIndex: Int,
        pageSize: Int
    ): Pagination<Exam> = suspendCoroutine { continuation ->
        val request = TokenRequest(token, Request.Method.POST, {
            appendPath("report")
            appendPath("getPageAllExamList")
            appendQueryParameter("reportType", "exam")
            appendQueryParameter("pageIndex", pageIndex.toString())
            appendQueryParameter("pageSize", pageSize.toString())
        }, null, { response ->
            val data = response.result?.optJSONArray("examInfoList") ?: JSONArray()

            val result = arrayListOf<Exam>()

            for (i in 0 until data.length()) {
                val jsonExam = data.getJSONObject(i)
                result.add(
                    Exam(
                        jsonExam.getString("examId"),
                        jsonExam.getString("examName"),
                        ExamType.ofString(jsonExam.getString("examType")),
                        Date(jsonExam.getLong("examCreateDateTime"))
                    )
                )
            }

            val totalPages = if (response.result?.getBoolean("hasNextPage") == true) {
                pageIndex + 1
            } else pageIndex

            continuation.resume(Pagination(pageIndex, result, pageSize, totalPages))
        }, { error ->
            Log.e(TAG, "Request exam list failed", error)
            continuation.resumeWithException(error)
        })

        requestQueue.addToRequestQueue(request)
    }

    override suspend fun getExamPaperList(
        token: String,
        exam: Exam
    ): List<ExamPaper> = suspendCoroutine { continuation ->
        val request = TokenRequest(token, Request.Method.GET, {
            appendPath("report")
            appendPath("exam")
            appendPath("getReportMain")
            appendQueryParameter("examId", exam.id)
        }, null, { response ->
            val data = response.result?.optJSONArray("paperList") ?: JSONArray()

            val result = arrayListOf<ExamPaper>()

            for (i in 0 until data.length()) {
                val jsonPaper = data.getJSONObject(i)

                val userScore = jsonPaper.optDouble(
                    "preAssignScore",
                    jsonPaper.getDouble("userScore")
                )
                val userLevel = jsonPaper.optString("userLevel")

                val paper = ExamPaper(
                    jsonPaper.getString("paperId"),
                    jsonPaper.getString("title"),
                    jsonPaper.getString("paperName"),
                    Subject.ofSubjectCode(jsonPaper.getString("subjectCode")),
                    jsonPaper.getDouble("standardScore"),
                    userScore,
                    if (userLevel.isNotEmpty()) AssignedScore.valueOf(userLevel) else null
                )

                result.add(paper)
            }

            continuation.resume(result)
        }, { error ->
            Log.e(TAG, "Request exam paper list failed", error)
            continuation.resumeWithException(error)
        })

        requestQueue.addToRequestQueue(request)
    }

    override suspend fun getExamPaperSheetImages(
        token: String,
        paper: ExamPaper
    ): List<Uri> = suspendCoroutine { continuation ->
        val request = TokenRequest(token, Request.Method.GET, {
            appendPath("report")
            appendPath("paper")
            appendPath("getCheckSheet")
            appendQueryParameter("examId", "")
            appendQueryParameter("paperId", paper.id)
        }, null, { response ->
            val json = response.result?.optString("sheetImages")
                .takeIf { !it.isNullOrBlank() } ?: "[]"
            val data = JSONTokener(json).nextValue() as JSONArray

            val result = arrayListOf<Uri>()

            for (i in 0 until data.length()) {
                result.add(Uri.parse(data.getString(i)))
            }

            continuation.resume(result)
        }, { error ->
            Log.e(TAG, "Request exam paper sheet images failed", error)
            continuation.resumeWithException(error)
        })

        requestQueue.addToRequestQueue(request)
    }

    override suspend fun getExamPaperAnalysis(
        token: String,
        paper: ExamPaper
    ): List<ExamPaperTopic> = suspendCoroutine { continuation ->
        val request = TokenRequest(token, Request.Method.GET, {
            appendPath("report")
            appendPath("getPaperAnalysis")
            appendQueryParameter("paperId", paper.id)
        }, null, { response ->
            val result = arrayListOf<ExamPaperTopic>()

            val topicTypes = response.result?.optJSONArray("typeTopicAnalysis") ?: JSONArray()

            for (i in 0 until topicTypes.length()) {
                val jsonTopicType = topicTypes.getJSONObject(i)
                val topics = jsonTopicType.getJSONArray("topicAnalysisDTOs")

                for (j in 0 until topics.length()) {
                    val jsonTopic = topics.getJSONObject(j)

                    val topicId = jsonTopic.getInt("topicNumber")
                    val topicType =
                        ExamPaperTopicType.ofString(jsonTopic.optString("answerType"))

                    val topic = ExamPaperTopic(
                        topicId,
                        topicType,
                        jsonTopic.optString("dispTitle", topicId.toString()),
                        jsonTopic.optString("standardAnswer")
                            .takeIf { it.isNotBlank() },
                        when (topicType) {
                            ExamPaperTopicType.TEXT -> jsonTopic.getString("userAnswer")
                            ExamPaperTopicType.IMAGE -> jsonTopic.optString("imageAnswer", "[]")
                        },
                        jsonTopic.getDouble("standardScore"),
                        jsonTopic.getDouble("score")
                    )

                    result.add(topic)
                }
            }

            continuation.resume(result)
        }, { error ->
            Log.e(TAG, "Request exam paper analysis failed", error)
            continuation.resumeWithException(error)
        })

        requestQueue.addToRequestQueue(request)
    }

    companion object {
        private const val TAG = "ExamWebService"
    }
}
