package com.barebrains.gyanith20.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.activities.StartPostActivity;
import com.barebrains.gyanith20.components.PostView;
import com.barebrains.gyanith20.interfaces.AuthStateListener;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.NetworkStateListener;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.others.PostViewHolder;
import com.barebrains.gyanith20.others.mFragment;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.barebrains.gyanith20.statics.PostManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.polyak.iconswitch.IconSwitch;

public class CommunityFragment extends mFragment {


    ShimmerFrameLayout initLoader;
    private View root;
    private FeedsPagerAdapter adapter;

    public CommunityFragment() {
        markBadges(4);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncWithPostService(new CompletionListener(){
            @Override
            public void OnComplete() {
                if (root != null)
                Snackbar.make(root,"Posted Successfully !", BaseTransientBottomBar.LENGTH_LONG)
                .setAction("REFRESH FEED", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      if (adapter!= null)
                          adapter.refresh();
                    }
                }).show();
            }

            @Override
            public void OnError(String error) {
                Log.d("asd","Error : " + error);
                if (root != null)
                    Snackbar.make(root,error,BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_community,container,false);

        initLoader = root.findViewById(R.id.empty_loader);
        final View addPostBtn = root.findViewById(R.id.add_post_btn);

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
    public void onShow() {
        super.onShow();
        NetworkManager.getInstance().addListener(78,new NetworkStateListener(){
            @Override
            public void OnDisconnected() {
                Toast.makeText(getContext(), "Couldn't Refresh feed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onHide() {
        super.onHide();
        NetworkManager.getInstance().removeListener(78);
    }

    @Override
    public void onDestroyView() {
        GyanithUserManager.removeAuthStateListener(1);
        super.onDestroyView();
    }


    private void SetupViewPager(View root){
        final ViewPager viewPager = root.findViewById(R.id.feedViewPager);

        final IconSwitch iconSwitch = root.findViewById(R.id.trendingSwitch);

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
        viewPager.setAdapter((adapter = new FeedsPagerAdapter(this)));
    }

    //SINGLETON
    private static CommunityFragment instance;

    public static CommunityFragment getInstance(){
        if (instance == null)
            instance = new CommunityFragment();
        return instance;
    }
}

class FeedsPagerAdapter extends PagerAdapter{
    private Activity activity;
    private LifecycleOwner lifecycleOwner;
    private ShimmerFrameLayout initLoader;
    private final PagedList.Config config = new PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(1)
            .setPageSize(2)
            .build();


    private FirebaseRecyclerPagingAdapter<Post, PostViewHolder> hotAdapter;
    private FirebaseRecyclerPagingAdapter<Post, PostViewHolder> trendAdapter;

    public void refresh(){
        if (hotAdapter != null && trendAdapter != null) {
            hotAdapter.refresh();
            trendAdapter.refresh();
        }
    }



    FeedsPagerAdapter(CommunityFragment parent){
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
                hotAdapter = SetUpFeed(R.id.hot_feed,R.id.hot_refreshFeed,R.id.h_es,query);
                break;
            case 1:
                resId = R.id.trend_feed_page;
                Query trendquery = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("likes");
                trendAdapter = SetUpFeed(R.id.trend_feed,R.id.trend_refreshFeed,R.id.t_es,trendquery);
                break;
        }
        return activity.findViewById(resId);
    }



    private FirebaseRecyclerPagingAdapter<Post, PostViewHolder> SetUpFeed(int feedId, int refreshFeedId,int es_id,Query query){
        final ProgressBar loadFeed = activity.findViewById(R.id.progressBar);
        RecyclerView feed = activity.findViewById(feedId);
        final View es = activity.findViewById(es_id);
        final SwipeRefreshLayout refreshFeed = activity.findViewById(refreshFeedId);

        query.keepSynced(true);

        DatabasePagingOptions<Post> options = new DatabasePagingOptions.Builder<Post>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(query, config, Post.class)
                .build();


        final FirebaseRecyclerPagingAdapter<Post, PostViewHolder> adapter = new FirebaseRecyclerPagingAdapter<Post, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PostViewHolder viewHolder, int position, @NonNull Post model) {
                viewHolder.postView.SetPost(activity,model);
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                PostView item = new PostView(activity);
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
                        es.setVisibility(View.GONE);
                        initLoader.setVisibility(View.VISIBLE);
                        break;
                    case LOADING_MORE:
                        loadFeed.setVisibility(View.VISIBLE);
                        es.setVisibility(View.GONE);
                        initLoader.setVisibility(View.GONE);
                        break;
                    case LOADED:
                        refreshFeed.setRefreshing(false);
                        loadFeed.setVisibility(View.GONE);
                        es.setVisibility(View.GONE);
                        initLoader.setVisibility(View.GONE);
                        break;
                    case ERROR:
                        loadFeed.setVisibility(View.GONE);
                        refreshFeed.setRefreshing(false);
                        es.setVisibility(View.VISIBLE);
                        break;
                    case FINISHED:
                        loadFeed.setVisibility(View.GONE);
                        es.setVisibility(View.GONE);
                        refreshFeed.setRefreshing(false);
                        initLoader.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            protected void onError(@NonNull DatabaseError databaseError) {
                super.onError(databaseError);
                initLoader.setVisibility(View.GONE);
                Log.d("asd","feed : " + databaseError.getMessage());
                retry();
            }
        };

        feed.setAdapter(adapter);
        feed.setHasFixedSize(true);
        feed.setLayoutManager(new LinearLayoutManager(activity){
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {

                }
            }
        });
        refreshFeed.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
            }
        });
        return adapter;
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

