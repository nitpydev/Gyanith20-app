package com.barebrains.gyanith20.activities;

import android.content.Context;
import android.content.Intent;


import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.GyanithUserManager;

public class LoginActivity extends AppCompatActivity {

    Loader loader;
    EditText uid,pwd;
    TextView signup;
    Button signinBtn;
    ImageButton backBtn;
    Loader sign_in_loader;
    Context cnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean resolved = GyanithUserManager.resolveUserState(this);
        if (resolved)
            OnSignInSuccess();

        setContentView(R.layout.activity_login);
        loader = findViewById(R.id.login_loader);
        backBtn =findViewById(R.id.backbutlogin);
        sign_in_loader = findViewById(R.id.sign_in_loader);
        uid=findViewById(R.id.uid);
        pwd=findViewById(R.id.password);
        cnt = this;
        signup = findViewById(R.id.sign_up);
        pwd.setTransformationMethod(new PasswordTransformationMethod());
        signinBtn =findViewById(R.id.signinBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sign_in_loader.loaded();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup_act();
            }
        });

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_in_loader.loading();
                String pas = pwd.getText().toString();
                String username = uid.getText().toString();
                if (pas.equals("") || username.equals("")){
                    sign_in_loader.loaded();
                    Toast.makeText(getApplicationContext(), "Enter credentials!", Toast.LENGTH_LONG).show();
                }
                else {
                    GyanithUserManager.SignInUser(LoginActivity.this
                            , username, pas, new ResultListener<GyanithUser>() {
                                    @Override
                                    public void OnResult(GyanithUser gyanithUser) {
                                        OnSignInSuccess();
                                    }

                                @Override
                                public void OnError(String error) {
                                        if (error.equals("not verified"))
                                        {
                                            //TODO:Should show verify to continue
                                        }
                                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                                        sign_in_loader.loaded();
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

    private void signup_act()
    {
        Intent signupint = new Intent(cnt,SignUpActivity.class);
        startActivity(signupint);
    }
}