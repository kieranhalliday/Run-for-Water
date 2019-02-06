package yukon.runforlife.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.Objects;

import yukon.runforlife.R;

/**
 * Created by Kieran Halliday on 2017-11-07
 */

// Used to promote friends of Run for Life
public class WebViewFragment extends Fragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        WebView webView = Objects.requireNonNull(getView()).findViewById(R.id.webView1);
        // Javascript is required for the shake your power website
        webView.getSettings().setJavaScriptEnabled(true);
        String url = "http://www.shakeyourpower.com/#shakeyourpower";
        webView.loadUrl(url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.shake_you_power_webview, container, false);
    }
}
