package com.barebrains.gyanith20.Statics;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.barebrains.gyanith20.Models.GyanithUser;
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


public class GyanithUserManager {

    private static GyanithUserManager instance;

    private static GyanithUser loggedUser;

    public static GyanithUserManager getInstance()
    {

        if (instance == null)
            instance = new GyanithUserManager();
        return instance;
    }

    public static GyanithUser getCurrentUser()
    {
        return loggedUser;
    }

    public static void setLoggedUser(GyanithUser user){
        if (user == null)
            return;
        loggedUser = user;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null)
            Log.d("asd","impossible Error");
    }
    public static void SignInUser(final Context context)//String username,String password,OnLoginResult result)
    {
        GyanithSignIn("", "", new OnLoginResult() {
            @Override
            public void OnResult(GyanithUser user) {
                loggedUser = user;
                SaveGyanithUser(context,user);

                FirebaseUserSignIn(user,"asdadsnfsknfd");
            }
        });
    }

    public static void FirebaseUserSignIn(final GyanithUser gyanithUser, final String password){

        FirebaseAuth.getInstance().signInWithEmailAndPassword(gyanithUser.email,password)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                    return;

               Class e = task.getException().getClass();

               if (e == FirebaseAuthInvalidCredentialsException.class)
                   Log.d("asd","Invalid Password");
               else if (e == FirebaseAuthInvalidUserException.class) {
                   FirebaseUserSignUp(gyanithUser, password, new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           Log.d("asd","New User " + task.isSuccessful());
                       }
                   });
               }
            }
        });

    }

    public static void FirebaseUserSignUp(final GyanithUser gyanithUser, String password, final OnCompleteListener<Void> completeListener){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(gyanithUser.email,password)
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

    private static void GyanithSignIn(final String username,final String password,OnLoginResult result){
        //login user with gyanith server()
        // after receiving json response create a gyanith user
        //and use result.OnResult(gyanithUser);

        GyanithUser user = new GyanithUser("154asdad","pushpavel","pixel54","jpushpavel@gmail.com","8468494545","NitPy");
        //user is dummy for now
        result.OnResult(user);
    }
    private static void SaveGyanithUser(Context context,GyanithUser user){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.package_name), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(user);
        sp.edit().putString(context.getString(R.string.gyanithUserKey),json)
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


    public interface OnLoginResult{
        public void OnResult(GyanithUser user);//RETURN NULL IF USER LOGIN FAILED

    }

}
