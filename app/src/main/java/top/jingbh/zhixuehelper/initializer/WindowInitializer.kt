package top.jingbh.zhixuehelper.initializer

import android.content.Context
import androidx.startup.Initializer
import androidx.window.core.ExperimentalWindowApi
import androidx.window.embedding.SplitController
import top.jingbh.zhixuehelper.R

@OptIn(ExperimentalWindowApi::class)
class WindowInitializer : Initializer<SplitController> {
    override fun create(context: Context): SplitController {
        SplitController.initialize(context, R.xml.main_split_config)
        return SplitController.getInstance()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
