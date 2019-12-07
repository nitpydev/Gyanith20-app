package com.barebrains.gyanith20.Statics;

import com.barebrains.gyanith20.Models.GyanithUser;

import java.util.concurrent.CompletableFuture;

public class GyanithUserManager {
    private static GyanithUserManager instance;
    private static GyanithUser loggedUser;

    public static GyanithUserManager getInstance(){
        if (instance == null)
            instance = new GyanithUserManager();
        return instance;
    }

    public static GyanithUser getLoggedUser(){
        return loggedUser;
    }

    public static void LoginUser(String username,String password,OnLoginResult callback)
    {
        //USE THIS USERNAME PASSWORD TO CREATE VOLLEY JSON REQUEST
        //AND CREATE A GyanithUser Object and call callback.OnResult() passing the created object

    }

    public interface OnLoginResult{
        public void OnResult(GyanithUser user);//RETURN NULL IF USER LOGIN FAILED

    }

}
