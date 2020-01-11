package com.barebrains.gyanith20.others;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.fragment.app.Fragment;

import com.barebrains.gyanith20.activities.MainActivity;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.services.PostUploadService;

public abstract class mFragment extends Fragment{

    private boolean isShown = false;


    @Override
    public void onStart() {
        super.onStart();
        onHiddenChanged(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden)
            onHide();
        else
            onShow();
        updateMark();
        updateBinding();
    }

    @CallSuper
    public void onShow(){
        isShown = true;
    }

    @CallSuper
    public void onHide(){
        isShown = false;
    }




    //MARKING BADGES FUNCTIONALITY
    private Integer index = null;

    public void markBadges(Integer index){
        this.index = index;
        updateMark();
    }

    private void updateMark(){
        if (index != null && MainActivity.botNav != null)
        {
            MainActivity.botNav.updateMarkState(index,isShown);
        }
    }


    //BINDING TO POST_UPLOAD_SERVICE FUNCTIONALITY

    private CompletionListener listener;
    private Intent bindingIntent;
    private ServiceConnection serviceConnection;
    private PostUploadService.PostBinder postBinder;
    private boolean isListenerAdded = false;

    //Should be with onCreate()
    protected void syncWithPostService(CompletionListener listener){
        this.listener = listener;
        bindingIntent = new Intent(getContext(), PostUploadService.class);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                postBinder = (PostUploadService.PostBinder)iBinder;
                Log.d("asd","Post Binded");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d("asd","service disconnected");
            }
        };

        if (getContext() != null)
            getContext().bindService(bindingIntent,serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void updateBinding(){
        if (postBinder == null || isListenerAdded == isShown)
            return;

        if (isShown)
            postBinder.addListener(listener);
        else
            postBinder.removeListener(listener);

        isListenerAdded = isShown;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getContext() != null && serviceConnection != null)
            getContext().unbindService(serviceConnection);
    }
}
