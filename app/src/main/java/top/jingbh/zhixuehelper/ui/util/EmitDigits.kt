package top.jingbh.zhixuehelper.ui.util

import java.text.DecimalFormat

fun Double.emitDigits(): String {
    return DecimalFormat("###.#").format(this)
}
