package com.barebrains.gyanith20.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.others.LoaderException;
import com.barebrains.gyanith20.others.PostViewHolder;
import com.firebase.ui.database.SnapshotParser;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class postFeedAdapter extends postsAdapter {

    private Loader loader;
    private SwipeRefreshLayout pullRefresh;

    //Loader should have its content added to it before calling this constructor
    public postFeedAdapter(FragmentManager fragmentManager, @NonNull LifecycleOwner lifecycleOwner, Query query, boolean timeOrdered, Loader loader, SwipeRefreshLayout pullRefresh) {
        super(fragmentManager,lifecycleOwner,query,timeOrdered);
        this.loader = loader;
        this.pullRefresh = pullRefresh;
        loader.setNeverLoadingFlag(true);
    }


    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        switch (state){
            case LOADING_INITIAL:
                loader.loading();
                break;
            case LOADED:
                loader.loaded();
                pullRefresh.setRefreshing(false);
                break;
            case ERROR:
                pullRefresh.setRefreshing(false);
                break;
        }
    }

    @Override
    protected void onError(@NonNull LoaderException e) {
            //TODO:HANDLE EXCEPTIONS
    }
}
