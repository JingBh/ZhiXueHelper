package top.jingbh.zhixuehelper.data.util

data class Pagination<T>(
    val page: Int,
    val data: List<T>,
    val pageSize: Int = data.size,
    val totalPages: Int
) {
    fun hasPrevPage(): Boolean {
        return page > 1
    }

    fun hasNextPage(): Boolean {
        return page < totalPages
    }
}
