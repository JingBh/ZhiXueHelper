package top.jingbh.zhixuehelper.data.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import top.jingbh.zhixuehelper.data.auth.authDataStore
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthDataStore

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @AuthDataStore
    fun provideAuthDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.authDataStore
    }
}
