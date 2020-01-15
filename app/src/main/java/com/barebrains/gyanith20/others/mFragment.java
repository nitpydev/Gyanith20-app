package com.barebrains.gyanith20.others;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.barebrains.gyanith20.activities.MainActivity;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.services.PostUploadService;

public abstract class mFragment extends Fragment{

    private boolean isShown = false;


    @CallSuper
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        onHiddenChanged(false);
        return super.onCreateView(inflater, container, savedInstanceState);
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

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onHiddenChanged(true);
    }

    //MARKING BADGES FUNCTIONALITY
    private Integer index = null;

    protected void markBadges(Integer index){
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
    private ServiceConnection serviceConnection;
    private PostUploadService.PostBinder postBinder;
    private boolean isListenerAdded = false;

    //Should be with onCreate()
    protected void syncWithPostService(final CompletionListener listener){
        this.listener = listener;
        Intent bindingIntent = new Intent(getContext(), PostUploadService.class);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                postBinder = (PostUploadService.PostBinder)iBinder;
                postBinder.addListener(listener);
                isListenerAdded = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

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
        onHiddenChanged(true);
        if (getContext() != null && serviceConnection != null)
            getContext().unbindService(serviceConnection);
    }
}

