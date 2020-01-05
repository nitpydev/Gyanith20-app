package com.barebrains.gyanith20.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.fragments.BottomSheetFragment;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.models.SignUpDetails;
import com.barebrains.gyanith20.statics.GyanithUserManager;

import java.security.InvalidParameterException;

public class SignUpActivity extends AppCompatActivity {

    EditText name,usrname,pwd,conpwd,clg,email,num;
    ProgressBar prog;
    Button signup, back;
    Boolean checked = false;
    String gender;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
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
        prog = findViewById(R.id.signupprog);
        isLoading(false);

//back click
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//click event
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isLoading(true);
                try {
                    SignUpDetails details = new SignUpDetails(
                            usrname.getText().toString()
                            ,name.getText().toString()
                            ,pwd.getText().toString()
                            ,conpwd.getText().toString()
                            ,email.getText().toString()
                            ,clg.getText().toString()
                            ,num.getText().toString()
                            ,gender
                    );

                    GyanithUserManager.GyanithUserSignUp(details, new CompletionListener() {

                        public void OnComplete() {
                            isLoading(false);
                            //Verify User
                            BottomSheetFragment fragment = new BottomSheetFragment("Verify mail",getString(R.string.msg),true,new CompletionListener(){
                                @Override
                                public void OnComplete() {
                                    finish();
                                }
                            });
                            fragment.show(getSupportFragmentManager(), "TAG");

                        }

                        @Override
                        public void OnError(String error) {
                            isLoading(false);
                            //Show Error
                            BottomSheetFragment fragment = new BottomSheetFragment("Error",error,false,new CompletionListener(){
                                @Override
                                public void OnComplete() {
                                    finish();
                                }
                            });
                            fragment.show(getSupportFragmentManager(), "TAG");
                        }
                    });
                }catch (InvalidParameterException e){
                    isLoading(false);
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


    private void isLoading(boolean state){
        if (state){
            prog.setVisibility(View.VISIBLE);
            signup.setVisibility(View.GONE);
        }
        else {
            prog.setVisibility(View.GONE);
            signup.setVisibility(View.VISIBLE);
        }

    }

}
