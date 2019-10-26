package movietube.movietube.provider;

import android.app.Activity;
import android.os.AsyncTask;

import movietube.movietube.listeners.Listener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class VeryStreamProvider {

    private static VeryStreamProvider instance;
    private static Activity activity;
    private Listener<String> listener;
    private static String postFix = "?download=true";
    private static String preFix = "https://verystream.com/gettoken/";

    public static VeryStreamProvider getInstance(Activity activity) {
        VeryStreamProvider.activity = activity;
        if (instance == null) {
            instance = new VeryStreamProvider();
        }
        return instance;
    }

    public void fetch(String url, String id, Listener<String> listener) {
        if (listener == null) {
            return;
        }
        this.listener = listener;
        new MyTask(url, id).execute();


    }

    private class MyTask extends AsyncTask<String, Void, String> {
        private String url;
        private String id;
        private String string;
        private boolean error;

        MyTask(String url, String id) {
            this.url = url;
            this.id = id;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document document = Jsoup.connect(url).get();
                document.getElementById(id);
                Element element;
                element = document.getElementById(id);
                if (element == null) {
                    error = true;
                    return "Generic Error : Try after some time";

                } else {
                    error = false;
                    string = element.text();
                    return preFix + string + postFix;
                }
            } catch (IOException e) {
                error = true;
                return e.getLocalizedMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (error) {
                listener.onError(s);
            } else {
                listener.onSuccess(s);
            }
        }
    }

}
