package com.barebrains.gyanith20.models;

import com.google.firebase.database.Exclude;

public class ScheduleItem {

    public String venue,title,id = null;
    public Long start_time,end_time;

    public ScheduleItem() {
    }

@Exclude
    public boolean isLive(){
        Long time = System.currentTimeMillis();
        return  time >= start_time && time <= end_time;
    }
}
