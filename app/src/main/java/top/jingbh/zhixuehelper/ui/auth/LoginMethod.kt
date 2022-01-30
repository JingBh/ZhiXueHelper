package top.jingbh.zhixuehelper.ui.auth

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.IdRes
import androidx.appcompat.content.res.AppCompatResources
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.util.PACKAGE_ZHIXUEAPP_PARENT
import top.jingbh.zhixuehelper.util.PACKAGE_ZHIXUEAPP_STUDENT
import top.jingbh.zhixuehelper.util.isPackageInstalled

enum class LoginMethod {
    IMPORT_STUDENT,
    IMPORT_PARENT,
    WEBVIEW,
    MANUAL;

    fun getName(resources: Resources): String {
        return when (this) {
            MANUAL -> resources.getString(R.string.method_manual)
            WEBVIEW -> resources.getString(R.string.method_webview)
            IMPORT_STUDENT -> resources.getString(R.string.method_import_student)
            IMPORT_PARENT -> resources.getString(R.string.method_import_parent)
        }
    }

    fun getHelp(resources: Resources): String {
        return when (this) {
            MANUAL -> resources.getString(R.string.method_manual_help)
            WEBVIEW -> resources.getString(R.string.method_webview_help)
            IMPORT_PARENT, IMPORT_STUDENT -> resources.getString(R.string.method_import_help)
        }
    }

    fun getIcon(context: Context): Drawable? {
        return when (this) {
            MANUAL -> AppCompatResources.getDrawable(context, R.drawable.ic_round_edit_24)
            WEBVIEW -> AppCompatResources.getDrawable(context, R.drawable.ic_round_language_24)
            IMPORT_STUDENT -> context.packageManager.getApplicationIcon(PACKAGE_ZHIXUEAPP_STUDENT)
            IMPORT_PARENT -> context.packageManager.getApplicationIcon(PACKAGE_ZHIXUEAPP_PARENT)
        }
    }

    @IdRes
    fun getNavigationDestination(): Int {
        return when (this) {
            MANUAL -> R.id.manual
            WEBVIEW -> R.id.webview
            else -> 0
        }
    }

    fun isAvailable(packageManager: PackageManager): Boolean {
        return when (this) {
            MANUAL -> true
            WEBVIEW -> true
            IMPORT_STUDENT -> isPackageInstalled(packageManager, PACKAGE_ZHIXUEAPP_STUDENT)
            IMPORT_PARENT -> isPackageInstalled(packageManager, PACKAGE_ZHIXUEAPP_PARENT)
        }
    }

    companion object {
        fun getAvailableMethods(packageManager: PackageManager): List<LoginMethod> {
            return values().filter { method -> method.isAvailable(packageManager) }
        }
    }
}
