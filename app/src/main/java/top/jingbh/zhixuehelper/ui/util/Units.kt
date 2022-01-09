package top.jingbh.zhixuehelper.ui.util

import android.content.Context

// Taken from: https://stackoverflow.com/a/19953871
fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

