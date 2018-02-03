package com.lighterletter.www.oauth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    // Parameters given to you by the server //
    private String clientId = "";
    // Parameter set by you //
    private String redirectUri = "";
    private String freshTokenURL = redirectUri + "#access_token=";

    // Url to request an access token.
    private String key_request_url = "https://api.instagram.com/oauth/authorize/?client_id=" + clientId + "&redirect_uri=" + redirectUri + "&response_type=token";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = (WebView) findViewById(R.id.webView1);
        String savedToken = getSavedToken();


        if (savedToken.isEmpty()) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.setWebViewClient(new InstagramWebViewClient(this, getWindowManager(), redirectUri, freshTokenURL));
            webView.loadUrl(key_request_url);
        } else {
            webView.setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.textview)).setText("Logged in");
        }

    }


    private String getSavedToken() {
        String sharedPrefsKey = Constants.SHARED_PREFS;
        SharedPreferences prefs = getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE);
        return prefs.getString(Constants.SAVED_TOKEN, "");
    }


}
