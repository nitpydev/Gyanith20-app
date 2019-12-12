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
import com.google.gson.JsonParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class GyanithUserManager {

    private static GyanithUserManager instance;

    private static GyanithUser loggedUser;

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

    public static void SignInUser(final Context context,String username,String password,OnLoginResult result)
    {
        GyanithSignIn(context,"", "", new OnLoginResult() {
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

    private static void GyanithSignIn(Context context,final String username, final String password, final OnLoginResult result) {

        StringRequest userLoginRequest = new StringRequest(Request.Method.GET, buildUrl(username, password), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    GyanithUserJsonResponse user = (new Gson()).fromJson(response, GyanithUserJsonResponse.class);
                    GyanithUser gyanithUser = new GyanithUser(user.gyanithId
                            , user.name
                            , user.username
                            , user.email
                            , user.phoneNumber
                            , user.clg);
                    result.OnResult(gyanithUser);
                }
                catch (JsonParseException e) {
                    Log.d("asd",e.toString());//Handle Error-------------------------
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Handle Error---------------------------------
            }
        });

       // Volley.newRequestQueue(context).add(userLoginRequest); //This will be used after backend ready


        GyanithUser user = new GyanithUser("154asdad", "pushpavel", "pixel54", "jpushpavel@gmail.com", "8468494545", "NitPy");
        //user is dummy for now
        result.OnResult(user);
    }

    private static String buildUrl(String username,String password){
        return "https://gyanith.org/?userApirequest?" + username + password;//DUMMY FOR NOW (will be updated after backend)
    }


    private static void SaveGyanithUser(Context context, GyanithUser user) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.package_name), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(user);
        sp.edit().putString(context.getString(R.string.gyanithUserKey), json)
                .apply();
    }

    public static GyanithUser RetriveGyanithUser(Context context){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.package_name), Context.MODE_PRIVATE);
        String json = sp.getString(context.getString(R.string.gyanithUserKey),"");
        if (json.equals(""))
            return null;

        Gson gson = new Gson();
        return gson.fromJson(json,GyanithUser.class);
    }

    private class GyanithUserJsonResponse{
        public String username;
        public String name;
        public String email;
        public String gyanithId;
        public String phoneNumber;
        public String clg;

        public GyanithUserJsonResponse(){}
        //Other Fields will be updated following the backend
    }
}



interface OnLoginResult
    {
        public void OnResult(GyanithUser user);//RETURN NULL IF USER LOGIN FAILED

    }


