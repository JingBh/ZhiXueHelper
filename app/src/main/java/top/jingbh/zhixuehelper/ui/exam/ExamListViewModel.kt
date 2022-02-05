package top.jingbh.zhixuehelper.ui.exam

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.data.auth.UserRepository
import top.jingbh.zhixuehelper.data.exam.ExamRepository
import top.jingbh.zhixuehelper.data.exam.ExamType
import javax.inject.Inject

@HiltViewModel
class ExamListViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val examRepository: ExamRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListExamUiState())

    val uiState = _uiState.asStateFlow()

    private val currentToken = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingFlow = currentToken
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { token ->
            Log.d(TAG, "New pager generated")
            examRepository.getPager(token).flow
        }
        .cachedIn(viewModelScope)

    fun checkLogin() {
        viewModelScope.launch {
            val result = userRepository.isLoggedIn()
            _uiState.update { state ->
                state.copy(
                    isLoggedIn = result,
                    isLoginNeeded = !result
                )
            }

            if (result) {
                val token = userRepository.getToken()
                currentToken.emit(token)
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

    fun filterExamType(type: ExamType) {
        _uiState.update { state ->
            val types = state.examTypes.toMutableSet()

            if (types.contains(type)) {
                types.remove(type)
            } else types.add(type)

            if (types.size == ExamType.values().size) {
                state.copy(examTypes = setOf())
            } else state.copy(examTypes = types.toSet())
        }
    }

    data class ListExamUiState(
        val isLoggedIn: Boolean = false,
        val isLoginNeeded: Boolean = false,
        val examTypes: Set<ExamType> = setOf()
    )

    companion object {
        private const val TAG = "ListExamViewModel"
    }
}
