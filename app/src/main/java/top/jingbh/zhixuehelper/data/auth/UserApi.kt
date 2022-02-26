package top.jingbh.zhixuehelper.data.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

interface UserApi {
    suspend fun getToken(cookie: String): String?

    suspend fun getUserId(token: String): String?
}

@Module
@InstallIn(SingletonComponent::class)
abstract class UserApiModule {
    @Binds
    abstract fun bindUserApi(
        userWebService: UserWebService
    ): UserApi
}
