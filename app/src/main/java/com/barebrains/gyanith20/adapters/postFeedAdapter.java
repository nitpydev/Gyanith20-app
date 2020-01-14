package com.barebrains.gyanith20.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.others.PostViewHolder;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class postFeedAdapter extends FirebaseRecyclerPagingAdapter<Post, PostViewHolder> {


    private static final PagedList.Config config = new PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(1)
            .setPageSize(2)
            .build();
    private boolean flag = false;

    private final ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null)
                postCount = dataSnapshot.getValue(Long.class).intValue();
            else
                postCount = 0;

            if (!flag) {
                notifyDataSetChanged();
                flag = true;
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private DatabaseReference postCountRef;

    private Integer postCount = 0;
    private Loader loader;
    private SwipeRefreshLayout pullRefresh;
    private View bottomRefresh;

    //Loader should have its content added to it before calling this constructor
    public postFeedAdapter(@NonNull LifecycleOwner lifecycleOwner, Query query, DatabaseReference postCountRef, Loader loader, SwipeRefreshLayout pullRefresh, View bottomRefresh) {
        super(new DatabasePagingOptions.Builder<Post>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(query, config,Post.class)
                .build());
        query.keepSynced(true);
        this.loader = loader;
        this.pullRefresh = pullRefresh;
        this.bottomRefresh = bottomRefresh;
        loader.loading();
        this.postCountRef = postCountRef;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        postCountRef.addValueEventListener(valueEventListener);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        postCountRef.removeEventListener(valueEventListener);
    }

    @Override
    protected void onBindViewHolder(@NonNull final PostViewHolder viewHolder, int position, @NonNull Post model) {

        viewHolder.SetPost(model);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post,parent,false);
        return new PostViewHolder(item);
    }

    @Override
    public int getItemCount(){
        return (postCount < super.getItemCount())?postCount:super.getItemCount();
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        switch (state){
            case LOADING_INITIAL:
                bottomRefresh.setVisibility(View.GONE);
                pullRefresh.setRefreshing(false);
                loader.loading();
                break;
            case LOADING_MORE:
                bottomRefresh.setVisibility(View.VISIBLE);
                break;
            case LOADED:
                bottomRefresh.setVisibility(View.GONE);
                loader.loaded();
                pullRefresh.setRefreshing(false);
                break;
            case ERROR:
                bottomRefresh.setVisibility(View.GONE);
                pullRefresh.setRefreshing(false);
                if (getItemCount() == 0)
                    loader.error();
                break;
        }
    }

    @Override
    protected void onError(@NonNull DatabaseError databaseError) {
        super.onError(databaseError);
        if (getItemCount() == 0)
            loader.error();
    }
}
