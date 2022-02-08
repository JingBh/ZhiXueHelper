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
class TokenRepository @Inject constructor(
    @AuthDataStore private val dataStore: DataStore<Preferences>
) {
    private val mutex = Mutex()

    private var token: String? = null

    suspend fun getToken(): String? {
        var currentToken = mutex.withLock {
            token
        }

        if (currentToken == null) {
            currentToken = dataStore.data.first()[KEY_TOKEN]
            mutex.withLock {
                token = currentToken
            }

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "our token: $currentToken")
            }
        }

        return token
    }

    suspend fun setToken(newToken: String) {
        mutex.withLock {
            token = newToken
        }

        Log.d(TAG, "new token set: $newToken")

        dataStore.edit { data ->
            data[KEY_TOKEN] = newToken
        }
    }

    companion object {
        private const val TAG = "TokenRepository"
    }
}
