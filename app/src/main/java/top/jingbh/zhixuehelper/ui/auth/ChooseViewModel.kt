package top.jingbh.zhixuehelper.ui.auth

import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChooseViewModel @Inject constructor(
    packageManager: PackageManager
) : ViewModel() {
    val methods: LiveData<List<LoginMethods>> = liveData {
        emit(LoginMethods.getAvailableMethods(packageManager))
    }

    private val recommendedMethods = MediatorLiveData<List<LoginMethods>>()

    init {
        recommendedMethods.addSource(methods) { methods ->
            val result = arrayListOf<LoginMethods>()

            if (methods.contains(LoginMethods.IMPORT_STUDENT))
                result.add(LoginMethods.IMPORT_STUDENT)

            if (methods.contains(LoginMethods.IMPORT_PARENT))
                result.add(LoginMethods.IMPORT_PARENT)

            recommendedMethods.postValue(result)
        }
    }
}
