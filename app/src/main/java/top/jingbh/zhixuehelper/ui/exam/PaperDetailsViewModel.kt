package top.jingbh.zhixuehelper.ui.exam

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import top.jingbh.zhixuehelper.data.auth.UserRepository
import top.jingbh.zhixuehelper.data.exam.ExamPaper
import top.jingbh.zhixuehelper.data.exam.ExamRepository
import javax.inject.Inject

@HiltViewModel
class PaperDetailsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val examRepository: ExamRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaperDetailsUiState())

    val uiState = _uiState.asStateFlow()

    private val _paper = MutableStateFlow<ExamPaper?>(null)

    val paper = _paper.asStateFlow().filterNotNull()

    @OptIn(ExperimentalCoroutinesApi::class)
    val sheetImages = paper
        .distinctUntilChangedBy { it.id }
        .mapLatest { paper ->
            _uiState.update { state ->
                state.copy(isLoading = true)
            }

            val token = userRepository.getToken()
            val result = if (token != null) {
                examRepository.getExamPaperSheetImages(token, paper)
            } else null

            _uiState.update { state ->
                state.copy(isLoading = false)
            }

            result
        }
        .filterNotNull()

    fun initSetPaper(paper: ExamPaper) {
        if (_paper.value?.id != paper.id)
            _paper.tryEmit(paper)
    }

    data class PaperDetailsUiState(
        val isLoading: Boolean = false
    )
}
