package top.jingbh.zhixuehelper.data.util

import android.content.Context
import android.graphics.Bitmap
import androidx.collection.LruCache
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomRequestQueue @Inject constructor(@ApplicationContext context: Context) {
    val imageLoader: ImageLoader by lazy {
        ImageLoader(
            requestQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(20)

                override fun getBitmap(url: String): Bitmap? {
                    return cache.get(url)
                }

                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }

    private val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context)
    }

    private val retryPolicy = DefaultRetryPolicy(
        10000,
        3,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
    )

    fun <T> addToRequestQueue(req: Request<T>) {
        req.retryPolicy = retryPolicy
        requestQueue.add(req)
    }
}
