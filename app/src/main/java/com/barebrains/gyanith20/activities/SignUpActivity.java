package com.barebrains.gyanith20.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.fragments.botSheet;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.models.SignUpDetails;
import com.barebrains.gyanith20.statics.GyanithUserManager;

import java.security.InvalidParameterException;

import static com.barebrains.gyanith20.fragments.botSheet.EMAIL_REQUEST_ID;

public class SignUpActivity extends AppCompatActivity {

    Loader loader;
    EditText name,usrname,pwd,conpwd,clg,email,num;
    ImageButton back;
    Button signup;
    Boolean checked = false;
    String gender;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //VIEW BINDINGS
        loader = findViewById(R.id.sign_up_loader);
        name = findViewById(R.id.name);
        num = findViewById(R.id.phone);
        usrname = findViewById(R.id.username);
        pwd = findViewById(R.id.password);
        pwd.setTransformationMethod(new PasswordTransformationMethod());
        conpwd = findViewById(R.id.confirmpassword);
        clg = findViewById(R.id.Collegename);
        email = findViewById(R.id.email);
        signup = findViewById(R.id.signupBtn);
        back = findViewById(R.id.backbutsignup);
//back click
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loader.loading();

        handleAuthState();
//click event
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    final SignUpDetails details = new SignUpDetails(
                            usrname.getText().toString()
                            ,name.getText().toString()
                            ,pwd.getText().toString()
                            ,conpwd.getText().toString()
                            ,email.getText().toString()
                            ,clg.getText().toString()
                            ,num.getText().toString()
                            ,gender
                    );
                    loader.loading();
                    GyanithUserManager.GyanithUserSignUp(details, new CompletionListener() {

                        public void OnComplete() {
                            loader.loaded();
                            //Verify User
                            botSheet.makeBotSheet(getSupportFragmentManager())
                                    .setTitle("Hi " + usrname.getText().toString() + ",")
                                    .setBody(getString(R.string.msg))
                                    .setAction("VERIFY")
                                    .setActionListener(new CompletionListener() {
                                                           @Override
                                                           public void OnComplete() {
                                                               try
                                                               {
                                                                   Intent email = new Intent(Intent.ACTION_MAIN);
                                                                   email.addCategory(Intent.CATEGORY_APP_EMAIL);
                                                                   startActivityForResult(email, EMAIL_REQUEST_ID);
                                                               }catch(ActivityNotFoundException n)
                                                               {
                                                                   Toast.makeText(SignUpActivity.this, "Error opening Default Email app, sorry", Toast.LENGTH_SHORT).show();
                                                               }
                                                           }

                                                           @Override
                                                           public void OnError(String error) {
                                                               GyanithUserManager.SignOutUser(null);
                                                           }
                                                       }
                                    ).show();
                        }

                        @Override
                        public void OnError(String error) {
                            loader.loaded();
                            //Show Error
                            botSheet.makeBotSheet(getSupportFragmentManager())
                                    .setTitle("Something went wrong")
                                    .setBody(error)
                                    .show();
                        }
                    });
                }catch (InvalidParameterException e){
                    loader.loaded();
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

   public void onRadioButtonClicked(View view)
    {

        checked = ((RadioButton) view).isChecked();

            switch(view.getId())
            {
                case R.id.male:
                    gender = "m";
                    break;
                case R.id.female:
                    gender = "f";
                    break;
                case R.id.other:
                    gender = "o";
                    break;
            }


    }


    private void handleAuthState(){
        GyanithUserManager.getCurrentUser().observe(this, new Observer<Resource<GyanithUser>>() {
            @Override
            public void onChanged(Resource<GyanithUser> res) {
                loader.loaded();

                res.response.handle();

                if (res.value != null)
                {
                    Intent intent = new Intent(SignUpActivity.this,ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EMAIL_REQUEST_ID)
        {
            GyanithUserManager.SignInUser(usrname.getText().toString(),pwd.getText().toString());
            Intent login = new Intent(SignUpActivity.this, LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            finish();
        }
    }
}
