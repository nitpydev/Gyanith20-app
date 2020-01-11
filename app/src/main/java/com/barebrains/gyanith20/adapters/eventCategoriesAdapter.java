package com.barebrains.gyanith20.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barebrains.gyanith20.activities.EventDetailsActivity;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.R;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

public class eventCategoriesAdapter extends ArrayAdapter{

    private int res;
    private Context c;
    private View emptyState;
    private View progress;
    private int pos=0;

    public eventCategoriesAdapter(int res,View emptyState,View progress, ArrayList<EventItem> ei, Context c) {
        super(c, res,ei);
        this.res = res;
        this.c = c;
        this.emptyState = emptyState;
        this.progress = progress;
        emptyState.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }


    @Override
    public void notifyDataSetChanged() {
        progress.setVisibility(View.GONE);
        if (getCount() == 0)
            emptyState.setVisibility(View.VISIBLE);
        else
            emptyState.setVisibility(View.GONE);

        super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemRoot = convertView;

        if (itemRoot == null)
            itemRoot = LayoutInflater.from(c).inflate(res,null,false);

        ((TextView) itemRoot.findViewById(R.id.eveitname)).setText(((EventItem)getItem(position)).name);
        ((TextView) itemRoot.findViewById(R.id.eveittime)).setText(((EventItem)getItem(position)).timestamp);
        if (pos==position) {

            itemRoot.setAlpha(0);


        Long delay = Long.valueOf(position * 150);
        ObjectAnimator a = ObjectAnimator.ofFloat(itemRoot, "alpha", 0, 1);
        a.setStartDelay(delay);
        a.start();
        ObjectAnimator o = ObjectAnimator.ofFloat(itemRoot, "scaleX", 0.5f, 1f);
        o.setStartDelay(delay);
        o.setInterpolator(new DecelerateInterpolator());
        o.setDuration(500);
        o.start();
        pos++;
        }

        String url = ((EventItem)getItem(position)).img2;
        ImageView imageView = itemRoot.findViewById(R.id.logo);
        Glide.with(c)
                .load(url)
                .placeholder(R.drawable.l2)
                .error(R.drawable.gyanith_error)
                .into(imageView);

        itemRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EventDetailsActivity.class);

                Gson gson = new Gson();
                intent.putExtra("eventItem", gson.toJson(((EventItem)getItem(position))));
                getContext().startActivity(intent);
            }
        });
        return itemRoot;
    }


}
