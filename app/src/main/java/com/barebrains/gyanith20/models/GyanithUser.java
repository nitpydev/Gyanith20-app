package com.barebrains.gyanith20.models;

import android.util.Log;

import java.util.ArrayList;

public class GyanithUser {
    public String gyanithId;
    public String name;
    public String userName;
    public String email;
    public String phoneNo;
    public String clg;
    public String token;
    public boolean verified;
    public ArrayList<EventItem> reg_te;
    public ArrayList<EventItem> reg_w;

    public GyanithUser(String gyanithId, String name, String userName, String email, String phoneNo, String clg, ArrayList<EventItem> reg_te, ArrayList<EventItem> reg_w, boolean verified, String token){
        this.gyanithId = gyanithId;
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.clg = clg;
        this.token = token;
        this.reg_te = reg_te;
        this.reg_w = reg_w;
        this.verified = verified;
    }

}
