package com.barebrains.gyanith20.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.interfaces.UrlLoadListener;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.GyanithUserManager;

import java.net.MalformedURLException;
import java.net.URL;

public class WebRedirecter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();
        final String token = uri.getQueryParameter("code");
        final String verifyUrl = "https://gyanith.org/verify.php?code=" + token + "&gyid=" + uri.getQueryParameter("gyid");

        Web.WebFactory.with(this)
                .title("Verfication")
                .load(verifyUrl)
                .interceptUrlLoading(new UrlLoadListener() {
                    @Override
                    public void onLoad(Web web,WebView view, String url) {
                        if (!url.equals(verifyUrl)) {
                            GyanithUserManager.SignOutUser(null);
                            Intent intent = new Intent(WebRedirecter.this,LoginActivity.class);
                            startActivity(intent);
                            GyanithUserManager.SignInFromVerification(token);
                            web.finish();
                            finish();
                        }
                    }
                })
                .start();
        finish();
    }
}
