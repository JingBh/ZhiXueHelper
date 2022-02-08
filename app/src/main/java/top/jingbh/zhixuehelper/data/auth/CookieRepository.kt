package top.jingbh.zhixuehelper.data.auth

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import top.jingbh.zhixuehelper.BuildConfig
import top.jingbh.zhixuehelper.data.util.AuthDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CookieRepository @Inject constructor(
    @AuthDataStore private val dataStore: DataStore<Preferences>
) {
    private val mutex = Mutex()

    private var cookie: String? = null

    suspend fun getCookie(): String? {
        var currentCookie = mutex.withLock {
            cookie
        }

        if (currentCookie == null) {
            currentCookie = dataStore.data.first()[KEY_COOKIE]
            mutex.withLock {
                cookie = currentCookie
            }

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "our cookie: $currentCookie")
            }
        }

        return cookie
    }

    suspend fun setCookie(newCookie: String) {
        mutex.withLock {
            cookie = newCookie
        }

        Log.d(TAG, "new cookie set: $newCookie")

        dataStore.edit { data ->
            data[KEY_COOKIE] = newCookie
        }
    }

    suspend fun clearCookie() {
        mutex.withLock {
            cookie = null
        }

        Log.d(TAG, "cookie cleared")

        dataStore.edit { data ->
            data.remove(KEY_COOKIE)
        }
    }

    companion object {
        private const val TAG = "CookieRepository"
    }
}
