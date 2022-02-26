package top.jingbh.zhixuehelper.ui.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.data.auth.UserRepository
import javax.inject.Inject

@HiltViewModel
class ExamContainerViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExamContainerUiState())

    val uiState = _uiState.asStateFlow()

    fun checkLogin() {
        viewModelScope.launch {
            val result = userRepository.isLoggedIn()
            _uiState.update { state ->
                state.copy(
                    isLoggedIn = result,
                    isLoginNeeded = !result
                )
            }
        }
    }

    fun wentToLogin() {
        _uiState.update { state ->
            state.copy(
                isLoggedIn = false,
                isLoginNeeded = false
            )
        }
    }

    data class ExamContainerUiState(
        val isLoggedIn: Boolean = false,
        val isLoginNeeded: Boolean = false,
        val isError: Boolean = false,
        val isNoNetwork: Boolean = false
    )
}
