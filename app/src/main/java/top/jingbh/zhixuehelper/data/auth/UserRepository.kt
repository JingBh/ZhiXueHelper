package top.jingbh.zhixuehelper.data.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val cookieRepository: CookieRepository,
    private val userNetworkDataSource: UserNetworkDataSource
) {
    private val tokenFlow = MutableStateFlow<String?>(null)

    suspend fun getToken(): String? {
        val token = tokenRepository.getToken()
        tokenFlow.emit(token)

        return token
    }

    suspend fun setToken(newToken: String) {
        tokenRepository.setToken(newToken)
        tokenFlow.emit(newToken)
    }

    private suspend fun getCookie() = cookieRepository.getCookie()

    suspend fun setCookie(newCookie: String) {
        cookieRepository.setCookie(newCookie)

        requestToken()
    }

    suspend fun clearCookie() = cookieRepository.clearCookie()

    suspend fun isLoggedIn(): Boolean {
        val token = getToken() ?: return if (requestToken()) isLoggedIn() else false

        val userId = userNetworkDataSource.getUserId(token)
        return userId != null
    }

    private suspend fun requestToken(): Boolean {
        val cookie = getCookie()
        if (cookie != null) {
            val newToken = userNetworkDataSource.getToken(cookie)

            if (newToken != null) {
                setToken(newToken)

                return true
            } else clearCookie()
        }

        return false
    }

    fun getTokenFlow() = tokenFlow.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            getToken()
        }
    }
}
