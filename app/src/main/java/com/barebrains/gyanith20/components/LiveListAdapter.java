package com.barebrains.gyanith20.components;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.barebrains.gyanith20.interfaces.Resource;

import java.util.ArrayList;


public abstract class LiveListAdapter<T> extends ArrayAdapter {


    //PUBLIC FUNCTIONS

    public abstract String getEmptyState();

    public abstract LiveData<Resource<T>> getLiveData();

    public abstract void bindView(View view,T data);

    public abstract View createView();


    //UNDER THE HOOD

    public LiveListAdapter(@NonNull Context context, int resource) {
        super(context,resource,new ArrayList<T>());
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView =createView();

        bindView(convertView,(T)getItem(position));

        return convertView;
    }
}
