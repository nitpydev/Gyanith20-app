package com.barebrains.gyanith20.models;

import android.util.Log;

public class EventItem {

    public String id;
    public String name;
    public String des;
    public String rules;
    public String contact;
    public String img1;
    public String img2;
    public String cost;
    private String max_ptps;
    public String type;
    public String timestamp;
    public String wid;
    public String eid;

    public  EventItem(){}//PRIVATE CONSTRUCTOR

    public void setMax_ptps(String max_ptps){
        Log.d("asd","laos");
        this.max_ptps = (max_ptps != null)?max_ptps:"1";
    }

    public String getMax_ptps(){
        return max_ptps;
    }
}

