package top.jingbh.zhixuehelper.data.exam

import android.net.Uri
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

class SheetImageRepository @Inject constructor(
    private val examApi: ExamApi
) {
    suspend fun getSheetImages(token: String, paper: ExamPaper): List<Uri> {
        val key = "$token-${paper.id}"
        var images = mutex.withLock {
            cache.get(key)
        }

        if (images == null) {
            images = examApi.getExamPaperSheetImages(token, paper)

            mutex.withLock {
                cache.put(key, images)
            }
        }

        return images
    }

    companion object {
        private val mutex = Mutex()

        private var cache = Cache.Builder()
            .expireAfterWrite(50.minutes)
            .build<String, List<Uri>>()
    }
}
