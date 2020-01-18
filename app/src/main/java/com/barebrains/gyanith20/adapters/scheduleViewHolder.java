package com.barebrains.gyanith20.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.activities.EventDetailsActivity;
import com.barebrains.gyanith20.fragments.ScheduleFragment;
import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.models.ScheduleItem;
import com.barebrains.gyanith20.statics.Util;

import java.util.Calendar;

public class scheduleViewHolder extends LiveViewHolder<ScheduleItem>{

    private TextView time;
    private TextView title;
    private TextView venue;
    private View live;
    private View btn;

    public scheduleViewHolder(@NonNull View itemView, Activity activity) {
        super(itemView, activity);
        time = itemView.findViewById(R.id.time);
        title = itemView.findViewById(R.id.title);
        venue = itemView.findViewById(R.id.venue);
        live = itemView.findViewById(R.id.liveindicator);
        btn = itemView.findViewById(R.id.btn);
    }

    @Override
    public void bindView(final ScheduleItem data) {
        time.setText(formatTime(data.start_time));
        title.setText(data.title);
        venue.setText(data.venue);

        if (data.isLive())
            live.setVisibility(View.VISIBLE);
        else
            live.setVisibility(View.INVISIBLE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.id != null){
                    Intent intent = new Intent(itemView.getContext(), EventDetailsActivity.class);
                    intent.putExtra("EXTRA_ID",data.id);
                    itemView.getContext().startActivity(intent);
                }
            }
        });
    }

    private String formatTime(Long time){
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(time);
        return cl.get(Calendar.HOUR) + ":"
                + cl.get(Calendar.MINUTE) + " "
                + Util.amPm(cl.get(Calendar.AM_PM));
    }
}