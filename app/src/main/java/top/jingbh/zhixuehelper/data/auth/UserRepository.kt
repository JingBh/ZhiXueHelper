package top.jingbh.zhixuehelper.data.auth

import javax.inject.Inject

class UserRepository @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val cookieRepository: CookieRepository,
    private val userNetworkDataSource: UserNetworkDataSource
) {
    suspend fun getToken() = tokenRepository.getToken()

    suspend fun setToken(newToken: String) = tokenRepository.setToken(newToken)

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
}
