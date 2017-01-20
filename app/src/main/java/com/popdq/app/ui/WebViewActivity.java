package com.popdq.app.ui;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.popdq.app.R;
import com.popdq.app.util.Utils;

public class WebViewActivity extends BaseActivity {
    private static final String TAG = "WebViewActivity";
    private WebView webView;
    private String url;
    private String title;
    private ProgressDialog progressBar;
    private boolean isStartLoad = false;
    private boolean isOver10s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);


        webView = (WebView) findViewById(R.id.webView);
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        Utils.setActionBar(this, title.toUpperCase(), R.drawable.btn_back);
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Loading...");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.i(TAG, "Processing webview url click...");
//                view.loadUrl(url);
//
//                return true;
//            }

            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "Finished loading URL: " + url);


                isStartLoad = true;
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, "Error: " + description);

            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setMessage("Loading: " + newProgress + "%");
                if (newProgress >= 100) {
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isOver10s = true;
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }
        }, 7000);

        webView.loadUrl(url);
    }
}
