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
import androidx.lifecycle.Observer;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.fragments.BottomSheetFragment;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.NetworkManager;

public class LoginActivity extends AppCompatActivity {

    Loader loader;
    EditText uid,pwd;
    TextView signup;
    Button signinBtn;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loader = findViewById(R.id.login_loader);

        loader.setNeverErrorFlag(true);
        loader.loading();
        handleAuthState();

        backBtn =findViewById(R.id.backbutlogin);
        uid=findViewById(R.id.uid);
        pwd=findViewById(R.id.password);
        signup = findViewById(R.id.sign_up);
        signinBtn =findViewById(R.id.signinBtn);

        pwd.setTransformationMethod(new PasswordTransformationMethod());

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
                else
                    GyanithUserManager.SignInUser(username,pas);

            }
        });
    }
    private void signup_act()
    {
        Intent signupint = new Intent(this,SignUpActivity.class);
        startActivity(signupint);
    }

    private void handleAuthState(){
        GyanithUserManager.getCurrentUser().observe(this, new Observer<Resource<GyanithUser>>() {
            @Override
            public void onChanged(Resource<GyanithUser> res) {
                loader.loaded();
                if (res.error.getMessage() != null)
                    Toast.makeText(LoginActivity.this, res.error.getMessage(), Toast.LENGTH_SHORT).show();

                if (res.error.getIndex() != null && res.error.getIndex() == 1)
                {
                    BottomSheetFragment fragment = new BottomSheetFragment("Verify mail",getString(R.string.msg),true,new CompletionListener(){
                        @Override
                        public void OnComplete() {
                            signinBtn.performClick();
                        }
                    });
                    fragment.show(getSupportFragmentManager(), "TAG");
                }

                if (res.value != null)
                {
                    Intent intent = new Intent(LoginActivity.this,ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}