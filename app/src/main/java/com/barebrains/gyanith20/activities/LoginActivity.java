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
import com.barebrains.gyanith20.fragments.BottomSheetFragment;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.GyanithUserManager;

public class LoginActivity extends AppCompatActivity {

    Loader loader;
    EditText uid,pwd;
    TextView signup;
    Button signinBtn;
    ImageButton backBtn;
    Context cnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loader = findViewById(R.id.login_loader);

        loader.loading();
        resolveIntent(getIntent());


        backBtn =findViewById(R.id.backbutlogin);
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

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup_act();
            }
        });

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loader.loading();
                String pas = pwd.getText().toString();
                String username = uid.getText().toString();
                if (pas.equals("") || username.equals("")){
                    loader.loaded();
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
                                            loader.loaded();
                                            BottomSheetFragment fragment = new BottomSheetFragment("Verify mail",getString(R.string.msg),true,new CompletionListener(){
                                                @Override
                                                public void OnComplete() {
                                                    signinBtn.performClick();
                                                }
                                            });
                                            fragment.show(getSupportFragmentManager(), "TAG");
                                        }
                                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                                        loader.loaded();
                                }
                            });
                }
            }
        });
    }

    private void OnSignInSuccess(){
        Intent intent = new Intent(this,ProfileActivity.class);
        finish();
        startActivity(intent);
    }

    private void signup_act()
    {
        Intent signupint = new Intent(cnt,SignUpActivity.class);
        startActivity(signupint);
    }

    private void resolveIntent(Intent intent){
        boolean resolved = GyanithUserManager.resolveUserState(this);
        if (resolved)
            OnSignInSuccess();

        if (intent != null)
            return;

        String username = intent.getStringExtra("usrname");
        String pwd = intent.getStringExtra("pwd");
        if (username == null || pwd == null || username.isEmpty() || pwd.isEmpty())
            return;

        uid.setText(username);
        this.pwd.setText(pwd);

        signinBtn.performClick();
    }
}