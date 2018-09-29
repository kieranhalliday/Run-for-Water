package yukon.runforlife.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import yukon.runforlife.R;

/**
 * Created by Kieran Halliday on 2017-11-01
 */

public class BuyFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.buy_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        WebView webView = getView().findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        // Enable hardware acceleration
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.loadUrl("https://riftvalleymarathon.com/sponsor-a-well/sponsor/");
    }
}