package com.barebrains.gyanith20.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.barebrains.gyanith20.activities.StartPostActivity;
import com.barebrains.gyanith20.activities.UploadPostActivity;
import com.barebrains.gyanith20.components.PostView;
import com.barebrains.gyanith20.interfaces.AuthStateListener;
import com.barebrains.gyanith20.interfaces.NetworkStateListener;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.others.PostViewHolder;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.barebrains.gyanith20.statics.PostManager;
import com.barebrains.gyanith20.statics.Util;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.polyak.iconswitch.IconSwitch;

import static android.app.Activity.RESULT_OK;

public class CommunityFragment extends Fragment {


    public ShimmerFrameLayout initLoader;

    public CommunityFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_community,container,false);

        initLoader = root.findViewById(R.id.empty_loader);
        PostManager.getInstance().setSnackbarParent(root); //Need to check here*/*/
        final View addPostBtn = root.findViewById(R.id.add_post_btn);

        NetworkManager.getInstance().addListener(78,new NetworkStateListener(){
            @Override
            public void OnDisconnected() {
                Toast.makeText(getContext(), "Couldn't Refresh feed", Toast.LENGTH_SHORT).show();
            }
        });
        GyanithUserManager.addAuthStateListner(1, new AuthStateListener() {
            @Override
            public void onChange() {
                addPostBtn.setOnClickListener(null);
                addPostBtn.setVisibility(View.GONE);
            }
            @Override
            public void VerifiedUser() {
                addPostBtn.setVisibility(View.VISIBLE);
                addPostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), StartPostActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        SetupViewPager(root);
        return root;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden)
            NetworkManager.getInstance().removeListener(78);
        else
            NetworkManager.getInstance().addListener(78,new NetworkStateListener(){
                @Override
                public void OnDisconnected() {
                    Toast.makeText(getContext(), "Couldn't Refresh feed", Toast.LENGTH_SHORT).show();
                }
            });
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroyView() {
        GyanithUserManager.removeAuthStateListener(1);
        super.onDestroyView();
    }


    private void SetupViewPager(View root){
        final ViewPager viewPager = root.findViewById(R.id.feedViewPager);

        final IconSwitch iconSwitch = (IconSwitch)root.findViewById(R.id.trendingSwitch);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    iconSwitch.setChecked(IconSwitch.Checked.LEFT);
                else
                    iconSwitch.setChecked(IconSwitch.Checked.RIGHT);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        iconSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {

                if (current == IconSwitch.Checked.RIGHT)
                    viewPager.setCurrentItem(1);
                else
                    viewPager.setCurrentItem(0);
            }
        });

        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(new FeedsPagerAdapter(this));
    }


}

class FeedsPagerAdapter extends PagerAdapter{
    private Activity activity;
    private LifecycleOwner lifecycleOwner;
    private ShimmerFrameLayout initLoader;
    private final PagedList.Config config = new PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(1)
            .setPageSize(3)
            .build();

    public FeedsPagerAdapter(CommunityFragment parent){
        this.activity = parent.getActivity();
        lifecycleOwner = parent.getViewLifecycleOwner();
        initLoader = parent.initLoader;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int resId = 0;
        switch (position) {
            case 0:
                resId = R.id.hot_feed_page;
                Query query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("time");
                SetUpFeed(R.id.hot_feed,R.id.hot_refreshFeed,query);
                break;
            case 1:
                resId = R.id.trend_feed_page;
                Query trendquery = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("likes");
                SetUpFeed(R.id.trend_feed,R.id.trend_refreshFeed,trendquery);
                break;
        }
        return activity.findViewById(resId);
    }



    private void SetUpFeed(int feedId, int refreshFeedId,Query query){
        final ProgressBar loadFeed = activity.findViewById(R.id.progressBar);
        RecyclerView feed = activity.findViewById(feedId);
        final SwipeRefreshLayout refreshFeed = activity.findViewById(refreshFeedId);

        DatabasePagingOptions<Post> options = new DatabasePagingOptions.Builder<Post>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(query, config, Post.class)
                .build();

        final FirebaseRecyclerPagingAdapter<Post, PostViewHolder> adapter = new FirebaseRecyclerPagingAdapter<Post, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PostViewHolder viewHolder, int position, @NonNull Post model) {
                viewHolder.postView.SetPost(activity,model);
                PostManager.getInstance().AddLikeStateChangedLister(model.postId
                        ,viewHolder.postView.getLikeChangedListener());
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View item = new PostView(activity);
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
                            Toast.makeText(activity, "Couldn't Refresh Feed!", Toast.LENGTH_SHORT).show();
                        break;
                    case LOADING_MORE:
                        loadFeed.setVisibility(View.VISIBLE);
                        break;
                    case LOADED:
                        loadFeed.setVisibility(View.GONE);
                        initLoader.setVisibility(View.GONE);
                        break;
                }
            }
        };
        PostManager.getInstance().setRefreshs(adapter);

        feed.setAdapter(adapter);
        feed.setHasFixedSize(true);
        feed.setLayoutManager(new LinearLayoutManager(activity));
        refreshFeed.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
            }
        });
    }
    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}

