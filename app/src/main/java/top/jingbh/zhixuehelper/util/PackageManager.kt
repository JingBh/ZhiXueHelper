package top.jingbh.zhixuehelper.util

import android.content.Context
import android.content.pm.PackageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

const val PACKAGE_ZHIXUEAPP_STUDENT = "com.iflytek.elpmobile.student"
const val PACKAGE_ZHIXUEAPP_PARENT = "com.iflytek.elpmobile.smartlearning"

fun isPackageInstalled(packageManager: PackageManager, packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

@Module
@InstallIn(SingletonComponent::class)
object PackageManagerModule {
    @Provides
    fun providePackageManager(@ApplicationContext context: Context): PackageManager {
        return context.packageManager
    }
}
