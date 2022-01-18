package top.jingbh.zhixuehelper.ui.util

import android.content.Context
import android.text.Spanned
import androidx.core.text.HtmlCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Agreements @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getAgreementsHtml(): Spanned {
        val inputStream = context.resources.assets.open("pages/agreements.html")
        val reader = inputStream.bufferedReader()
        val htmlSource = reader.readText()
        return HtmlCompat.fromHtml(htmlSource, 0)
    }

    fun isAgreementsAgreed(): Boolean {
        val file = context.filesDir.resolve(".agreements-agreed")
        return file.exists()
    }

    fun agreeAgreements() {
        val file = getAgreementsAgreedFile()
        if (!file.exists()) file.createNewFile()
    }

    private fun getAgreementsAgreedFile() = context.filesDir.resolve(".agreements-agreed")
}
