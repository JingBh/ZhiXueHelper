package top.jingbh.zhixuehelper.data.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

class UserNetworkDataSource @Inject constructor(
    private val userApi: UserApi
) {
    suspend fun getUserId(token: String): String? {
        val userId = userIdsMutex.withLock {
            userIds.get(token)
        }

        return if (userId == null) {
            val result = userApi.getUserId(token)
            if (result != null) userIdsMutex.withLock {
                userIds.put(token, result)
            }

            result
        } else userId
    }

    companion object {
        private val userIdsMutex = Mutex()

        private var userIds = Cache.Builder()
            .expireAfterWrite(10.minutes)
            .build<String, String>()
    }
}

interface UserApi {
    suspend fun getUserId(token: String): String?
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class UserApiModule {
    @Binds
    abstract fun bindUserWebService(
        userWebService: UserWebService
    ): UserApi
}
