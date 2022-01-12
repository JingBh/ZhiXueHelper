package top.jingbh.zhixuehelper.data.exam

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.volley.VolleyError
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.json.JSONException

class ExamPagingSource @AssistedInject constructor(
    @Assisted private val token: String,
    private val examApi: ExamApi
) : PagingSource<Int, Exam>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Exam> {
        return try {
            val loadKey = params.key ?: 1
            val response = examApi.getExamList(token, loadKey, params.loadSize)
            return LoadResult.Page(
                data = response.data,
                prevKey = if (response.hasPrevPage()) loadKey - 1 else null,
                nextKey = if (response.hasNextPage()) loadKey + 1 else null
            )
        } catch (e: JSONException) {
            LoadResult.Error(e)
        } catch (e: VolleyError) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Exam>): Int {
        return 1
    }
}

@AssistedFactory
interface ExamPagingSourceFactory {
    fun create(token: String): ExamPagingSource
}
