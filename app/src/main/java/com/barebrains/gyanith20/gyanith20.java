package com.barebrains.gyanith20;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.multidex.MultiDexApplication;

import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.models.NotificationItem;
import com.barebrains.gyanith20.statics.DataRepository;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.LikesSystem;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.barebrains.gyanith20.statics.PostManager;
import com.barebrains.gyanith20.statics.VolleyManager;
import com.google.firebase.database.FirebaseDatabase;

public class gyanith20 extends MultiDexApplication {
    public static final String PROGRESS_CHANNEL = "progress";
    public static SharedPreferences sp;
    public static Context appContext;
    @Override
    public void onCreate() {
        //APP BASICS INITIATIONS
        sp = getSharedPreferences(getString(R.string.package_name),MODE_PRIVATE);
        appContext = getApplicationContext();
        setScreenOrientation();
        CreateProgressNotificationChannel();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        //NETWORKING INITIATIONS
        VolleyManager.setRequestQueue(this);
        NetworkManager.init(getApplicationContext());
        PostManager.StartListeningPostCount();

        //USER SYSTEM INITIATIONS
        GyanithUserManager.SignInReturningUser();
        LikesSystem.fetchLikedPosts();


        //INITIATION TO FETCH DATA
        DataRepository.getAllEventItems().observeForever(new Observer<ArrayResource<EventItem>>() {
            @Override
            public void onChanged(ArrayResource<EventItem> eventItemArrayResource) {
                DataRepository.getAllEventItems().removeObserver(this);
            }
        });

        DataRepository.getAllNotiItems().observeForever(new Observer<ArrayResource<NotificationItem>>() {
            @Override
            public void onChanged(ArrayResource<NotificationItem> notificationItemArrayResource) {
                DataRepository.getAllNotiItems().removeObserver(this);
            }
        });
        DataRepository.fetchClgFeverUrl();
        super.onCreate();
    }
    private void setScreenOrientation(){
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
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
