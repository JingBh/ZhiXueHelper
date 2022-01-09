package top.jingbh.zhixuehelper.domain.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.data.auth.UserRepository
import javax.inject.Inject

class CheckIsLoggedInUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    private var jobCheckIsLoggedIn: Job? = null

    operator fun invoke(scope: CoroutineScope, callback: (Boolean) -> Unit) {
        jobCheckIsLoggedIn?.cancel()
        jobCheckIsLoggedIn = null

        jobCheckIsLoggedIn = scope.launch {
            val result = userRepository.isLoggedIn()
            callback(result)
        }
    }
}
