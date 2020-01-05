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
    public ArrayList<EventItem> reg_w = new ArrayList<>();
    public ArrayList<EventItem> reg_te = new ArrayList<>();

    public GyanithUser(String gyanithId, String name, String userName, String email, String phoneNo, String clg, String token){
        this.gyanithId = gyanithId;
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.clg = clg;
        this.token = token;
    }

}
