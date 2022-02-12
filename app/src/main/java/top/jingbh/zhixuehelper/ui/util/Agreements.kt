package top.jingbh.zhixuehelper.ui.util

import android.content.Context
import android.text.Spanned
import androidx.core.text.HtmlCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.math.BigInteger
import javax.inject.Inject

class Agreements @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var agreed = false

    fun getAgreementsHtml(): Spanned {
        val inputStream = context.resources.assets.open("pages/agreements.html")
        val reader = inputStream.bufferedReader()
        val htmlSource = reader.readText()
        return HtmlCompat.fromHtml(htmlSource, 0)
    }

    fun isAgreementsAgreed(): Boolean {
        if (!agreed) {
            val file = getAgreementsAgreedFile()
            val agreedVersion = if (file.exists()) {
                val content = file.readBytes()
                try {
                    BigInteger(content).toInt()
                } catch (e: NumberFormatException) {
                    1
                }
            } else 0

            agreed = agreedVersion >= AGREEMENTS_VERSION
        }

        return agreed
    }

    fun agreeAgreements() {
        val file = getAgreementsAgreedFile()
        if (!file.exists()) file.createNewFile()
        file.writeBytes(BigInteger.valueOf(AGREEMENTS_VERSION.toLong()).toByteArray())
        agreed = true
    }

    private fun getAgreementsAgreedFile() = context.filesDir.resolve(".agreements-agreed")

    companion object {
        const val AGREEMENTS_VERSION = 1
    }
}
