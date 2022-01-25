package top.jingbh.zhixuehelper.ui.auth

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.data.auth.UserRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState = _uiState.asStateFlow()

    fun clearLoginFailure() {
        _uiState.update { state ->
            state.copy(errorMessage = null)
        }
    }

    fun updateToken(newToken: String, isImported: Boolean = false) {
        viewModelScope.launch {
            userRepository.setToken(newToken)

            _uiState.update { state ->
                state.copy(
                    isLoggedIn = false,
                    isImported = isImported,
                    errorMessage = null
                )
            }

            checkIsLoggedIn()
        }
    }

    private fun checkIsLoggedIn() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoading = true)
            }

            @StringRes var errorMessage: Int? = null
            val newToken = userRepository.getToken()

            if (newToken != null) {
                try {
                    val jwt = JWT(newToken)

                    if (jwt.isExpired(600)) {
                        errorMessage = if (_uiState.value.isImported) {
                            R.string.login_failed_expired_imported
                        } else R.string.login_failed_expired
                    }
                } catch (e: DecodeException) {
                    errorMessage = R.string.login_failed_format
                }
            } else R.string.login_failed_null

            var result = false
            if (errorMessage == null) {
                result = userRepository.isLoggedIn()
                if (!result) errorMessage = R.string.login_failed_help
            }

            _uiState.update { state ->
                state.copy(
                    isLoggedIn = result,
                    errorMessage = errorMessage
                )
            }

            _uiState.update { state ->
                state.copy(isLoading = false)
            }
        }
    }

    data class LoginUiState(
        val isLoading: Boolean = false,
        val isImported: Boolean = false,
        val isLoggedIn: Boolean = false,
        @StringRes val errorMessage: Int? = null
    )
}
