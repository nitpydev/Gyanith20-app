package com.barebrains.gyanith20.adapters;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.ArrayResource;

import java.util.ArrayList;


public abstract class LiveListAdapter<T> extends ArrayAdapter {

    private int resId;
    private Loader loader;
    private LifecycleOwner lifecycleOwner;
    private int pos = 0;
    private LiveData<ArrayResource<T>> liveData;

    private Observer<ArrayResource<T>> observer = new Observer<ArrayResource<T>>() {
        @Override
        public void onChanged(ArrayResource<T> res) {
           if (res.handleLoader(loader))
               return;
           clear();
           addAll(res.value);
           notifyDataSetChanged();
        }
    };

    //PUBLIC FUNCTIONS

    @NonNull
    public abstract LiveData<ArrayResource<T>> getLiveData();

    @NonNull
    public abstract void bindView(View view,T data);

    @NonNull
    public abstract View createView();

    public void setLoader(@NonNull Loader loader){
        this.loader = loader;
    }
    @NonNull
    public Loader getLoader(){
        return loader;
    }

    @NonNull
    public int getResId(){
        return resId;
    }

    //UNDER THE HOOD

    public LiveListAdapter(@NonNull Context context, LifecycleOwner lifecycleOwner, int resource) {
        super(context,resource,new ArrayList<T>());
        this.lifecycleOwner = lifecycleOwner;
        resId = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = createView();
        }

        bindView(convertView,(T)getItem(position));
        animateItem(convertView,position);
        return convertView;
    }

    public void observe() {
        liveData = getLiveData();
        liveData.observe(lifecycleOwner,observer);
    }

    public void refresh(){
        if (liveData != null)
        liveData.removeObserver(observer);
        observe();
    }


    private void animateItem(View itemView,int position){
        if (pos==position) {

            itemView.setAlpha(0);


            Long delay = Long.valueOf(position * 150);
            ObjectAnimator a = ObjectAnimator.ofFloat(itemView, "alpha", 0, 1);
            a.setStartDelay(delay);
            a.start();
            ObjectAnimator o = ObjectAnimator.ofFloat(itemView, "scaleX", 0.5f, 1f);
            o.setStartDelay(delay);
            o.setInterpolator(new DecelerateInterpolator());
            o.setDuration(500);
            o.start();
            pos++;
        }

    }
}
