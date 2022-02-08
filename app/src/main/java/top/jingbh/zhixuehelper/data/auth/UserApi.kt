package top.jingbh.zhixuehelper.data.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

interface UserApi {
    suspend fun getUserId(token: String): String?
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class UserApiModule {
    @Binds
    abstract fun bindUserApi(
        userWebService: UserWebService
    ): UserApi
}
