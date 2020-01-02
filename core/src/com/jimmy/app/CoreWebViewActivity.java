package com.jimmy.app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jimmy.core.R;

import org.jetbrains.annotations.NotNull;


public class CoreWebViewActivity extends CoreActivity {

    protected WebView webView;
    protected ProgressBar progressBar;
    protected String url;
    protected String alertMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchParams();
        setupView();
        setupWebView();
        loadUrl(url);
    }

    @Override
    public int getLayoutId() {
        return R.layout.webview_layout;
    }

    protected void fetchParams() {
        Uri uri = getIntent().getData();
        url = uri.getQueryParameter("url");
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, getString(R.string.web_url_empty), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            Toast.makeText(this, getString(R.string.web_url_error), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        alertMessage = uri.getQueryParameter("alertmessage");
    }


    protected void setupView() {
        webView = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

    }

    protected void setupWebView() {
        WebSettings settings = webView.getSettings();
        setupWebSettings(settings);

        webView.setWebViewClient(createWebViewClient());
        webView.setWebChromeClient(createChromeWebViewClient());
    }

    protected void loadUrl(String url) {
        webView.loadUrl(url);
    }

    protected void setupWebSettings(WebSettings settings) {
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setAppCacheEnabled(false);
        settings.setGeolocationEnabled(true);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    protected void goBack() {
        hideKeyboard(webView);
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    protected WebViewClient createWebViewClient() {
        return new QCWebViewClient();
    }

    protected WebChromeClient createChromeWebViewClient() {
        return new QCWebChromeClient();
    }

    @NotNull
    @Override
    public ILoadingDialog getProgressDialog() {
        return new CoreLoadingDialog(this);
    }

    public class QCWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("tel://")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    public class QCWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressBar.setProgress(newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
//            if (getTitleBar().getTitleStyle() == TitleBar.CUSTOM_TITLE) {
//                getTitleBar().setTitle(title);
//            }
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }
    }

}
