package top.jingbh.zhixuehelper.data.auth

import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val userNetworkDataSource: UserNetworkDataSource
) {
    private val tokenMutex = Mutex()

    private var token: String? = null

    suspend fun getToken(): String? {
        var currentToken = tokenMutex.withLock {
            token
        }

        if (currentToken == null) {
            currentToken = tokenLocalDataSource.fetchToken()
            tokenMutex.withLock {
                token = currentToken
            }

        }

        return token
    }

    suspend fun setToken(newToken: String) {
        tokenMutex.withLock {
            token = newToken
        }

        Log.d(TAG, "New token set: $newToken")

        tokenLocalDataSource.putToken(newToken)
    }

    suspend fun isLoggedIn(): Boolean {
        getToken()
        val token = tokenMutex.withLock {
            token
        } ?: return false

        val userId = userNetworkDataSource.getUserId(token)
        return userId != null
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}
