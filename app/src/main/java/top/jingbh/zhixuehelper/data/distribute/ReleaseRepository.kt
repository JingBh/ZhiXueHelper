package top.jingbh.zhixuehelper.data.distribute

import androidx.annotation.StringRes
import com.android.volley.VolleyError
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import top.jingbh.zhixuehelper.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReleaseRepository @Inject constructor(
    private val releaseApi: ReleaseApi
) {
    private val mutex = Mutex()

    private var cache: Result? = null
    private var cacheTime = 0L

    suspend fun getLatestRelease(): Result {
        val cachedResult: Result? = mutex.withLock {
            cache.takeIf { cacheTime + CACHE_EXPIRY > System.currentTimeMillis() }
        }

        if (cachedResult == null || cachedResult.message == R.string.update_failed) {
            val newResult: Result = try {
                Result(releaseApi.getLatestRelease())
            } catch (e: VolleyError) {
                if (e.networkResponse?.statusCode == 404) {
                    Result(null, R.string.update_latest)
                } else {
                    Result(null, R.string.update_failed)
                }
            } catch (e: Exception) {
                if (e.message == "NO_ASSETS") {
                    Result(null, R.string.update_no_assets)
                } else {
                    Result(null, R.string.update_failed)
                }
            }

            mutex.withLock {
                cache = newResult
                cacheTime = System.currentTimeMillis()
            }

            return newResult
        } else return cachedResult
    }

    data class Result(
        val data: Release?,
        @StringRes val message: Int = 0
    )

    companion object {
        private const val CACHE_EXPIRY = 1000L * 60 // 1 minute
    }
}
