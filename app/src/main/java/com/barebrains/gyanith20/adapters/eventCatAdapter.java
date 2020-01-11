package com.barebrains.gyanith20.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.activities.EventDetailsActivity;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.EventItem;
import com.bumptech.glide.Glide;

public abstract class eventCatAdapter extends LiveListAdapter<EventItem>{

    private int pos;

    public eventCatAdapter(@NonNull Context context, LifecycleOwner lifecycleOwner, int resource) {
        super(context, lifecycleOwner, resource);
    }

    @NonNull
    @Override
    public abstract LiveData<Resource<EventItem>> getLiveData();


    @NonNull
    @Override
    public void bindView(View view, final EventItem data) {
        ((TextView) view.findViewById(R.id.eveitname)).setText(data.name);
        ((TextView) view.findViewById(R.id.eveittime)).setText(data.timestamp);

        String url = data.img2;
        ImageView imageView = view.findViewById(R.id.logo);
        Glide.with(getContext())
                .load(url)
                .placeholder(R.drawable.l2)
                .error(R.drawable.gyanith_error)
                .into(imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.id != null){
                    Intent intent = new Intent(getContext(), EventDetailsActivity.class);
                    intent.putExtra("EXTRA_ID",data.id);
                    getContext().startActivity(intent);
                }
            }
        });
    }

    @NonNull
    @Override
    public View createView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.item_event_category, null);
    }
}