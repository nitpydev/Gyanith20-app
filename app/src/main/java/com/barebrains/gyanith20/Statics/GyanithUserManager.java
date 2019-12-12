package com.barebrains.gyanith20.Statics;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.barebrains.gyanith20.Models.GyanithUser;
import com.barebrains.gyanith20.Models.Userinfo;
import com.barebrains.gyanith20.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class GyanithUserManager extends AppCompatActivity {

    private static GyanithUserManager instance;

    private static GyanithUser loggedUser;

    private static String url = "example.com"; //url is not yet ready, dummy url is implemented

    public static GyanithUserManager getInstance() {

        if (instance == null)
            instance = new GyanithUserManager();
        return instance;
    }

    public static GyanithUser getCurrentUser() {
        return loggedUser;
    }

    public static void setLoggedUser(GyanithUser user) {
        if (user == null)
            return;
        loggedUser = user;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null)
            Log.d("asd", "impossible Error");
    }

    public static void SignInUser(final Context context)//String username,String password,OnLoginResult result)
    {
        GyanithSignIn("", "", new OnLoginResult() {
            @Override
            public void OnResult(GyanithUser user) {
                loggedUser = user;
                SaveGyanithUser(context, user);

                FirebaseUserSignIn(user, "asdadsnfsknfd");
            }
        });
    }

    public static void FirebaseUserSignIn(final GyanithUser gyanithUser, final String password) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(gyanithUser.email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                            return;

                        Class e = task.getException().getClass();

                        if (e == FirebaseAuthInvalidCredentialsException.class)
                            Log.d("asd", "Invalid Password");
                        else if (e == FirebaseAuthInvalidUserException.class) {
                            FirebaseUserSignUp(gyanithUser, password, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("asd", "New User " + task.isSuccessful());
                                }
                            });
                        }
                    }
                });

    }

    public static void FirebaseUserSignUp(final GyanithUser gyanithUser, String password, final OnCompleteListener<Void> completeListener) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(gyanithUser.email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                        root.child("users").child(gyanithUser.gyanithId).setValue(gyanithUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                completeListener.onComplete(task);
                            }
                        });
                    }
                });
    }

    private static void GyanithSignIn(final String username, final String password, OnLoginResult result) {
        //login user with gyanith server()
        // after receiving json response create a gyanith user
        //and use result.OnResult(gyanithUser);


        GyanithUser user = new GyanithUser("154asdad", "pushpavel", "pixel54", "jpushpavel@gmail.com", "8468494545", "NitPy");
        //user is dummy for now
        result.OnResult(user);
        requestJSON(); //requesting JSON from url
    }

    private static void SaveGyanithUser(Context context, GyanithUser user) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.package_name), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(user);
        sp.edit().putString(context.getString(R.string.gyanithUserKey), json)
                .apply();
    }

    private static void requestJSON() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject obj = new JSONObject(response);
                            if (obj.optString("status").equals("true")) { // User status "true"

                                ArrayList<Userinfo> UserinfoArrayList = new ArrayList<>();
                                JSONArray dataArray = obj.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    Userinfo info = new Userinfo();
                                    JSONObject dataobj = dataArray.getJSONObject(i);
                                    // setters are to be implemented here, until jason responce is implemented

                                    UserinfoArrayList.add(info);

                                }

                            } else {
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error  if occurrs
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(instance);

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }




    public static GyanithUser RetriveGyanithUser(Context context){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.package_name), Context.MODE_PRIVATE);
        String json = sp.getString(context.getString(R.string.gyanithUserKey),"");
        if (json.equals(""))
            return null;

        Gson gson = new Gson();
        return gson.fromJson(json,GyanithUser.class);
    }}



interface OnLoginResult
    {
        public void OnResult(GyanithUser user);//RETURN NULL IF USER LOGIN FAILED

    }


