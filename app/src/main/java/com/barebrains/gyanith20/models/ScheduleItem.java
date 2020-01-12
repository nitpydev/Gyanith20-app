package com.barebrains.gyanith20.models;

public class ScheduleItem {

    public String venue,title,id = null;
    public Long start_time,end_time;

    public ScheduleItem() {
    }


    public boolean isLive(){
        Long time = System.currentTimeMillis();
        return  time >= start_time && time <= end_time;
    }
}
