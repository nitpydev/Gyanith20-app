package com.barebrains.gyanith20.models;

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
    public ArrayList<eventitem> reg_te;
    public ArrayList<eventitem> reg_w;

    public GyanithUser(String gyanithId,String name,String userName,String email,String phoneNo,String clg,ArrayList<eventitem> reg_te,ArrayList<eventitem> reg_w,boolean verified,String token){
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
