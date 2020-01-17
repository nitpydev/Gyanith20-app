package com.barebrains.gyanith20.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.adapters.postFeedAdapter;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class PostsFeed extends SwipeRefreshLayout {
    public PostsFeed(@NonNull Context context) {
        super(context);
        init();
    }

    public PostsFeed(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private Loader loader;
    private RecyclerView recyclerView;
    private postFeedAdapter adapter;


    private void init(){
        loader = new Loader(getContext());
        recyclerView = new RecyclerView(getContext());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
        this.addView(loader);

        loader.set_loading_indicator_res_id(R.layout.loader_post_shimmer);

        loader.addView(recyclerView,layoutParams);
        layoutParams.setMargins(15,15,15,15);
        loader.set_empty_error("Tap post button and add a post that end up here");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
    }

    public void load(FragmentManager fragmentManager, LifecycleOwner lifecycleOwner, Query query, boolean timeOrdered){
        query.keepSynced(true);
        this.adapter = new postFeedAdapter(fragmentManager,lifecycleOwner,query,timeOrdered,loader,this);
        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    public void refresh(){
        if (adapter != null)
            adapter.refresh();
    }
}
