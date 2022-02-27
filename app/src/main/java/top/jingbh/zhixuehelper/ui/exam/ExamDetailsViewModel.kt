package top.jingbh.zhixuehelper.ui.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import top.jingbh.zhixuehelper.data.auth.UserRepository
import top.jingbh.zhixuehelper.data.exam.Exam
import top.jingbh.zhixuehelper.data.exam.ExamRepository
import javax.inject.Inject

@HiltViewModel
class ExamDetailsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val examRepository: ExamRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExamDetailsUiState())

    val uiState = _uiState.asStateFlow()

    private val _exam = MutableStateFlow<Exam?>(null)

    val exam = _exam.asStateFlow().filterNotNull()

    private val tokenFlow = userRepository.getTokenFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val papers = exam
        .distinctUntilChangedBy { it.id }
        .mapLatest { exam ->
            _uiState.update { state ->
                state.copy(isLoading = true)
            }

            val token = tokenFlow.value
            val result = if (token != null) {
                try {
                    examRepository.getExamPaperList(token, exam)
                } catch (e: Exception) {
                    _uiState.update { state ->
                        state.copy(isFailed = true)
                    }

                    null
                }
            } else null

            _uiState.update { state ->
                state.copy(isLoading = false)
            }

            result
        }
        .filterNotNull()
        .shareIn(viewModelScope, Lazily, 1)

    fun initSetExam(exam: Exam) {
        if (_exam.value?.id != exam.id)
            _exam.tryEmit(exam)
    }

    data class ExamDetailsUiState(
        val isLoading: Boolean = false,
        val isFailed: Boolean = false
    )
}
