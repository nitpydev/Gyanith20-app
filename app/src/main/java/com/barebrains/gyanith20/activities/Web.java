package com.barebrains.gyanith20.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.barebrains.gyanith20.R;

public class Web extends AppCompatActivity {

    public static final String EXTRA_URL="EXTRA_URL";
    public static final String EXTRA_TITLE="EXTRA_TITLE";


    String url = null;
    String title = null;
    WebView webView;


    public static class WebFactory{

        public final static int WEB_RESULT_CODE = 5;

        String url = null;
        String title = null;
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

        public void start(){
            Intent intent = new Intent(withActivity, Web.class);
            withActivity.startActivityForResult(intent,WEB_RESULT_CODE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        extractData(getIntent());

    }

    private void extractData(Intent intent){
        url = intent.getStringExtra(EXTRA_URL);
        title = intent.getStringExtra(EXTRA_TITLE);
    }

    private void applyData(){
        webView = findViewById(R.id.web);
        ((TextView)findViewById(R.id.web_title)).setText(title);
        webView.loadUrl(url);
    }
}
