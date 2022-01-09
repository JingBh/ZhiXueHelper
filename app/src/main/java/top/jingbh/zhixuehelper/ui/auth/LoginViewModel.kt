package top.jingbh.zhixuehelper.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.data.auth.UserRepository
import top.jingbh.zhixuehelper.domain.auth.CheckIsLoggedInUseCase
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val checkIsLoggedInUseCase: CheckIsLoggedInUseCase
) : ViewModel() {
    private val isLoading = MutableLiveData(false)

    fun isLoading(): LiveData<Boolean> = isLoading

    fun setLoading(isLoading: Boolean) {
        this.isLoading.value = isLoading
    }

    private val isLoggedIn = MutableLiveData(false)

    fun isLoggedIn(): LiveData<Boolean> = isLoggedIn

    private val isLoginFailed = MutableLiveData(false)

    fun isLoginFailed(): LiveData<Boolean> = isLoginFailed

    fun clearLoginFailed() {
        isLoginFailed.value = false
    }

    fun updateToken(newToken: String) {
        viewModelScope.launch {
            userRepository.setToken(newToken)

            isLoggedIn.value = false
            clearLoginFailed()

            checkIsLoggedIn()
        }
    }

    private fun checkIsLoggedIn() {
        setLoading(true)

        checkIsLoggedInUseCase(viewModelScope) { result ->
            isLoggedIn.value = result
            isLoginFailed.value = !result

            setLoading(false)
        }
    }
}
