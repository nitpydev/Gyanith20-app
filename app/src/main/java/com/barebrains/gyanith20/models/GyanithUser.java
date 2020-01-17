package com.barebrains.gyanith20.models;

import android.util.Log;

import java.util.ArrayList;

public class GyanithUser {
    public boolean trustedToken = true;

    public String gyanithId;
    public String name;
    public String userName;
    public String email;
    public String phoneNo;
    public String gender;
    public String clg;
    public String token;
    public ArrayList<String> regEventIds;

    public GyanithUser(String gyanithId, String name, String userName, String email, String phoneNo,String gender, String clg,ArrayList<String> regEventIds, String token){
        this.gyanithId = gyanithId;
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.clg = clg;
        this.regEventIds = regEventIds;
        setGender(gender);
        this.token = token;
    }


    private void setGender(String code){
        switch (code){
            case "m":
                gender = "Male";
                break;
            case "f":
                gender = "Female";
                break;
            case "o":
                gender = "Other";
                break;
            default:
                gender = "invalid";
        }
    }
}
