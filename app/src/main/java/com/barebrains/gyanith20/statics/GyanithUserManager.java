package com.barebrains.gyanith20.statics;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.interfaces.AuthStateListener;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.models.SignUpDetails;
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

    //Use this sign in only if all the traces of a user is removed
    public static void SignInUser(final Context context, String username, String password, final ResultListener<GyanithUser> result) throws IllegalStateException
    {
        if (loggedUser != null)
            throw new IllegalStateException("SignInUser : Already user Signed in");

        GetGyanithUserToken(username, password, new ResultListener<String>() {
            @Override
            public void OnResult(String token) {

                GyanithSignInWithToken(context, token, new ResultListener<GyanithUser>() {
                    @Override
                    public void OnResult(GyanithUser gyanithUser) {
                        loggedUser = gyanithUser;
                        SaveGyanithUser(context,loggedUser);
                        result.OnResult(gyanithUser);
                    }

                    @Override
                    public void OnError(String error) {
                        if (error.equals("")){//Implies Token Expired
                            SignOutUser(context);
                            result.OnError("User Session Expired");
                        }
                        else
                            result.OnError(error);
                    }
                });
            }

            @Override
            public void OnError(String error) {
                result.OnError(error);
            }
        });
    }

    public static void SignInReturningUser(final Context context, final ResultListener<GyanithUser> result) throws IllegalStateException {
        final GyanithUser user = RetriveGyanithUser(context);
        if (user == null)
            throw new IllegalStateException();
        GyanithSignInWithToken(context, user.token, new ResultListener<GyanithUser>() {
            @Override
            public void OnResult(GyanithUser gyanithUser) {
                if (gyanithUser == null) {//Implies Token Expired
                    SignOutUser(context);
                    result.OnError("User Session Expired");
                    return;
                }

                loggedUser = gyanithUser;
                SaveGyanithUser(context,gyanithUser);
                result.OnResult(loggedUser);
            }

            @Override
            public void OnError(String error) {
                loggedUser = user;
                result.OnError(error);
            }
        });
    }

    private static void GetGyanithUserToken(final String username, final String password, final ResultListener<String> result){

        JsonObjectRequest userTokenRequest = new JsonObjectRequest
                (Request.Method.GET, buildTokenRequestUrl(username,password), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.has("token"))
                                result.OnResult(response.getString("token"));
                            else if (response.has("error"))
                                result.OnError(response.getString("error"));

                        } catch (JSONException e) {
                            result.OnError("Internal Error");
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        result.OnError("Internal Error");
                        error.printStackTrace();
                    }
                });

        VolleyManager.requestQueue.add(userTokenRequest);
    }

    private static void GyanithSignInWithToken(final Context context, final String token, final ResultListener<GyanithUser> callback) {

        RequestQueue requestQueue = VolleyManager.requestQueue;
        JsonObjectRequest userInfoRequest = new JsonObjectRequest(Request.Method.GET,buildUserInfoRequestUrl(token), null
                ,new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.has("usr"))
                        callback.OnResult(Util.jsonToGyanithUser(response.toString(), token));
                    else if (response.has("error"))
                        callback.OnError(response.getString("error"));
                    else
                        callback.OnError("Internal Error");

                } catch (JSONException e) {
                    callback.OnError("Internal Error");
                }
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.OnError("Network Error");
            }
        });

        userInfoRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(userInfoRequest);
    }

    public static void GyanithUserSignUp(final SignUpDetails details, final CompletionListener listener){

        String url = "http://gyanith.org/api.php?action=signup&key=2ppagy0";

        StringRequest signUpRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                    String result = obj.getString("result");
                    if (result.equals("success"))
                        listener.OnComplete();
                    else
                        listener.OnError(obj.getString("text"));

                } catch (JSONException e) {
                    listener.OnError("Internal Error");
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.OnError("Network Error");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("pname",details.name);
                params.put("usr",details.usrname);
                params.put("clg",details.clg);
                params.put("gdr",details.gender);
                params.put("email",details.email);
                params.put("pswd1",details.pwd);
                params.put("phone",details.phn);

                return params;
            }
        };

        VolleyManager.requestQueue.add(signUpRequest);

    }

    private static String buildTokenRequestUrl(String username, String password){
        return "http://gyanith.org/api.php?action=login&key=2ppagy0&uname=" + username + "&pwd=" + Util.sha1(password);
    }
    private static String buildUserInfoRequestUrl(String token){
        return "http://gyanith.org/api.php?action=login&key=2ppagy0&token=" + token;
    }

    private static void FirebaseUserSignIn(final GyanithUser gyanithUser) {
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            return;

        FirebaseAuth.getInstance().signInWithEmailAndPassword(gyanithUser.email, gyanithUser.gyanithId + "extra123!@#")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            PostManager.getInstance().Initialize();
                            return;
                        }

                        Exception e = task.getException();

                        if (e instanceof FirebaseAuthInvalidCredentialsException)
                            Log.d("asd", "FirebaseAuth : Invalid Password");
                        else if (e instanceof FirebaseAuthInvalidUserException) {
                            FirebaseUserSignUp(gyanithUser, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                                        Log.d("asd", "FirebaseAuth : New User Created");
                                    else
                                        Log.d("asd","FirebaseAuth : New User Creation Error : " + task.getException());

                                }
                            });
                        }
                    }
                });

    }

    private static void FirebaseUserSignUp(final GyanithUser gyanithUser, final OnCompleteListener<Void> completeListener) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(gyanithUser.email, gyanithUser.gyanithId + "extra123!@#")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("asd",  task.isSuccessful() + " " + task.getException());
                        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                        root.child("users").child(gyanithUser.gyanithId).child("postCount").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                completeListener.onComplete(task);
                            }
                        });
                    }
                });
    }


    //Should be called to save a user with new token
    private static void SaveGyanithUser(Context context, GyanithUser user) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.package_name), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(user);
        sp.edit().putString(context.getString(R.string.gyanithUserKey), json).apply();
        loggedUser = user;
        resolveUserState(context);
        AuthStateChanged();
    }

    //Should be called to get the user if it exists from prefs
    private static GyanithUser RetriveGyanithUser(Context context){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.package_name), Context.MODE_PRIVATE);
        String json = sp.getString(context.getString(R.string.gyanithUserKey),"");
        if (json.equals(""))
            return null;
        Gson gson = new Gson();
        return gson.fromJson(json,GyanithUser.class);
    }

    //Completely removes the trace of a user
    public static void SignOutUser(Context context){
        if (loggedUser == null)
            return;
        AuthStateChanged();
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.package_name), Context.MODE_PRIVATE);
        sp.edit().remove(context.getString(R.string.gyanithUserKey)).apply();
        FirebaseAuth.getInstance().signOut();
        loggedUser = null;
    }

    //Handles conflicts with firebase login and stale auth state
    public static boolean resolveUserState(final Context context){
        if (loggedUser == null) {
            SignOutUser(context);
            return false;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            FirebaseUserSignIn(loggedUser);
        else if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(loggedUser.email)) {
            FirebaseAuth.getInstance().signOut();
            FirebaseUserSignIn(loggedUser);
        }

        return true;
    }

    private Map<Integer, AuthStateListener> userStateListeners;
    private ArrayList<AuthStateListener> userStateListenersUnMapped;

    public static void addAuthStateListner(Integer code, AuthStateListener listener){
        listener.onChange();
        if (loggedUser == null)
            listener.NullUser();
        else
            listener.VerifiedUser();
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
        else
            listener.VerifiedUser();
        getInstance().userStateListenersUnMapped.add(listener);
    }

    private static void AuthStateChanged(){
        for (AuthStateListener listener : getInstance().userStateListeners.values()) {
            listener.onChange();
            if (loggedUser == null)
                listener.NullUser();
            else
                listener.VerifiedUser();
        }
        for (AuthStateListener listener : getInstance().userStateListenersUnMapped) {
            listener.onChange();
            if (loggedUser == null)
                listener.NullUser();
            else
                listener.VerifiedUser();
        }


    }
}



