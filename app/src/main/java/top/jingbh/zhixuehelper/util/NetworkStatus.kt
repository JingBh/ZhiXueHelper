package top.jingbh.zhixuehelper.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

private const val CHECK_TIMEOUT = 1000 // ms

// Taken from https://stackoverflow.com/a/27312494/18176440
suspend fun isOnline(): Boolean = withContext(Dispatchers.IO) {
    try {
        val socket = Socket()
        val address = InetSocketAddress("zhixue.com", 443)

        socket.connect(address, CHECK_TIMEOUT)
        socket.close()

        true
    } catch (e: IOException) {
        false
    }
}
