package com.barebrains.gyanith20.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.activities.EventDetailsActivity;
import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.models.EventItem;
import com.bumptech.glide.Glide;

public class eventCatAdapter extends LiveListAdapter<EventItem>{

    private Activity activity;
    public eventCatAdapter(@NonNull Activity context,@Nullable LifecycleOwner lifecycleOwner,@NonNull int resource) {
        super(context, lifecycleOwner, resource);
        activity = context;
    }

    @Nullable
    @Override
    public LiveData<ArrayResource<EventItem>> getLiveData(){ return  null;};


    @NonNull
    @Override
    public void bindView(View view, final EventItem data) {

        final TextView eventItemName = view.findViewById(R.id.eveitname);
        eventItemName.setText(data.name);
        ((TextView) view.findViewById(R.id.eveittime)).setText(data.timestamp);
        final View eve_cat_btn = view.findViewById(R.id.eve_cat_btn);
        String url = data.img1;
        final ImageView imageView = view.findViewById(R.id.logo);
        Glide.with(getContext())
                .load(url)
                .placeholder(R.drawable.abbg1)
                .error(R.drawable.abbg1)
                .into(imageView);

        eve_cat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.id != null){
                    Pair<View, String> eventName = Pair.<View, String>create(eventItemName,"eventName");
                    Pair<View, String> eventBar = Pair.<View, String>create(eve_cat_btn,"eventBar");
                    Pair<View, String> eventImg = Pair.<View, String>create(imageView,"eventImg");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        eventItemName.setTransitionName(eventName.second);
                        eve_cat_btn.setTransitionName(eventBar.second);
                        imageView.setTransitionName(eventImg.second);
                    }


                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,eventName,eventImg,eventBar);
                    Intent intent = new Intent(getContext(), EventDetailsActivity.class);
                    intent.putExtra("EXTRA_ID",data.id);
                    getContext().startActivity(intent,options.toBundle());
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