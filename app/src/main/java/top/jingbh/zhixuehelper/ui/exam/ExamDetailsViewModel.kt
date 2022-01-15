package top.jingbh.zhixuehelper.ui.exam

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val papers = exam
        .distinctUntilChanged()
        .mapLatest { exam ->
            _uiState.update { state ->
                state.copy(isLoading = true)
            }

            val token = userRepository.getToken()
            val result = if (token != null) {
                examRepository.getExamPaperList(token, exam)
            } else null

            _uiState.update { state ->
                state.copy(isLoading = false)
            }

            result
        }
        .filterNotNull()

    fun initSetExam(exam: Exam) {
        if (_exam.value?.equals(exam) != true)
            _exam.tryEmit(exam)
    }

    data class ExamDetailsUiState(
        val isLoading: Boolean = false
    )
}
