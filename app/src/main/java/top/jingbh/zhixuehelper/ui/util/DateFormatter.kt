package top.jingbh.zhixuehelper.ui.util

import android.content.Context
import androidx.core.os.ConfigurationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.DateFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateFormatter @Inject constructor(
    @ApplicationContext context: Context
) {
    private val locale = ConfigurationCompat.getLocales(context.resources.configuration)[0]

    val mediumFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
}
