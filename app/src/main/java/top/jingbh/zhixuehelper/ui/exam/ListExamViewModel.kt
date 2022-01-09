package top.jingbh.zhixuehelper.ui.exam

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
class ListExamViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val checkIsLoggedInUseCase: CheckIsLoggedInUseCase
) : ViewModel() {
    private val isLoading = MutableLiveData(false)

    fun isLoading(): LiveData<Boolean> = isLoading

    // TODO: Replace with list.count == 0
    private val isLoaded = MutableLiveData(false)

    fun isLoaded(): LiveData<Boolean> = isLoaded

    private val isLoginNeeded = MutableLiveData(false)

    fun isLoginNeeded(): LiveData<Boolean> = isLoginNeeded

    fun reload() {
        isLoading.value = true
        isLoaded.value = false

        checkIsLoggedInUseCase(viewModelScope) { result ->
            isLoginNeeded.value = !result
        }

        viewModelScope.launch {
            val token = userRepository.getToken()

            if (token != null) {
                // TODO
            } else {
                isLoading.value = false
                isLoaded.value = false
            }
        }
    }

    fun wentToLogin() {
        isLoading.value = false
        isLoaded.value = false
        isLoginNeeded.value = false
    }
}
