package com.barebrains.gyanith20.Statics;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import com.barebrains.gyanith20.Models.GyanithUser;

import java.security.PrivateKey;
import java.util.concurrent.CompletableFuture;

public class GyanithUserManager<Private> {

    //shared_preference constants
    private static final String SHARED_PREF_NAME = "gyanith_pref";
    private static final String KEY_USERNAME = "Keyusername"


    private static GyanithUserManager instance;
    private Context cntx;

    //constructor
    private GyanithUserManager(Context context)
    {
        cntx = context;
    }


    public static synchronized GyanithUserManager getInstance()
    {

        if (instance == null)
            instance = new GyanithUserManager(context);
        return instance;
    }

    public  GyanithUser getLoggedUser()
    {
        SharedPreferences sharedPreferences = cntx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return new GyanithUser(SharedPreferences.getString( KEY_USERNAME,null ));
    }

    public void LoginUser()
    {

       SharedPreferences sharedPreferences = cntx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = sharedPreferences.edit();
       editor.putString(KEY_USERNAME, GyanithUser.getUserName());
       editor.apply();

        //USE THIS USERNAME PASSWORD TO CREATE VOLLEY JSON REQUEST
        //AND CREATE A GyanithUser Object and call callback.OnResult() passing the created object

    }

    public boolean isLoggedin()
    {
        SharedPreferences sharedPreferences = cntx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME,null) != null;
    }



}
