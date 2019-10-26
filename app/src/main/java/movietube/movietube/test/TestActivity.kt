package movietube.movietube.test


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test.*
import movietube.movietube.R
import movietube.movietube.listeners.Listener


class TestActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        browsertest.settings.javaScriptEnabled = true
        browsertest.webChromeClient = object : WebChromeClient() {

        }
        browsertest.addJavascriptInterface(UrlInspector(object : Listener<String>() {
            override fun onError(error: String) {
                Toast.makeText(getContex(), error, Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(response: String) {
                Toast.makeText(getContex(), response, Toast.LENGTH_SHORT).show()

            }

        }), "Foo")



        browsertest.webViewClient =
                object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        urlTV.text = url
                        runOnUiThread {
                            browsertest.loadUrl("javascript:window.Foo.onUrlFound(document.getElementById('DtsBlkVFQx').innerText);")
                        }
                    }


                }




        browsertest.loadUrl(URLEmbed)

    }


    companion object {
        val URL = "https://oladblock.me/f/-aIj_mXj4f8"
        val URLEmbed = "https://oladblock.me/embed/-aIj_mXj4f8"
        val URLEmbeda = "https://google.com"
        var StreamUrl = "https://openload.co/stream/-aIj_mXj4f8~1554914454~103.114.0.0~h6jwhb_9?mime=true"
        var streamPrefix = "https://openload.co/stream/"
    }

    fun getContex(): Context = this

    class UrlInspector(var listener: Listener<String>) {

        @JavascriptInterface
        fun onUrlFound(url: String) {
            Log.e("Foo Url : ", streamPrefix + url)
            listener.onSuccess(url)
            if (url == "") {
                listener.onError("File Not Found")
            }

        }
    }
}





