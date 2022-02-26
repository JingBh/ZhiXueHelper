package top.jingbh.zhixuehelper.ui.util

import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import top.jingbh.zhixuehelper.R

fun makeLoadingSnackbar(view: View, @StringRes text: Int = R.string.loading): Snackbar {
    val context = view.context

    val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE)

    val barTextId = com.google.android.material.R.id.snackbar_text
    val contentLayout = snackbar.view.findViewById<View>(barTextId).parent as ViewGroup

    val progress = CircularProgressIndicator(context).apply {
        setPadding(0, dpToPx(4), 0, dpToPx(4))
        trackThickness = dpToPx(3)
        indicatorSize = dpToPx(32)
        isIndeterminate = true
    }
    contentLayout.addView(progress, 0)

    return snackbar
}
