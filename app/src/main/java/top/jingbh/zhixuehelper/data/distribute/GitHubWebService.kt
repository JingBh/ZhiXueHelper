package top.jingbh.zhixuehelper.data.distribute

import android.net.Uri
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import top.jingbh.zhixuehelper.data.util.CustomRequestQueue
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GitHubWebService @Inject constructor(
    private val requestQueue: CustomRequestQueue
) : ReleaseApi {
    override suspend fun getLatestRelease(): Release = suspendCoroutine { continuation ->
        val releaseRequest = JsonObjectRequest(buildApiUrl {
            appendPath("releases").appendPath("latest")
        }, { releaseJson ->
            val assetsJson = releaseJson.getJSONArray("assets")

            var metadataUrl = ""
            var downloadUrl = ""

            for (i in 0 until assetsJson.length()) {
                val asset = assetsJson.getJSONObject(i)

                val name = asset.getString("name")
                val url = asset.getString("browser_download_url")

                if (name == "output-metadata.json") {
                    metadataUrl = url
                } else if (name.endsWith(".apk")) {
                    downloadUrl = url
                }
            }

            if (metadataUrl.isBlank() || downloadUrl.isBlank())
                continuation.resumeWithException(Exception("NO_ASSETS"))

            val metadataRequest = JsonObjectRequest(metadataUrl, { metadataJson ->
                val artifactJson = metadataJson.getJSONArray("elements")
                    .getJSONObject(0)

                val release = Release(
                    releaseJson.getString("tag_name"),
                    releaseJson.optString("name").takeIf { it.isNotBlank() },
                    releaseJson.optString("body").takeIf { it.isNotBlank() },
                    artifactJson.getInt("versionCode"),
                    artifactJson.getString("versionName"),
                    releaseJson.getString("html_url"),
                    downloadUrl
                )

                continuation.resume(release)
            }, { error ->
                Log.e(TAG, error.stackTraceToString())

                continuation.resumeWithException(error)
            })

            requestQueue.addToRequestQueue(metadataRequest)
        }, { error ->
            if (error.networkResponse?.statusCode == 404) {
                Log.w(TAG, "release not found")
            } else Log.e(TAG, error.stackTraceToString())

            continuation.resumeWithException(error)
        })

        requestQueue.addToRequestQueue(releaseRequest)
    }

    companion object {
        private const val TAG = "GitHubWebService"

        private const val webBaseUrl = "https://github.com/"
        private const val apiBaseUrl = "https://api.github.com/"

        private const val owner = "JingBh"
        private const val repository = "ZhiXueHelper"

        private fun buildApiUrl(buildUrl: Uri.Builder.() -> Uri.Builder): String {
            return Uri.parse(apiBaseUrl).buildUpon()
                .appendPath("repos")
                .appendPath(owner)
                .appendPath(repository)
                .buildUrl()
                .toString()
        }

        fun getWebUrl(): String {
            return Uri.parse(webBaseUrl).buildUpon()
                .appendPath(owner)
                .appendPath(repository)
                .toString()
        }
    }
}
