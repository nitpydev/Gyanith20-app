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
import com.android.volley.toolbox.Volley;
import com.barebrains.gyanith20.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    AlertDialog.Builder builder;
    EditText name,usrname,pwd,conpwd,clg,email;
    ProgressBar prog;
    Button signup, back;
    Context context;
    Boolean checked = false, passmatch;
    String fullname, username, pass,conpass, college, mail, gender;
    String url = "http://gyanith.org/api.php?action=signup&key=2ppagy0",result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name = (EditText) findViewById(R.id.name);
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
                college = clg.getText().toString();
                mail = email.getText().toString();
                pass = pwd.getText().toString();
                conpass = conpwd.getText().toString();
                passmatch = pass.equals(conpass);
                if((!passmatch) || fullname.equals("")|| username.equals("") || college.equals("") || mail.equals("") || pass.equals("")|| (!checked)){

                    isLoading(false);

                    if(!passmatch)
                        Toast.makeText(getApplicationContext(),"PassWord not matched",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(),"Please Fill all details",Toast.LENGTH_SHORT).show();
                }
                else{

                //jsonobject request

                RequestQueue requestQueue = Volley.newRequestQueue(context);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                        result = response.getString("result");
                        if(result.equals("success")){
                            builder.setTitle("Verify Email");
                            builder.setMessage(R.string.msg);
                            builder.setPositiveButton("Later", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent login = new Intent(context,LoginActivity.class);
                                    startActivity(login); }});
                            builder.setNegativeButton("Verify now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   try{ Intent email = new Intent(Intent.ACTION_MAIN);
                                       email.addCategory(Intent.CATEGORY_APP_EMAIL);
                                       startActivity(email);}catch (ActivityNotFoundException n){ Toast.makeText(context,"Error opening Default Email app, sorry",Toast.LENGTH_SHORT).show();}}});
                           AlertDialog dialog = builder.create();
                           dialog.show();
                        }
                        else{
                            String text = response.getString("text");
                            builder.setTitle("Error");
                            builder.setMessage(text);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        }catch (JSONException j){Toast.makeText(context,"server error",Toast.LENGTH_SHORT).show(); isLoading(false);}

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
                        return params;

                    }
                };
                requestQueue.add(jsonObjectRequest);}

            }
        });
    }

   public void onRadioButtonClicked(View view)
    {

        checked = ((RadioButton) view).isChecked();

            switch(view.getId())
            {
                case R.id.male:
                    gender = "male";
                    break;
                case R.id.female:
                    gender = "female";
                    break;
                case R.id.other:
                    gender = "other";
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
