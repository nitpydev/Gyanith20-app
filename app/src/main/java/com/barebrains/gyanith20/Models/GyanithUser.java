package com.barebrains.gyanith20.Models;

public class GyanithUser {
    public String gyanithId;
    public String name;
    public String userName;
    public String password;
    public String email;
    public String phoneNo;
    public String clg;

    public GyanithUser(String gyanithId,String name,String userName,String email,String phoneNo,String clg){
        this.gyanithId = gyanithId;
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.clg = clg;
    }

}
