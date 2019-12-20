package com.barebrains.gyanith20.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.PostManager;
import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class LoginActivity extends AppCompatActivity {


    EditText uid,pwd;
    Button signinBtn, backBtn;
    ProgressBar loginprog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        backBtn =findViewById(R.id.backbutlogin);
        loginprog=findViewById(R.id.loginprog);
        uid=findViewById(R.id.uid);
        pwd=findViewById(R.id.password);
        pwd.setTransformationMethod(new PasswordTransformationMethod());
        signinBtn =findViewById(R.id.signinBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        isLoading(false);

        boolean resolved = GyanithUserManager.resolveUserState(LoginActivity.this);
        if (resolved)
            OnSignInSuccess();

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLoading(true);
                String pas = pwd.getText().toString();
                String username = uid.getText().toString();

                if (pas.equals("") || username.equals("")){
                    isLoading(false);
                    Toast.makeText(getApplicationContext(), "Enter credentials!", Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d("asd","asdasd");
                    GyanithUserManager.SignInUser(LoginActivity.this
                            , username, pas, new ResultListener<GyanithUser>() {
                                    @Override
                                    public void OnResult(GyanithUser gyanithUser) {
                                        isLoading(false);
                                        if (gyanithUser == null) {
                                            Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        OnSignInSuccess();
                                    }

                                @Override
                                public void OnError(String error) {
                                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                                    isLoading(false);
                                }
                            });
                }
            }
        });
    }

    private void OnSignInSuccess(){
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(intent);
    }

    private void isLoading(boolean state){
        if (state){
            loginprog.setVisibility(View.VISIBLE);
            signinBtn.setVisibility(View.GONE);
        }
        else {
            loginprog.setVisibility(View.GONE);
            signinBtn.setVisibility(View.VISIBLE);
        }
    }
}

// Login https://api.jsonbin.io/b/5c67b234a83a29317735e26c/1
// Details https://api.jsonbin.io/b/5c67b201a83a29317735e24c/1