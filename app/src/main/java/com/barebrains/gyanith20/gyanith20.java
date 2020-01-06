package com.barebrains.gyanith20;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.AppNotiManager;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.barebrains.gyanith20.statics.PostManager;
import com.barebrains.gyanith20.statics.VolleyManager;
import com.barebrains.gyanith20.statics.eventsManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;

public class gyanith20 extends Application {
    public static final String PROGRESS_CHANNEL = "progress";
    @Override
    public void onCreate() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        VolleyManager.setRequestQueue(this);
        NetworkManager.initialize(this);
        setScreenOrientation();
        CreateProgressNotificationChannel();
        AppNotiManager.initNotifications();
        HandleUserManagement();
        eventsManager.initialize(getApplicationContext());
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

    private void HandleUserManagement(){
        try {
            GyanithUserManager.SignInReturningUser(this, new ResultListener<GyanithUser>() {
                @Override
                public void OnResult(GyanithUser gyanithUser) {
                    Log.d("asd","user return successful");
                    if (gyanithUser != null && FirebaseAuth.getInstance().getCurrentUser() != null)
                        PostManager.getInstance().Initialize();
                }

                @Override
                public void OnError(String error) {
                    Log.d("asd","SignInReturningUser : " + error);
                }
            });
        } catch (IllegalStateException e) {
            Log.d("asd","No Saved User Found");//No Saved User Found
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
