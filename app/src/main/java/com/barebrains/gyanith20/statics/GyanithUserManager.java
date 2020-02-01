package com.barebrains.gyanith20.statics;

import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.barebrains.gyanith20.gyanith20;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.models.SignUpDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.barebrains.gyanith20.gyanith20.sp;
import static com.barebrains.gyanith20.others.Response.ILLEGAL_STATE;


public class GyanithUserManager {

    private static final String GYANITH_USER_SP_KEY = "gyanithUser";
    private static String firebaseUserSessionToken;

    public static Resource<GyanithUser> loggedUser_value;

    private static MutableLiveData<Resource<GyanithUser>> loggedUser = new MutableLiveData<>();

    private static Observer<Resource<GyanithUser>> observer;

    public static LiveData<Resource<GyanithUser>> getCurrentUser(){
        return Transformations.map(loggedUser, new Function<Resource<GyanithUser>, Resource<GyanithUser>>() {
            @Override
            public Resource<GyanithUser> apply(Resource<GyanithUser> input) {

                if (observer == null) {
                    observer = new Observer<Resource<GyanithUser>>() {
                        @Override
                        public void onChanged(Resource<GyanithUser> resource) {
                            loggedUser_value = resource;
                        }
                    };
                    loggedUser.observeForever(observer);
                }

                if (input.value == null) {
                    return input;
                }

                if (FirebaseAuth.getInstance().getCurrentUser() == null)//FIREBASE NOT SIGNED IN
                    FirebaseUserSignIn(input.value);
                else if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(input.value.email)) {//FIREBASE DIFFERENT USER SIGNED IN
                    FirebaseAuth.getInstance().signOut();
                    FirebaseUserSignIn(input.value);
                }

                return input;
            }
        });
    }


    //Use this sign in only if all the traces of a user is removed
    public static void SignInUser(String username, String password) throws IllegalStateException
    {
        if (loggedUser_value != null && loggedUser_value.value != null)
            throw new IllegalStateException("SignInUser : Already user Signed in /IMPOSSIBLE");

        GetGyanithUserToken(username, password, new ResultListener<String>() {
            @Override
            public void OnResult(String token) {
                GyanithSignInWithToken(token);
            }

            @Override
            public void OnError(String error) {
                if (error.equals("not verified")) {
                    loggedUser.postValue(Resource.<GyanithUser>onlyCode(ILLEGAL_STATE));
                } else if (error.equals("invalid request"))//TODO: CHECK HERE
                    loggedUser.postValue(Resource.<GyanithUser>onlyToasts("Invalid Credentials"));
                else
                    loggedUser.postValue(Resource.<GyanithUser>onlyToasts(error));
            }
        });
    }

    public static void SignInReturningUser() throws IllegalStateException {
        final GyanithUser user = RetriveGyanithUser();
        if (user == null) {
            SignOutUser(null);
            return;//NO SAVED USER FOUND
        }
        GyanithSignInWithToken(user.token);
    }

    private static void GetGyanithUserToken(final String username, final String password, final ResultListener<String> result){

        JsonObjectRequest userTokenRequest = new JsonObjectRequest
                (Request.Method.GET, buildTokenRequestUrl(username,password), null, new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.has("token"))
                                result.OnResult(response.getString("token"));
                            else if (response.has("error")) {
                                    result.OnError(response.getString("error"));
                            } else
                                result.OnError("Internal Error");

                        } catch (JSONException e) {
                            result.OnError("Internal Error");
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        result.OnError("No Internet");
                        error.printStackTrace();
                    }
                });

        VolleyManager.requestQueue.add(userTokenRequest);
    }

    private static void GyanithSignInWithToken(final String token) {
        JsonObjectRequest userInfoRequest = new JsonObjectRequest(Request.Method.GET,buildUserInfoRequestUrl(token), null
                ,new com.android.volley.Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                if (response.has("usr")) {//SUCCESS
                    GyanithUser user = Util.jsonToGyanithUser(response.toString(),token);
                    SaveGyanithUser(user);


                    StartNewUserSession(user.gyanithId);
                    webLogin(user.gyanithId,user.token);

                    loggedUser.postValue(Resource.withValue(user));

                    Log.d("asd","User Signed In");
                } else if (response.has("reg"))//TODO:CHECK HERE
                {//TOKEN EXPIRED
                    SignOutUser("User Session Expired");
                    Log.d("asd","User Session Expired");
                } else {
                    loggedUser.postValue(Resource.<GyanithUser>onlyToasts("Internal Error"));
                    Log.d("asd","Server Error");

                }
            }
        },new com.android.volley.Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                GyanithUser user = RetriveGyanithUser();
                loggedUser.postValue(Resource.withValue(user));
            }
        });
        VolleyManager.requestQueue.add(userInfoRequest);
    }

    public static void GyanithUserSignUp(final SignUpDetails details, final CompletionListener listener){

        String url = "http://gyanith.org/api.php?action=signup&key=2ppagy0";

        StringRequest signUpRequest = new StringRequest(Request.Method.POST,url,new com.android.volley.Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    String result = obj.getString("result");
                    if (result.equals("success"))
                        listener.OnComplete();
                    else
                        listener.OnError(obj.getString("body"));

                } catch (JSONException e) {
                    listener.OnError("Internal Error");
                    e.printStackTrace();
                }

            }
        },new com.android.volley.Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.OnError("Network Error");
            }
        }){
            @Override
            protected Map<String, String> getParams() {
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
                           // PostManager.Initialize();
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


    private static void StartNewUserSession(String gyanithId){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(gyanithId).child("token");
        firebaseUserSessionToken = ref.push().getKey();
        ref.setValue(firebaseUserSessionToken).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && !dataSnapshot.getValue(String.class).equals(firebaseUserSessionToken))
                                SignOutUser("Another Device Logged with this Account");//TODO:CHANGE THIS MESSAGE
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            }
        });

    }

    private static void webLogin(@NonNull String gyid,@NonNull String token){
        String url = "http://gyanith.org/verify.php?code=" + token + "&gyid=" + gyid;

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //SUCCESS
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("asd","webLogin Error : " + error.getMessage());
            }
        });

        VolleyManager.requestQueue.add(request);
    }

    //save user of the current session even in offline mode
    private static void SaveGyanithUser(GyanithUser user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        sp.edit().putString(GYANITH_USER_SP_KEY, json).apply();
    }

    //Should be called to get the user if it exists from prefs
    private static GyanithUser RetriveGyanithUser(){
        String json = sp.getString(GYANITH_USER_SP_KEY,"");
        if (json.equals(""))
            return null;
        Gson gson = new Gson();
        return gson.fromJson(json,GyanithUser.class);
    }

    //Completely removes the trace of a user
    public static void SignOutUser(String toast){
        if (loggedUser_value != null && loggedUser_value.value == null)
            return;
        sp.edit().remove(GYANITH_USER_SP_KEY).apply();
        FirebaseAuth.getInstance().signOut();
        CookieManager.getInstance().removeAllCookie();
        loggedUser.postValue(Resource.<GyanithUser>withValue(null));
        if (toast != null)
            Toast.makeText(gyanith20.appContext, toast, Toast.LENGTH_SHORT).show();
    }
}



