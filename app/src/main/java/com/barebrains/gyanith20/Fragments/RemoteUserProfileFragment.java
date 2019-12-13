package com.barebrains.gyanith20.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.barebrains.gyanith20.Models.Post;
import com.barebrains.gyanith20.Others.PostViewHolder;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.Statics.GyanithUserManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RemoteUserProfileFragment extends Fragment {

    private  String userId;
    public RemoteUserProfileFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_remote_user_profile,container,false);

        RecyclerView feed = root.findViewById(R.id.remote_feed);
        SwipeRefreshLayout refreshLayout = root.findViewById(R.id.refreshFeed);
        View loadFeed = root.findViewById(R.id.load_user_posts);
        SetUpUserFeed(feed,refreshLayout,loadFeed);

        return root;
    }

    private void SetUpUserFeed(final RecyclerView feed, final SwipeRefreshLayout refreshLayout, final View loadFeed){
        Query query = FirebaseDatabase.getInstance().getReference().child("users").child(GyanithUserManager.getCurrentUser().gyanithId).child("posts");
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(1)
                .setPageSize(3)
                .build();
        DatabasePagingOptions<Post> options = new DatabasePagingOptions.Builder<Post>()
                .setLifecycleOwner(getViewLifecycleOwner())
                .setQuery(query, config, Post.class)
                .build();

        final FirebaseRecyclerPagingAdapter<Post, PostViewHolder> adapter = new FirebaseRecyclerPagingAdapter<Post, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder viewHolder, int position, @NonNull Post model) {
                viewHolder.FillSimplePost(getContext(), model);
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
                        refreshLayout.setRefreshing(false);
                    case LOADING_MORE:
                        loadFeed.setVisibility(View.VISIBLE);
                    case LOADED:
                        loadFeed.setVisibility(View.GONE);
                }
            }
        };

        feed.setAdapter(adapter);
        feed.setHasFixedSize(true);
        feed.setLayoutManager(new GridLayoutManager(getContext(),3));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
            }
        });
    }
}




