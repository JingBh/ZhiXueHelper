package top.jingbh.zhixuehelper.data.exam

import android.net.Uri
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import top.jingbh.zhixuehelper.data.util.Pagination

interface ExamApi {
    suspend fun getExamList(token: String, pageIndex: Int, pageSize: Int): Pagination<Exam>

    suspend fun getExamPaperList(token: String, exam: Exam): List<ExamPaper>

    suspend fun getExamPaperSheetImages(token: String, paper: ExamPaper): List<Uri>

    suspend fun getExamPaperAnalysis(token: String, paper: ExamPaper): List<ExamPaperTopic>
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class ExamApiModule {
    @Binds
    abstract fun bindExamWebService(
        examWebService: ExamWebService
    ): ExamApi
}
