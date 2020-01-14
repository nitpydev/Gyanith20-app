package com.barebrains.gyanith20.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.components.PostView;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.others.PostViewHolder;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.barebrains.gyanith20.statics.PostManager;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

public class postsAdapter extends FirebaseRecyclerPagingAdapter<Post, PostViewHolder> {

    private static final PagedList.Config config = new PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(1)
            .setPageSize(2)
            .build();

    ProgressBar loadFeed;
    SwipeRefreshLayout refreshFeed;
    View initLoader;
    Context context;

    public postsAdapter(Activity activity, LifecycleOwner lifecycleOwner, Query query, int loadFeedResId,int swipeRefreshResId,int initLoaderResId) {
        super(new DatabasePagingOptions.Builder<Post>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(query, config, Post.class)
                .build());

        loadFeed = activity.findViewById(loadFeedResId);
        refreshFeed = activity.findViewById(swipeRefreshResId);
        initLoader = activity.findViewById(initLoaderResId);
        query.keepSynced(true);
        context = activity;

    }

    @Override
    protected void onBindViewHolder(@NonNull final PostViewHolder viewHolder, int position, @NonNull Post model) {
    viewHolder.SetPost(model);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PostView item = new PostView(context);
        return new PostViewHolder(item);
    }

    @Override
    public int getItemCount() {
        return (PostManager.postCount < super.getItemCount())?PostManager.postCount:super.getItemCount();
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        switch (state){
            case LOADING_INITIAL:
                refreshFeed.setRefreshing(false);
                if (!NetworkManager.getInstance().isNetAvailable())
                    Toast.makeText(context, "Couldn't Refresh Feed!", Toast.LENGTH_SHORT).show();
                initLoader.setVisibility(View.VISIBLE);
                break;
            case LOADING_MORE:
                loadFeed.setVisibility(View.VISIBLE);
                initLoader.setVisibility(View.GONE);
                break;
            case LOADED:
                refreshFeed.setRefreshing(false);
                loadFeed.setVisibility(View.GONE);
                initLoader.setVisibility(View.GONE);
                break;
            case ERROR:
                loadFeed.setVisibility(View.GONE);
                refreshFeed.setRefreshing(false);

                break;
            case FINISHED:
                loadFeed.setVisibility(View.GONE);

                refreshFeed.setRefreshing(false);
                initLoader.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onError(@NonNull DatabaseError databaseError) {
        super.onError(databaseError);
        retry();
    }

}
