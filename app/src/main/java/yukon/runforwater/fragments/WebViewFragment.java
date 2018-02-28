package yukon.runforwater.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import yukon.runforwater.R;

/**
 * Created by Kieran Halliday on 2017-11-07
 */

public class WebViewFragment extends Fragment {

    private WebView webView;
    private String url = "http://www.shakeyourpower.com/#shakeyourpower";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        webView = getView().findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.shake_you_power_webview, container, false);
    }
}
