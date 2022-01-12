package top.jingbh.zhixuehelper.data.exam

import androidx.paging.Pager
import androidx.paging.PagingConfig
import javax.inject.Inject

class ExamRepository @Inject constructor(
    private val examPagingSourceFactory: ExamPagingSourceFactory
) {
    fun getPager(token: String) = Pager(
        config = PagingConfig(pageSize = 10)
    ) {
        examPagingSourceFactory.create(token)
    }
}
