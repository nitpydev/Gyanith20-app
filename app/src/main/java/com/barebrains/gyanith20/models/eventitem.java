package com.barebrains.gyanith20.models;

public class eventitem {

    private String name,time, tag , img2;
    public String type;

    public void setType(String type) {
        this.type = type;
    }



    public void setImg2(String img2) { this.img2 = img2;}

    public eventitem(String name, String time , String tag) {
        this.name = name;
        this.time = time;
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }





    public String getImg2() { return img2;}
}

