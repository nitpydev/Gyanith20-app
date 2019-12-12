package com.barebrains.gyanith20.Statics;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.barebrains.gyanith20.Activities.SplashActivity;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.gyanith20;

import java.util.HashMap;
import java.util.Map;

public class NotificationManager {
    private static NotificationManager instance;

    //PROGRESS NOTIFICATION
    private static int PROGRESS_MAX;
    private static final int PROGRESS_NID = 65;
    private static Map<Integer,NotificationCompat.Builder> builders;
    private static Map<Integer,NotificationManagerCompat> managers;

    public static NotificationManager getInstance(){
        if (instance == null)
            instance = new NotificationManager();
        return instance;
    }

    public static void Create(Service service,int PROGRESS_MAX){
        NotificationManager.PROGRESS_MAX = PROGRESS_MAX;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(service, gyanith20.PROGRESS_CHANNEL);
        builder.setSmallIcon(R.drawable.l2)
                .setContentTitle("Posting")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(false)
                .setContentIntent(PendingIntent.getActivity(service, 0,
                        new Intent(service, SplashActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        builders = new HashMap<>();
        managers = new HashMap<>();
        builders.put(PROGRESS_NID,builder);
        managers.put(PROGRESS_NID,NotificationManagerCompat.from(service));
        service.startForeground(PROGRESS_NID,builders.get(PROGRESS_NID).build());
    }
    public static void setProgress(int CURR_PROGRESS){
        builders.get(PROGRESS_NID).setProgress(PROGRESS_MAX,CURR_PROGRESS,false);
        managers.get(PROGRESS_NID).notify(PROGRESS_NID,builders.get(PROGRESS_NID).build());
    }
    public static void removeProgress(){
       PROGRESS_MAX =0;
       setProgress(0);
    }
    public static void setProgressTitle(String title){
        builders.get(PROGRESS_NID).setContentTitle(title);
        managers.get(PROGRESS_NID).notify(PROGRESS_NID, builders.get(PROGRESS_NID).build());
    }

    public static void CompleteProgress(){
        builders.get(PROGRESS_NID).setAutoCancel(true);
        removeProgress();
        setProgressTitle("Posted");
        managers.get(PROGRESS_NID).notify(11,builders.get(PROGRESS_NID).build());
        builders.remove(PROGRESS_NID);
        managers.remove(PROGRESS_NID);
    }
}
