package com.barebrains.gyanith20.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.ArrayResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public abstract class LiveListAdapter<T,VH extends LiveViewHolder<T>> extends RecyclerView.Adapter<VH> {

    private List<T> data = new ArrayList<>();

    private int resId;
    private Loader loader;
    private LifecycleOwner lifecycleOwner;
    private int pos = 0;
    private LiveData<ArrayResource<T>> liveData;

    private boolean readyMade = false;

    private Observer<ArrayResource<T>> observer = new Observer<ArrayResource<T>>() {
        @Override
        public void onChanged(ArrayResource<T> res) {
           if (res.handleWithLoader(loader))
               return;
           data = Arrays.asList(res.value);
           notifyDataSetChanged();
        }
    };


    public LiveListAdapter(Context context,LifecycleOwner lifecycleOwner, int resource) {
        this.lifecycleOwner = lifecycleOwner;
        this.resId = resource;
        loader = new Loader(context);
        readyMade = false;
    }

    public LiveListAdapter(Context context,LifecycleOwner lifecycleOwner,int resource,Loader loader){
        this.lifecycleOwner = lifecycleOwner;
        this.resId = resource;
        this.loader = loader;
        readyMade = true;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        if (!readyMade){
            loader.addView(recyclerView);
            loader.loading();
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext(),RecyclerView.VERTICAL,false){
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        };
        recyclerView.setLayoutManager(linearLayoutManager);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.layout_anim);
        recyclerView.setLayoutAnimation(animation);
        observe();
        super.onAttachedToRecyclerView(recyclerView);

    }

    @NonNull
    public abstract LiveData<ArrayResource<T>> getLiveData();

    @NonNull
    public abstract VH createViewHolder(View ItemView);

    @NonNull
    public Loader getLoader(){
        return loader;
    }



    private void observe() {
        liveData = getLiveData();
        liveData.observe(lifecycleOwner,observer);
    }

    public void refresh(){
        if (liveData != null)
        liveData.removeObserver(observer);
        observe();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(resId,parent,false);
        return createViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position){
        holder.bindView(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

abstract class LiveViewHolder<T> extends RecyclerView.ViewHolder{

    Activity activity;

    public LiveViewHolder(@NonNull View itemView, Activity activity) {
        super(itemView);
        this.activity = activity;
    }

    public abstract void bindView(T data);
}
