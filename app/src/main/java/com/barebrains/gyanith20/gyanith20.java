package com.barebrains.gyanith20;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.barebrains.gyanith20.Models.GyanithUser;
import com.barebrains.gyanith20.Models.Post;
import com.barebrains.gyanith20.Statics.GyanithUserManager;
import com.barebrains.gyanith20.Statics.PostManager;
import com.google.firebase.database.FirebaseDatabase;

public class gyanith20 extends Application {
    public static final String PROGRESS_CHANNEL = "progress";
    @Override
    public void onCreate() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        CreateProgressNotificationChannel();
        HandleUserManagement();
        super.onCreate();
    }

    private void HandleUserManagement(){
        GyanithUserManager.SignInUser(this,",","",null);
        GyanithUser user = GyanithUserManager.RetriveGyanithUser(this);
       if (user != null) {
           GyanithUserManager.setLoggedUser(user);
           PostManager.getInstance().Initialize();
       }
    }

    private void CreateProgressNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "Indicates the progress of posting";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(PROGRESS_CHANNEL, "Posting progress", importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
}
