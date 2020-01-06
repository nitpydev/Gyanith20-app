package com.barebrains.gyanith20.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.barebrains.gyanith20.models.NotificationItem;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.statics.Util;

import java.util.ArrayList;

public class notificationAdapter extends ArrayAdapter<NotificationItem> {
    private int res;

    public notificationAdapter(Context c, ArrayList<NotificationItem> list, int res) {
        super(c,res, list);
        this.res = res;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root;

        if (convertView != null)
            root = convertView;
        else
            root = LayoutInflater.from(getContext()).inflate(res,parent,false);


        NotificationItem item = getItem(position);
        ((TextView) root.findViewById(R.id.notificationSender)).setText(item.title);
        ((TextView) root.findViewById(R.id.notificationTime)).setText(Util.BuildDateString(item.time));
        ((TextView) root.findViewById(R.id.notificationText)).setText(item.body);

        return root;
    }
}