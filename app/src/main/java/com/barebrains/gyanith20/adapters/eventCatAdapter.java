package com.barebrains.gyanith20.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
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
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.models.EventItem;
import com.bumptech.glide.Glide;

public class eventCatAdapter extends LiveListAdapter<EventItem,eventCatViewHolder>{

    private Activity activity;

    public eventCatAdapter(Activity activity, LifecycleOwner lifecycleOwner, Loader loader) {
        super(activity, lifecycleOwner,  R.layout.item_event_category, loader);
        this.activity = activity;
    }

    public eventCatAdapter(Activity activity, @Nullable LifecycleOwner lifecycleOwner) {
        super(activity,lifecycleOwner, R.layout.item_event_category);
        this.activity = activity;
    }

    @Nullable
    @Override
    public LiveData<ArrayResource<EventItem>> getLiveData(){ return  null;}

    @NonNull
    @Override
    public eventCatViewHolder createViewHolder(View ItemView) {
        return new eventCatViewHolder(ItemView,activity);
    }
}

class eventCatViewHolder extends LiveViewHolder<EventItem>{

    private TextView eventItemName;
    private TextView eventItemTime;
    private View eventItemBtn;
    private ImageView eventItemImg;

    public eventCatViewHolder(@NonNull View itemView,Activity activity) {
        super(itemView,activity);
        eventItemName = itemView.findViewById(R.id.eveitname);
        eventItemTime = itemView.findViewById(R.id.eveittime);
        eventItemBtn = itemView.findViewById(R.id.eve_cat_btn);
        eventItemImg = itemView.findViewById(R.id.logo);
    }

    @Override
    public void bindView(final EventItem data) {
        //Fill up data
        eventItemName.setText(data.name);
        eventItemTime.setText(data.timestamp);

        Glide.with(itemView.getContext())
                .load(data.img1)
                .placeholder(R.drawable.abbg1)
                .error(R.drawable.abbg1)
                .into(eventItemImg);

        eventItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.id != null){
                    Pair<View, String> eventName = Pair.<View, String>create(eventItemName,"eventName");
                    Pair<View, String> eventBar = Pair.<View, String>create(eventItemBtn,"eventBar");
                    Pair<View, String> eventImg = Pair.<View, String>create(eventItemImg,"eventImg");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        eventItemName.setTransitionName(eventName.second);
                        eventItemBtn.setTransitionName(eventBar.second);
                        eventItemImg.setTransitionName(eventImg.second);
                    }


                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,eventName,eventImg,eventBar);
                    Intent intent = new Intent(itemView.getContext(), EventDetailsActivity.class);
                    intent.putExtra("EXTRA_ID",data.id);
                    itemView.getContext().startActivity(intent,options.toBundle());
                }
            }
        });
    }
}