package com.barebrains.gyanith20.Adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barebrains.gyanith20.Models.eventitem;
import com.barebrains.gyanith20.R;

import java.util.ArrayList;

public class eventCategoriesAdapter extends ArrayAdapter{

    int res;
    ArrayList<eventitem> ei;
    Context c;
    int pos=0;

    public eventCategoriesAdapter(int res, ArrayList<eventitem> ei, Context c) {
        super(c, res, ei);
        this.res = res;
        this.ei = ei;
        this.c = c;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(c);
        View root = li.inflate(res, null, false);
        ((TextView) root.findViewById(R.id.eveitname)).setText(ei.get(position).getName());
        ((TextView) root.findViewById(R.id.eveittime)).setText(ei.get(position).getTime());
        if (pos==position) {


        root.setAlpha(0);


        Long delay = Long.valueOf(position * 150);
        ObjectAnimator a = ObjectAnimator.ofFloat(root, "alpha", 0, 1);
        a.setStartDelay(delay);
        a.start();
        ObjectAnimator o = ObjectAnimator.ofFloat(root, "translationX", 500, 0f);
        o.setStartDelay(delay);
        o.setInterpolator(new DecelerateInterpolator());
        o.setDuration(500);
        o.start();
       pos++;
    }

        ((TextView)root.findViewById(R.id.eveitname)).setText(ei.get(position).getName());
        ((TextView)root.findViewById(R.id.eveittime)).setText(ei.get(position).getTime());
        int id = c.getResources().getIdentifier("com.barebrains.gyanith19:drawable/" + ei.get(position).getTag().toLowerCase(), null, null);
        if(id!=0)
            ((ImageView)root.findViewById(R.id.logo)).setImageResource(id);
        return root;
    }
}
