package com.barebrains.gyanith20.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.barebrains.gyanith20.Models.Post;
import com.barebrains.gyanith20.Others.PostViewHolder;
import com.barebrains.gyanith20.R;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class feedViewPagerAdapter extends PagerAdapter {
    Context context;
    LifecycleOwner lifecycleOwner;
    private PagedList.Config config;
    private Long postCount;

    private ProgressBar loadFeed;

    public feedViewPagerAdapter(Context context, LifecycleOwner lifecycleOwner,ProgressBar loadFeed){
        this.postCount = 0L;
        this.loadFeed = loadFeed;
        this.config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(1)
                .setPageSize(3)
                .build();
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
        ListenForPostCount();
    }
    @Override
    public int getCount() {
        return 2;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View Rootview = LayoutInflater.from(context).inflate(R.layout.item_feed,container,false);
        RecyclerView view = Rootview.findViewById(R.id.feed);
        SwipeRefreshLayout refreshLayout = Rootview.findViewById(R.id.refreshFeed);
        if (position == 0)
            SetUpHotFeed(view,refreshLayout);
        else
            SetUpTrendingFeed(view,refreshLayout);
        container.addView(Rootview);
        return Rootview;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout)object);
    }

    private void ListenForPostCount(){
        FirebaseDatabase.getInstance().getReference().child("postCount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postCount = dataSnapshot.getValue(Long.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SetUpHotFeed(RecyclerView feed, final SwipeRefreshLayout refreshFeed){
        Query query = FirebaseDatabase.getInstance().getReference().child("posts");

        DatabasePagingOptions<Post> options = new DatabasePagingOptions.Builder<Post>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(query, config, Post.class)
                .build();

        final FirebaseRecyclerPagingAdapter<Post, PostViewHolder> hotAdapter = new FirebaseRecyclerPagingAdapter<Post, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder viewHolder, int position, @NonNull Post model) {
                viewHolder.FillPost(context, model);
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View item = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_feedpost, parent, false);
                return new PostViewHolder(item);
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state){
                    case LOADING_INITIAL:
                        refreshFeed.setRefreshing(false);
                    case LOADING_MORE:
                        loadFeed.setVisibility(View.VISIBLE);
                    case LOADED:
                        loadFeed.setVisibility(View.GONE);
                }
            }
        };
        feed.setAdapter(hotAdapter);
        feed.setHasFixedSize(true);
        feed.setLayoutManager(new LinearLayoutManager(context));
        refreshFeed.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hotAdapter.refresh();
            }
        });
    }
    private void SetUpTrendingFeed(RecyclerView feed, final SwipeRefreshLayout refreshFeed)
    {
        Query query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("likes");

        DatabasePagingOptions<Post> options = new DatabasePagingOptions.Builder<Post>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(query, config, Post.class)
                .build();

        final FirebaseRecyclerPagingAdapter<Post, PostViewHolder> trendAdapter = new FirebaseRecyclerPagingAdapter<Post, PostViewHolder>(options) {
            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View item = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_feedpost, parent, false);
                return new PostViewHolder(item);
            }

            @Override
            public int getItemCount() {
                int value = super.getItemCount();
                return (value > postCount) ? postCount.intValue() : value;
            }

            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder viewHolder, int position, @NonNull Post model) {
                viewHolder.FillPost(context, model);
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state){
                    case LOADING_INITIAL:
                        refreshFeed.setRefreshing(false);
                    case LOADING_MORE:
                        loadFeed.setVisibility(View.VISIBLE);
                    case LOADED:
                        loadFeed.setVisibility(View.GONE);

                }
            }
        };
        feed.setAdapter(trendAdapter);
        feed.setHasFixedSize(true);
        feed.setLayoutManager(new LinearLayoutManager(context));

        refreshFeed.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                trendAdapter.refresh();
            }
        });

    }
}
