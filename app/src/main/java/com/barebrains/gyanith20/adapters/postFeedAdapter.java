package com.barebrains.gyanith20.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.others.Response;
import com.firebase.ui.database.paging.LoadingState;
import com.google.firebase.database.Query;

public class postFeedAdapter extends postsAdapter {

    private Loader loader;
    private SwipeRefreshLayout pullRefresh;

    //Loader should have its content added to it before calling this constructor
    public postFeedAdapter(FragmentManager fragmentManager, @NonNull LifecycleOwner lifecycleOwner, Query query, boolean timeOrdered, Loader loader, SwipeRefreshLayout pullRefresh) {
        super(fragmentManager,lifecycleOwner,query,timeOrdered);
        this.loader = loader;
        this.pullRefresh = pullRefresh;
        //loader.setNeverLoadingFlag(true);
    }


    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        switch (state){
            case LOADING_INITIAL:
                loader.loading();
                break;
            case LOADED:
                Log.d("asd","putting Loaded");
                loader.loaded();
                pullRefresh.setRefreshing(false);
                break;
            case ERROR:
                pullRefresh.setRefreshing(false);
                break;
        }
    }

    @Override
    protected void onError(@NonNull Response e) {
        Log.d("asd","e : " + e.getCode());
        e.handleWithLoader(loader);
    }
}
