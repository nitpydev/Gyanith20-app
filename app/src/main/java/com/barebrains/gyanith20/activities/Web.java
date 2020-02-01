package com.barebrains.gyanith20.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.cookies;

import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Web extends AppCompatActivity {

    public static final String EXTRA_URL="EXTRA_URL";
    public static final String EXTRA_TITLE="EXTRA_TITLE";
    public static final String EXTRA_AUTH_LOSS = "EXTRA_AUTH_LOSS";


    String url = null;
    String title = null;
    WebView webView;


    public static class WebFactory{

        String url = null;
        String title = null;
        boolean authLoss = false;
        Activity withActivity;

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

        public void start(){
            Intent intent = new Intent(withActivity, Web.class);
            intent.putExtra(EXTRA_URL,url);
            intent.putExtra(EXTRA_TITLE,title);
            intent.putExtra(EXTRA_AUTH_LOSS,authLoss);
            withActivity.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        findViewById(R.id.web_close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Web.super.onBackPressed();
            }
        });

        extractData(getIntent());
        applyData();
    }

    private void extractData(Intent intent){
        url = intent.getStringExtra(EXTRA_URL);
        title = intent.getStringExtra(EXTRA_TITLE);
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
        webView = findViewById(R.id.web);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);
        }
        else
            CookieManager.getInstance().setAcceptCookie(true);

        ((TextView)findViewById(R.id.web_title)).setText(title);
        webView.loadUrl(url);
    }
}
