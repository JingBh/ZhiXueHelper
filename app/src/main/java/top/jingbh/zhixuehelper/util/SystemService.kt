package top.jingbh.zhixuehelper.util

import android.content.Context
import android.os.storage.StorageManager
import android.webkit.CookieManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SystemServiceModule {
    @Provides
    fun provideStorageManager(@ApplicationContext context: Context): StorageManager {
        return context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
    }

    @Provides
    fun provideCookieManager(): CookieManager {
        return CookieManager.getInstance()
    }
}
