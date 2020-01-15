package com.barebrains.gyanith20.statics;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.barebrains.gyanith20.activities.MainActivity;
import com.barebrains.gyanith20.activities.SplashActivity;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.gyanith20;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.NotificationItem;
import com.barebrains.gyanith20.services.PostUploadService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class AppNotiManager {

    //PROGRESS NOTIFICATION
    private static Map<Integer,Integer> PROGRESS_MAXS = new HashMap<>();
    private static Map<Integer,NotificationCompat.Builder> builders = new HashMap<>();
    private static NotificationManagerCompat manager;
    private static int serviceNotificationId;
    private static NotificationCompat.Builder groupBuilder;

    public static void Create(Service service,int id,long PROGRESS_MAX){
        PROGRESS_MAXS.put(id,(int) PROGRESS_MAX);

        if (manager == null)
            manager = NotificationManagerCompat.from(service);

        if (groupBuilder == null){
            groupBuilder = new NotificationCompat.Builder(service,gyanith20.PROGRESS_CHANNEL)
                            .setContentTitle("Gyanith Community")
                            .setSmallIcon(R.drawable.l2)
                            .setGroupSummary(true)
                            .setGroup(service.getString(R.string.package_name));
            manager.notify(172, groupBuilder.build());
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(service, gyanith20.PROGRESS_CHANNEL);

        builder.setSmallIcon(R.drawable.l2)
                .setContentTitle("Posting")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(false)
                .setOngoing(true)
                .setGroup(service.getString(R.string.package_name))
                .setContentIntent(PendingIntent.getActivity(service, 0,
                        new Intent(service, SplashActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));

        builders.put(id,builder);



        service.startForeground(id,builder.build());
        serviceNotificationId = id;
    }
    public static void setProgress(int id,long CURR_PROGRESS){
        NotificationCompat.Builder builder = builders.get(id);
        Integer max = PROGRESS_MAXS.get(id);
        if (builder == null || max == null)
            return;
        builder.setProgress(max,(int)CURR_PROGRESS,false);
        manager.notify(id,builder.build());
    }

    private static void removeProgress(int id){
       setProgress(id,0);
    }
    public static void setProgressTitle(int id,String title){
        NotificationCompat.Builder builder = builders.get(id);

        if (builder == null)
            return;
        builder.setContentTitle(title);
        manager.notify(id, builder.build());
    }

    public static void finishNotification(Service service,int id){
        NotificationCompat.Builder builder = builders.get(id);

        if (builder == null)
            return;
        builder.setAutoCancel(true).setOngoing(false);
        removeProgress(id);

        if (serviceNotificationId == id && builders.size() != 1) {
            int otherId = getOtherBuilderId(id);
            builder = builders.get(otherId);
            if (builder == null)
                return;
            service.startForeground(id,builder.build());
        }


        manager.notify(id,builder.build());
        builders.remove(id);
    }

    private static int getOtherBuilderId(int id) throws IllegalStateException{
        Iterator<Integer> itr = builders.keySet().iterator();
        for (int i=0;i < builders.keySet().size();i++){
            int key = itr.next();
            if (key != id)
                return key;
        }
        throw new IllegalStateException("No other notification found,this is an impossible error");
    }
}