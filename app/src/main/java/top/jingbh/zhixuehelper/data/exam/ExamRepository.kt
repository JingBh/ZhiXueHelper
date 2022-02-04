package top.jingbh.zhixuehelper.data.exam

import androidx.paging.Pager
import androidx.paging.PagingConfig
import javax.inject.Inject

class ExamRepository @Inject constructor(
    private val examPagingSourceFactory: ExamPagingSourceFactory,
    private val sheetImageRepository: SheetImageRepository,
    private val examApi: ExamApi
) {
    fun getPager(token: String) = Pager(
        config = PagingConfig(pageSize = 10)
    ) { examPagingSourceFactory.create(token) }

    suspend fun getExamPaperList(token: String, exam: Exam) =
        examApi.getExamPaperList(token, exam)

    suspend fun getExamPaperSheetImages(token: String, paper: ExamPaper) =
        sheetImageRepository.getSheetImages(token, paper)

    suspend fun getExamPaperAnalysis(token: String, paper: ExamPaper) =
        examApi.getExamPaperAnalysis(token, paper)
}
