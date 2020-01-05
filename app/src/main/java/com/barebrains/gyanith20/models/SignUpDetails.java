package com.barebrains.gyanith20.models;

import com.android.volley.toolbox.StringRequest;

import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpDetails {
    public String usrname;
    public String name;
    public String pwd;
    public String email;
    public String clg;
    public String phn;
    public String gender;


    public SignUpDetails(String usrname, String name, String pwd1, String pwd2, String email, String clg,String phn, String gender) throws InvalidParameterException
    {
        //Check Empty
        if (usrname.isEmpty() || name.isEmpty() || pwd1.isEmpty() || pwd2.isEmpty()||email.isEmpty()||clg.isEmpty())
            throw new InvalidParameterException("All Fields should be filled");

        //Check pwd match
        if (!pwd1.equals(pwd2))
            throw new InvalidParameterException("Password does not match");

        //Check email
        if (!isValid(email))
            throw new InvalidParameterException("Invalid Email ID");

        //Check phn
        if (phn.length() != 10)
            throw new InvalidParameterException("Invalid Phone Number");

        this.usrname = usrname;
        this.name = name;
        this.pwd = pwd1;
        this.email = email;
        this.clg = clg;
        this.phn = phn;
        this.gender = gender;
    }

    private static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

}
