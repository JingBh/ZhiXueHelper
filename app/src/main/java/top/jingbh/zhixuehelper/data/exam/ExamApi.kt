package top.jingbh.zhixuehelper.data.exam

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import top.jingbh.zhixuehelper.data.util.Pagination

interface ExamApi {
    suspend fun getExamList(token: String, pageIndex: Int, pageSize: Int): Pagination<Exam>

    suspend fun getExamPaperList(token: String, exam: Exam): List<ExamPaper>
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class ExamApiModule {
    @Binds
    abstract fun bindExamWebService(
        examWebService: ExamWebService
    ): ExamApi
}
