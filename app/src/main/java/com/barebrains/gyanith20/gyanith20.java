package com.barebrains.gyanith20;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.PostManager;
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
        try {
            GyanithUserManager.SignInReturningUser(this, new ResultListener<GyanithUser>() {
                @Override
                public void OnResult(GyanithUser gyanithUser) {
                    if (gyanithUser == null)
                        Log.d("asd","User Token Expired");
                    else
                        Log.d("asd","user return successful");
                }
            });
        } catch (IllegalStateException e) {
            e.printStackTrace();//No Saved User Found
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
