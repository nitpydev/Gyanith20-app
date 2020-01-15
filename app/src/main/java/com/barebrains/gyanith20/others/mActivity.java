package com.barebrains.gyanith20.others;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.services.PostUploadService;

public abstract class mActivity extends AppCompatActivity {
    //BINDING TO POST_UPLOAD_SERVICE FUNCTIONALITY

    private CompletionListener listener;
    private PostUploadService.PostBinder postBinder;

    //Should be with onCreate()
    protected void syncWithPostService(final CompletionListener listener){
        this.listener = listener;
        Intent bindingIntent = new Intent(this, PostUploadService.class);
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                postBinder = (PostUploadService.PostBinder) iBinder;
                postBinder.addListener(listener);
                Log.d("asd", "Post Binded");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d("asd", "service disconnected");
            }
        };

        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        if (postBinder != null)
            postBinder.addListener(listener);
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (postBinder != null)
        postBinder.removeListener(listener);
    }
}
