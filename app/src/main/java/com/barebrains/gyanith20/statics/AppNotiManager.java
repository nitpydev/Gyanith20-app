package com.barebrains.gyanith20.statics;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.barebrains.gyanith20.activities.SplashActivity;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.gyanith20;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.NotificationItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppNotiManager {

    //PROGRESS NOTIFICATION
    private static int PROGRESS_MAX;
    private static final int PROGRESS_NID = 65;
    private static Map<Integer,NotificationCompat.Builder> builders;
    private static Map<Integer,NotificationManagerCompat> managers;

    public static void Create(Service service,int PROGRESS_MAX){
        AppNotiManager.PROGRESS_MAX = PROGRESS_MAX;
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

    public static Map<Integer,ResultListener<NotificationItem[]>> listeners = new HashMap<>();

    public static NotificationItem[] notiItems;

    public static void addNotificationListener(Integer code,ResultListener<NotificationItem[]> listener){
        listeners.put(code,listener);
        if (notiItems != null)
            listener.OnResult(notiItems);
    }

    public static void removeNotificationListener(Integer code){
        listeners.remove(code);
    }

    public static void initNotifications(){
        FirebaseDatabase.getInstance().getReference().child("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<NotificationItem> items = new ArrayList<>();

                        for (DataSnapshot snap : dataSnapshot.getChildren())
                            items.add(snap.getValue(NotificationItem.class));

                        notiItems = items.toArray(new NotificationItem[0]);

                        for (ResultListener<NotificationItem[]> listener : listeners.values())
                            listener.OnResult(notiItems);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (!NetworkManager.getInstance().isNetAvailable()) {
                            for (ResultListener<NotificationItem[]> listener : listeners.values())
                                listener.OnError("No Internet");
                        }
                        else
                        {
                            for (ResultListener<NotificationItem[]> listener : listeners.values()) {
                                notiItems = new NotificationItem[0];
                                listener.OnResult(notiItems);
                            }
                        }
                    }
                });
    }
}