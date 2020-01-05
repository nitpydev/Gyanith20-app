package com.barebrains.gyanith20.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.barebrains.gyanith20.R;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    AlertDialog.Builder builder;
    EditText name,usrname,pwd,conpwd,clg,email,num;
    ProgressBar prog;
    Button signup, back;
    Context context;
    Boolean checked = false, passmatch;
    String fullname, username, pass,conpass, college, mail, gender,phone;
    String url = "http://gyanith.org/api.php?action=signup&key=2ppagy0",result;
    String EMAIL_CHECK ="^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    static Pattern pattern;
    Matcher matcher;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name = (EditText) findViewById(R.id.name);
        num =(EditText) findViewById(R.id.phone);
        usrname = (EditText) findViewById(R.id.username);
        pwd = (EditText) findViewById(R.id.password);
        pwd.setTransformationMethod(new PasswordTransformationMethod());
        conpwd =(EditText) findViewById(R.id.confirmpassword);
        clg = (EditText) findViewById(R.id.Collegename);
        email = (EditText) findViewById(R.id.email);
        signup = (Button) findViewById(R.id.signupBtn);
        back =(Button) findViewById(R.id.backbutsignup);
        prog =(ProgressBar) findViewById(R.id.signupprog);
        context = this;
        builder = new AlertDialog.Builder(context);

        pattern = Pattern.compile(EMAIL_CHECK,Pattern.CASE_INSENSITIVE);

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
                fullname = name.getText().toString();
                username = usrname.getText().toString();
                String ws = username.replace(" ","");
                college = clg.getText().toString();
                mail = email.getText().toString();
                pass = pwd.getText().toString();
                conpass = conpwd.getText().toString();
                passmatch = pass.equals(conpass);
                phone = num.getText().toString();
                if( !emailValidate(mail)|| (phone.length() > 10) || !username.equals(ws)||(!passmatch) || fullname.equals("")|| username.equals("") || college.equals("") || mail.equals("") || pass.equals("")|| (!checked)){

                    isLoading(false);

                    if(!passmatch)
                        Toast.makeText(getApplicationContext(),"PassWord not matched",Toast.LENGTH_SHORT).show();
                    else if(!username.equals(ws))
                        Toast.makeText(getApplicationContext(),"Whitespace in username not Allowed",Toast.LENGTH_SHORT).show();
                    else if(phone.length() > 10)
                        Toast.makeText(getApplicationContext(),"phone number should not exceed 10 digits",Toast.LENGTH_SHORT).show();
                    else  if(!emailValidate(mail))
                        Toast.makeText(getApplicationContext(),"Please check your email, email is invalid",Toast.LENGTH_SHORT).show();

                    else
                        Toast.makeText(getApplicationContext(),"Please Fill all details",Toast.LENGTH_SHORT).show();

                }
                else{

                //jsonobject request

                RequestQueue requestQueue = Volley.newRequestQueue(context);
                StringRequest stringrequest = new StringRequest(Request.Method.POST, url,  new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            JSONObject obj = new JSONObject(response);
                            String result = obj.getString("result");
                        if(result.equals("success")){
                            builder.setTitle("Verify Email");
                            builder.setMessage(R.string.msg);
                            builder.setPositiveButton("Verify now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try{ Intent email = new Intent(Intent.ACTION_MAIN);
                                        email.addCategory(Intent.CATEGORY_APP_EMAIL);
                                        startActivity(email);}catch (ActivityNotFoundException n){ Toast.makeText(context,"Error opening Default Email app, sorry",Toast.LENGTH_SHORT).show();}}});

                           AlertDialog dialog = builder.create();
                           dialog.show();
                        }
                        else{
                            JSONObject txtobj = new JSONObject(response);
                            String text = txtobj.getString("text");
                            builder.setTitle("Error");
                            builder.setMessage(text);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        }catch (JSONException j){Toast.makeText(context,"server error",Toast.LENGTH_SHORT).show(); isLoading(false);}
                        isLoading(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        isLoading(false);
                        Toast.makeText(context,"Network Error",Toast.LENGTH_SHORT).show();  }               }){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("pname",fullname);
                        params.put("usr",username);
                        params.put("clg",college);
                        params.put("gdr",gender);
                        params.put("email",mail);
                        params.put("pswd1",pass);
                        params.put("phone",phone);
                        return params;

                    }
                };
                requestQueue.add(stringrequest);}

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

    public  Boolean emailValidate(String email){
        matcher = pattern.matcher(email);
        return matcher.matches();
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
