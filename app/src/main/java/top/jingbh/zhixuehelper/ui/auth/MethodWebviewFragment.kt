package top.jingbh.zhixuehelper.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import top.jingbh.zhixuehelper.databinding.FragmentLoginWebviewBinding

@AndroidEntryPoint
@SuppressLint("SetJavaScriptEnabled", "SourceLockedOrientationActivity")
class MethodWebviewFragment : Fragment() {
    private lateinit var binding: FragmentLoginWebviewBinding

    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginWebviewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.apply {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        binding.webview.apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    val host = Uri.parse(url).host
                    if (host == "www.zhixue.com" || host == "open.changyan.com") return false

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)

                    return true
                }

                override fun doUpdateVisitedHistory(
                    view: WebView?,
                    url: String?,
                    isReload: Boolean
                ) {
                    if (Uri.parse(url).path?.startsWith("/container/container") == true) {
                        Log.d(TAG, "Login succeed")
                        evaluateJavascript(jsFindToken()) {}
                    }

                    super.doUpdateVisitedHistory(view, url, isReload)
                }
            }

            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptThirdPartyCookies(this, true)

            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

            addJavascriptInterface(object {
                @JavascriptInterface
                fun submitToken(token: String) {
                    Log.d(TAG, "Received token: $token")
                    cookieManager.removeAllCookies {}
                    if (token.isNotBlank()) loginViewModel.updateToken(token)
                }
            }, "Android")

            loadUrl("https://www.zhixue.com/wap_login.html")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        activity?.apply {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    private fun jsFindToken(): String {
        val inputStream = requireContext().assets.open("js/findToken.js")
        val reader = inputStream.bufferedReader()
        val js = reader.readText()
        reader.close()
        inputStream.close()
        return js
    }

    companion object {
        private const val TAG = "MethodWebviewFragment"
    }
}
