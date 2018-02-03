package com.lighterletter.www.oauth;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by john on 2/3/18.
 */

public class InstagramWebViewClient extends WebViewClient {

    private static final String TAG = InstagramWebViewClient.class.getName();
    private static String redirectUri;
    private static String freshTokenUrl;
    private WindowManager windowManager;
    private Context context;

    public InstagramWebViewClient(Context context, WindowManager windowManager, String redirectUri, String freshTokenUrl) {
        this.context = context;
        this.windowManager = windowManager;
        this.redirectUri = redirectUri;
        this.freshTokenUrl = freshTokenUrl;
    }

    /**
     * The shouldOverrideUrlLoading() appear twice below to support different android versions.
     *
     * @param view webview
     * @param url  url to load in webview. Is request.getUrl(); for phones running android N.
     * @return
     */
    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        final Uri uri = Uri.parse(url);
        return handleUri(view, uri);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        final Uri uri = request.getUrl();
        return handleUri(view, uri);
    }

    /**
     * This method takes care of the logic of authenticating the user, saving token if permission
     * is granted and taking the user through sign up process if the user does not have an account.
     *
     * @param view webview
     * @param uri  redirect uri passed by shouldOverrideUrlLoading();
     * @return
     */
    private boolean handleUri(WebView view, final Uri uri) {
        String requestURI = String.valueOf(uri.toString());
        String uriBaseSubstring = requestURI.substring(0, redirectUri.length());

        if (uriBaseSubstring.contains("instagram")) {
            Log.d(TAG, "shouldOverrideUrlLoading: ");
            return false; //default, allows webview to load url

        } else if (requestURI.contains(redirectUri)) {

            if (requestURI.contains("error")) {
                //usually happens when user denies access permission
                Log.e(TAG, "Something went wrong. Uri: " + requestURI);
                windowManager.removeView(view);
                return true;
            } else if (requestURI.contains("#access_token=")) { //token request accepted, permission granted by the user

                String token = requestURI.substring(freshTokenUrl.length() + 1, requestURI.length());
                Log.d(TAG, "Token saved: Token: " + token);
                Log.d(TAG, "handleUri: removing webview... ");

                saveTokenToPrefs(token);
                view.setVisibility(View.INVISIBLE);
                view.destroy();
                view.clearCache(true);
                return true; // indicates webview to NOT load the url. Creates blank screen to avoid redirect.
            }
        }
        return false;
    }

    private void saveTokenToPrefs(String token) {
        String sharedPrefsKey = Constants.SHARED_PREFS;
        SharedPreferences prefs = context.getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.SAVED_TOKEN, token);
        editor.commit();
    }

}