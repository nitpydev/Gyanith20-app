package com.barebrains.gyanith20.statics;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.interfaces.AuthStateListener;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class GyanithUserManager {

    private static GyanithUserManager instance;

    private static GyanithUser loggedUser;

    public static GyanithUserManager getInstance() {

        if (instance == null)
            instance = new GyanithUserManager();

        if (instance.userStateListeners == null)
            instance.userStateListeners = new HashMap<>();

        if (instance.userStateListenersUnMapped == null)
            instance.userStateListenersUnMapped = new ArrayList<>();
        return instance;
    }

    public static GyanithUser getCurrentUser() {
        return loggedUser;
    }

    public static void SignInUser(final Context context, String username, String password, final ResultListener<GyanithUser> result) throws IllegalStateException
    {
        if (loggedUser != null)
            throw new IllegalStateException();

        GetGyanithUserToken(context,username, password, new ResultListener<String>() {
            @Override
            public void OnResult(String token) {
                if (token == null){
                    result.OnResult(null);//Invalid Credentials
                    return;
                }
                GyanithSignInWithToken(context, token, new ResultListener<GyanithUser>() {
                    @Override
                    public void OnResult(GyanithUser gyanithUser) {
                        if (gyanithUser == null) {
                            SignOutUser(context);//Token Expired
                            return;
                        }
                        loggedUser = gyanithUser;
                        SaveGyanithUser(context,loggedUser);
                        result.OnResult(gyanithUser);
                    }
                });
            }
        });
    }

    public static void SignInReturningUser(final Context context, final ResultListener<GyanithUser> result) throws IllegalStateException {
        GyanithUser user = RetriveGyanithUser(context);
        if (user == null)
            throw new IllegalStateException();

        GyanithSignInWithToken(context, user.token, new ResultListener<GyanithUser>() {
            @Override
            public void OnResult(GyanithUser gyanithUser) {
                if (gyanithUser == null) {
                    SignOutUser(context);
                    result.OnResult(null);
                    return;
                }

                loggedUser = gyanithUser;
                SaveGyanithUser(context,gyanithUser);
                result.OnResult(loggedUser);
            }
        });
    }

    private static void FirebaseUserSignIn(final GyanithUser gyanithUser) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(gyanithUser.email, gyanithUser.gyanithId)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            PostManager.getInstance().Initialize();
                            return;
                        }

                        Class e = task.getException().getClass();

                        if (e == FirebaseAuthInvalidCredentialsException.class)
                            Log.d("asd", "FirebaseAuth : Invalid Password");
                        else if (e == FirebaseAuthInvalidUserException.class) {
                            FirebaseUserSignUp(gyanithUser, gyanithUser.gyanithId, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("asd", "FirebaseAuth : New User " + task.isSuccessful());
                                }
                            });
                        }
                    }
                });

    }

    private static void FirebaseUserSignUp(final GyanithUser gyanithUser, String password, final OnCompleteListener<Void> completeListener) {
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

    private static void GetGyanithUserToken(Context context, final String username, final String password, final ResultListener<String> result){

        JsonObjectRequest userTokenRequest = new JsonObjectRequest
                (Request.Method.GET, buildTokenRequestUrl(username,password), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.has("token"))
                                result.OnResult(response.getString("token"));
                            else if (response.has("error"))
                                result.OnResult(null);
                            else
                                throw new UnknownError();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(context).add(userTokenRequest);
    }

    private static void GyanithSignInWithToken(Context context, final String token, final ResultListener<GyanithUser> callback) {

        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest userInfoRequest = new JsonObjectRequest(Request.Method.GET,buildUserInfoRequestUrl(token), null
                ,new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                if (response.has("user"))
                {
                    Log.d("asd","Token user");
                    callback.OnResult(Util.jsonToGyanithUser(response.toString(),token));
                }
                else if (response.has("error"))
                {
                    callback.OnResult(null);
                }
                Log.d("asd","no error");
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("asd","error");
                error.printStackTrace();
            }
        });
        userInfoRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(userInfoRequest);
    }

    private static String buildTokenRequestUrl(String username, String password){
        return "http://gyanith.org/api.php?action=login&key=2ppagy0&uname=" + username + "&pwd=" + Util.sha1(password);
    }
    private static String buildUserInfoRequestUrl(String token){
        return "http://gyanith.org/api.php?action=login&key=2ppagy0&token=" + token;
    }

    private static void SaveGyanithUser(Context context, GyanithUser user) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.package_name), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(user);
        sp.edit().putString(context.getString(R.string.gyanithUserKey), json)
                .apply();
        loggedUser = user;
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            FirebaseUserSignIn(user);
        else if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(loggedUser.email)) {
            FirebaseAuth.getInstance().signOut();
            FirebaseUserSignIn(loggedUser);
        }
        AuthStateChanged();
    }

    private static GyanithUser RetriveGyanithUser(Context context){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.package_name), Context.MODE_PRIVATE);
        String json = sp.getString(context.getString(R.string.gyanithUserKey),"");
        if (json.equals(""))
            return null;
        Gson gson = new Gson();
        return gson.fromJson(json,GyanithUser.class);
    }

    private static void SignOutUser(Context context){
        if (loggedUser == null)
            return;
        AuthStateChanged();
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.package_name), Context.MODE_PRIVATE);
        sp.edit().remove(context.getString(R.string.gyanithUserKey)).apply();
        FirebaseAuth.getInstance().signOut();
        loggedUser = null;
    }

    public static boolean resolveUserState(Context context){
        if (loggedUser == null) {
            SignOutUser(context);
            return false;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            FirebaseUserSignIn(loggedUser);

        return true;
    }

    private Map<Integer, AuthStateListener> userStateListeners;
    private ArrayList<AuthStateListener> userStateListenersUnMapped;

    public static void addAuthStateListner(Integer code, AuthStateListener listener){
        listener.onChange();
        if (loggedUser == null)
            listener.NullUser();
        else if (loggedUser.verified)
            listener.VerifiedUser();
        else
            listener.UnVerifiedUser();
        getInstance().userStateListeners.put(code,listener);
    }

    public static void removeAuthStateListener(Integer code){
        getInstance().userStateListeners.remove(code);
    }

    public static void removeAuthStateListener(AuthStateListener listener){
        getInstance().userStateListenersUnMapped.remove(listener);
    }

    public static void addAuthStateListener(AuthStateListener listener){
        listener.onChange();
        if (loggedUser == null)
            listener.NullUser();
        else if (loggedUser.verified)
            listener.VerifiedUser();
        else
            listener.UnVerifiedUser();
        getInstance().userStateListenersUnMapped.add(listener);
    }

    private static void AuthStateChanged(){
        for (AuthStateListener listener : getInstance().userStateListeners.values()) {
            listener.onChange();
            if (loggedUser == null)
                listener.NullUser();
            else if (loggedUser.verified)
                listener.VerifiedUser();
            else
                listener.UnVerifiedUser();
        }
    }
}

class GyanithUserJsonResponse{
    public String username;
    public String name;
    public String email;
    public String gyanithId;
    public String phoneNumber;
    public String clg;
    public String token;

    public GyanithUserJsonResponse(){}
    //Other Fields will be updated following the backend
}

