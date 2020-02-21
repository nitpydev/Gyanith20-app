package com.barebrains.gyanith20.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.interfaces.UrlLoadListener;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.DataRepository;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.cookies;

import java.lang.reflect.Array;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Web extends AppCompatActivity {

    public static final String EXTRA_URL="EXTRA_URL";
    public static final String EXTRA_TITLE="EXTRA_TITLE";
    public static final String EXTRA_AUTH_LOSS = "EXTRA_AUTH_LOSS";
    public static final String EXTRA_LOAD_LISTENER_INDEX = "EXTRA_LOAD_LISTENER_INDEX";


    String url = null;
    String title = null;
    WebView webView;

    private static ArrayList<UrlLoadListener> loadListeners = new ArrayList<>();


    private UrlLoadListener loadListener;

    public static class WebFactory{

        private String url = null;
        private String title = null;
        private boolean authLoss = false;
        private Activity withActivity;
        private int loadListenerIndex = -1;

        private WebFactory(Activity activity){
            this.withActivity = activity;
        }

        public static WebFactory with(@NonNull Activity activity){
            return new WebFactory(activity);
        }

        public WebFactory load(@NonNull String url){
            this.url = url;
            return this;
        }

        public WebFactory title(@NonNull String title){
            this.title = title;
            return this;
        }

        public WebFactory finishOnAuthLoss(){
            this.authLoss = true;
            return this;
        }

        public WebFactory interceptUrlLoading(@NonNull UrlLoadListener listener){
            Web.loadListeners.add(listener);
            loadListenerIndex = Web.loadListeners.size()-1;
            return this;
        }

        public void start(){
            Intent intent = new Intent(withActivity, Web.class);
            intent.putExtra(EXTRA_URL,url);
            intent.putExtra(EXTRA_TITLE,title);
            intent.putExtra(EXTRA_AUTH_LOSS,authLoss);
            intent.putExtra(EXTRA_LOAD_LISTENER_INDEX,loadListenerIndex);
            withActivity.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = findViewById(R.id.web);

        findViewById(R.id.web_close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        extractData(getIntent());
        applyData();
    }

    private void extractData(Intent intent){
        url = intent.getStringExtra(EXTRA_URL);
        title = intent.getStringExtra(EXTRA_TITLE);

        int index = intent.getIntExtra(EXTRA_LOAD_LISTENER_INDEX,-1);
        if (index != -1)
            loadListener = loadListeners.get(index);

        boolean authLoss = intent.getBooleanExtra(EXTRA_AUTH_LOSS, false);
        if (authLoss)
            GyanithUserManager.getCurrentUser().observe(this, new Observer<Resource<GyanithUser>>() {
                @Override
                public void onChanged(Resource<GyanithUser> res) {
                    if (res.value == null)
                        finish();
                }
            });
    }

    private void applyData(){
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);
        }
        else
            CookieManager.getInstance().setAcceptCookie(true);

        ((TextView)findViewById(R.id.web_title)).setText(title);
        webView.loadUrl(url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (loadListener != null)
                    loadListener.onLoad(Web.this,view,url);


            }

        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack())
            webView.goBack();
        else
            finish();
    }
}
