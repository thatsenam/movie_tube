package movietube.movietube;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;

public class WebFetcher {
    private Context context;
    private WebView browser;
    private Listener<String> listener;
    public static String URLEmbed = "https://oladblock.me/embed/";
    public static String streamPrefix = "https://openload.co/stream/";
    private final Activity activity;
    private static WebFetcher webFetcher;

    private WebFetcher(Context context, WebView browser, Listener<String> listener) {
        this.context = context;
        this.browser = browser;
        this.listener = listener;
        activity = (Activity) context;
    }

    /* 35SkpDWw6NI */
    public static WebFetcher getInstance(Context context, WebView browser, Listener<String> listener, String id) {
        URLEmbed = "https://oladblock.me/embed/" + id;
        if (webFetcher == null) {
            webFetcher = new WebFetcher(context, browser, listener);
        }
        return webFetcher;

    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public void fetchUrl() {
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript:window.Foo.onUrlFound(document.getElementById('DtsBlkVFQx').innerText);");
                    }
                });

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String request) {
                ByteArrayInputStream EMPTY = new ByteArrayInputStream("".getBytes());
                if (request.contains("css") ||
                        request.contains("spotscenered") ||
                        request.contains("jpg") ||
                        request.contains("easylist") ||
                        request.contains("vidcpm") ||
                        request.contains("popads") ||
                        request.contains("video") ||
                        request.contains("ico")) {
                    return new WebResourceResponse("text/plain", "utf-8", EMPTY);
                }
                Log.e("Foo", request);
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });


        browser.setWebChromeClient(new WebChromeClient() {

        });

        browser.addJavascriptInterface(new WebFetcher.UrlInspector(new Listener<String>() {
            @Override
            public void onError(@NotNull String error) {
                // Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                listener.onError(error);
            }

            @Override
            public void onSuccess(String response) {
                // Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                listener.onSuccess(streamPrefix + response);

            }
        }), "Foo");

        browser.loadUrl(URLEmbed);


    }

    class UrlInspector {
        private Listener<String> listener;

        private UrlInspector() {
        }

        UrlInspector(Listener<String> listener) {
            this.listener = listener;
        }

        @JavascriptInterface
        public void onUrlFound(String url) {
            Log.e("Foo Url : ", streamPrefix + url);
            listener.onSuccess(url);
            if (url.equals("") || url.contains(" ")) {
                listener.onError("File Not Found");
            }
        }
    }
}
