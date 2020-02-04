package com.barebrains.gyanith20.models;

import com.google.firebase.database.Exclude;

import java.security.InvalidParameterException;

public class NotificationItem {
    public String title, body;
    public long time;

    @Exclude
    public String key;

    public NotificationItem(String title, long time, String body) throws InvalidParameterException {

        if (title.isEmpty() || body.isEmpty())
            throw new InvalidParameterException("Fill all details");
        this.title = title;
        this.time = time;
        this.body = body;
    }


    public NotificationItem(){}//Empty Constructor
}
