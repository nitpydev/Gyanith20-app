package com.barebrains.gyanith20.Models;

public class GyanithUser {
    private static String userName;

    public void User(String userName)
    {
        this.userName = userName;
    }
//getter
    public static String getUserName() {
        return userName;
    }
}
