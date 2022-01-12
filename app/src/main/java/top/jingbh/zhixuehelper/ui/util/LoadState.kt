package top.jingbh.zhixuehelper.ui.util

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates

fun LoadStates.isAnyLoading(): Boolean {
    return refresh is LoadState.Loading ||
        prepend is LoadState.Loading ||
        append is LoadState.Loading
}

fun CombinedLoadStates.isAnyLoading(): Boolean {
    return source.isAnyLoading() || mediator?.isAnyLoading() == true
}
