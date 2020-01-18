package com.barebrains.gyanith20.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.models.NotificationItem;
import com.barebrains.gyanith20.statics.Util;

public class notiViewHolder extends LiveViewHolder<NotificationItem>{

    private TextView sender;
    private TextView time;
    private TextView text;

    public notiViewHolder(@NonNull View itemView, Activity activity) {
        super(itemView, activity);
        sender = itemView.findViewById(R.id.notificationSender);
        time = itemView.findViewById(R.id.notificationTime);
        text = itemView.findViewById(R.id.notificationText);
    }

    @Override
    public void bindView(NotificationItem data) {
        sender.setText(data.title);
        time.setText(Util.BuildScheduleDateString(data.time));
        text.setText(data.body);
    }
}
