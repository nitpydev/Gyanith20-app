package com.barebrains.gyanith20.models;

public class eventitem {

    String name,time,tag,type;

    public void setType(String type) {
        this.type = type;
    }

    public eventitem(String name, String time , String tag) {
        this.name = name;
        this.time = time;
        this.tag = tag;

    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getTag() {
        return tag;
    }


}
