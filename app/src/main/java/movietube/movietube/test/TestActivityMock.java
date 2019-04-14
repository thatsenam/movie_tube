package movietube.movietube.test;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import movietube.movietube.Listener;
import movietube.movietube.R;
import movietube.movietube.WebFetcher;


public class TestActivityMock extends AppCompatActivity {
    WebView browser;
    public static String URLEmbed = "https://oladblock.me/embed/-aIj_mXj4f8";
    public static String streamPrefix = "https://openload.co/stream/";
    long start, ends;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        browser = findViewById(R.id.browser);
        WebFetcher.getInstance(this, browser, new Listener<String>() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(TestActivityMock.this, response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NotNull String error) {
                Toast.makeText(TestActivityMock.this, error, Toast.LENGTH_SHORT).show();

            }
        }, "35SkpDWw6NI").fetchUrl();


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
