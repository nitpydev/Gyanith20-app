package com.barebrains.gyanith20.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;

import androidx.lifecycle.Lifecycle;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.statics.AppNotiManager;
import com.barebrains.gyanith20.statics.PostManager;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class BotNavView extends BottomNavigationView {

    public BotNavView(Context context) {
        super(context);
        init(context);
    }

    public BotNavView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BotNavView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }




    private SharedPreferences sp;

    private Map<Integer,Boolean> markStates = new HashMap<>();
    private Map<Integer,Integer> counts = new HashMap<>();
    private Map<Integer,Integer> readCounts = new HashMap<>();

    private void init(Context context){
        sp = context.getSharedPreferences(context.getString(R.string.package_name),Context.MODE_PRIVATE);
        if (AppNotiManager.notiItems != null)
            updateCount(3, AppNotiManager.notiItems.length);
        updateCount(4, PostManager.postCount);
    }

    public void updateCount(int index,Integer count){
        Integer id = getMenu().getItem(index).getItemId();
        if (!readCounts.containsKey(id))
            readCache(id);
        counts.put(id,count);
        refreshBadge(id);
    }

    public void updateMarkState(Integer index,boolean state){
        Integer id = getMenu().getItem(index).getItemId();
        if (markStates.containsKey(id) && markStates.get(id) == state)
            return;

        markStates.put(id,state);
        refreshBadge(id);
    }


    private void markRead(Integer id){
        if (!counts.containsKey(id) || !readCounts.containsKey(id))
            return;

        if (readCounts.get(id) > counts.get(id))
            return;
        readCounts.put(id,counts.get(id));
        writeCache(id);
    }



    private void refreshBadge(Integer id) throws NullPointerException{
        if (!counts.containsKey(id) || !readCounts.containsKey(id))
            return;

        if (markStates.containsKey(id) && markStates.get(id)) {
            markRead(id);
        }


        BadgeDrawable badge = getOrCreateBadge(id);

        badge.setVisible(counts.get(id) > readCounts.get(id));
    }

    private void readCache(Integer id){
        readCounts.put(id,sp.getInt(id.toString(), 0));
    }

    private void writeCache(Integer id){
        if (!readCounts.containsKey(id))
            return;
        sp.edit().putInt(id.toString(),readCounts.get(id)).apply();
    }
}
