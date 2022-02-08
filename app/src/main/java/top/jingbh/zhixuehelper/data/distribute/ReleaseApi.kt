package top.jingbh.zhixuehelper.data.distribute

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

interface ReleaseApi {
    suspend fun getLatestRelease(): Release
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ReleaseApiModule {
    @Binds
    abstract fun bindReleaseApi(
        gitHubWebService: GitHubWebService
    ): ReleaseApi
}
