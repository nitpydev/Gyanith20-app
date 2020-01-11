package com.barebrains.gyanith20.components;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.ScheduleItem;

import java.lang.reflect.Array;

public class LiveListView extends FrameLayout {

    //Default Constructors

    public LiveListView(@NonNull Context context) {
        super(context);
        init();
    }

    public LiveListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LiveListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    //INITIATIONS
    private ListView mlist;
    private View progress;
    private TextView emptyStateText;

    private LiveListAdapter mAdapter;
    private LifecycleOwner lifecycleOwner;

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_livelist,this,true);

        mlist = findViewById(R.id.list);
        progress = findViewById(R.id.prog);
        emptyStateText = findViewById(R.id.empty_state);
        isloading(true,false);
    }


    //PUBLIC FUNCTIONS
    public void setAdapter(LifecycleOwner lifecycleOwner, LiveListAdapter adapter)
    {
        if (mAdapter != null)
            return;
        if (lifecycleOwner != null)
            this.lifecycleOwner = lifecycleOwner;
        mAdapter = adapter;
        syncAdapter();
    }

     //HELPERS
    private void syncAdapter(){
        if (mAdapter == null)
            return;

        isloading(true,false);
        emptyStateText.setText(mAdapter.getEmptyState());
        mlist.setAdapter(mAdapter);
        mAdapter. getLiveData().observe(lifecycleOwner, new Observer<Resource>() {
            @Override
            public void onChanged(Resource res) {
                isloading(false,res.error == null && res.value.length != 0);
                if (res.error != null)
                    return;



                mAdapter.clear();
                mAdapter.addAll(res.value);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    private void isloading(boolean state,boolean content){
        progress.setVisibility(GONE);
        mlist.setVisibility(GONE);
        emptyStateText.setVisibility(GONE);
        if (state)
            progress.setVisibility(VISIBLE);
        else {
            if (content)
                mlist.setVisibility(VISIBLE);
            else
                emptyStateText.setVisibility(VISIBLE);
        }
    }
}
