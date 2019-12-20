package com.barebrains.gyanith20.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.adapters.eventCategoriesAdapter;
import com.barebrains.gyanith20.components.PostView;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.others.PostViewHolder;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.statics.Anim;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.PostManager;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ProfileActivity extends AppCompatActivity {


    View userInfoPanel;
    View profileCard;
    View qrCard;
    View topPanel;
    View backbtn;
    View profileBtn;
    View qrBack;
    View qrBtn;


    boolean userInfoBackReserved,qrBackReserved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        }
        setContentView(R.layout.activity_profile);
        ((ViewGroup) findViewById(R.id.profile_root)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);
        userInfoPanel = findViewById(R.id.userinfo_panel);
        userInfoPanel.setVisibility(View.GONE);
        profileCard = findViewById(R.id.profile_card);
        qrCard = findViewById(R.id.qr_card);
        topPanel = findViewById(R.id.top_panel_profile);
        backbtn = findViewById(R.id.profile_back_btn);
        qrBack = findViewById(R.id.qr_back_btn);
        qrBtn = findViewById(R.id.qrBtn);

        profileBtn = findViewById(R.id.profile_btn);
        userPanelTransition(false);
        qrPanelTransition(false);

        SetupUIwithData(GyanithUserManager.getCurrentUser());
        ViewPager viewPager = findViewById(R.id.profile_viewpager);
        SetupViewPager(viewPager);
        TabLayout tabLayout = findViewById(R.id.htab_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("WORKSHOPS");
        tabLayout.getTabAt(1).setText("Technical Events");
        tabLayout.getTabAt(2).setText("Community Posts");
    }

    private void SetupUIwithData(GyanithUser user){
        TextView username = findViewById(R.id.profile_username_txt);
        TextView name = findViewById(R.id.userinfo_name);
        TextView clg = findViewById(R.id.userinfo_clg);
        TextView email = findViewById(R.id.userinfo_email);
        TextView phoneno = findViewById(R.id.userinfo_phoneno);

        username.setText(user.userName);
        name.setText(user.name);
        clg.setText(user.clg);
        email.setText(user.email);
        phoneno.setText(user.phoneNo);
    }

    private void SetupViewPager(ViewPager viewPager){
        PagerAdapter adapter = new PagerAdapter() {

            private final PagedList.Config config = new PagedList.Config.Builder()
                    .setEnablePlaceholders(false)
                    .setPrefetchDistance(1)
                    .setPageSize(3)
                    .build();

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return (view == object);
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                int res;
                if(position == 0) {
                    res = R.id.reg_w;
                    setupRegisteredWorkshopsList();
                }
                else if (position == 1) {
                    res = R.id.reg_te;
                    setupRegisteredTechnicalEventsList();
                }
                else {
                    res = R.id.profile_posts;
                    setupUserPosts();
                }


                return findViewById(res);
            }

            private void setupRegisteredWorkshopsList(){
                ListView regList = findViewById(R.id.reg_w_list);
                regList.setAdapter(new eventCategoriesAdapter(R.layout.item_event_category
                        ,GyanithUserManager.getCurrentUser().reg_w
                        ,ProfileActivity.this));

            }

            private void setupRegisteredTechnicalEventsList(){
                ListView regList = findViewById(R.id.reg_te_list);
                regList.setAdapter(new eventCategoriesAdapter(R.layout.item_event_category
                        ,GyanithUserManager.getCurrentUser().reg_te
                        ,ProfileActivity.this));
            }

            private void setupUserPosts(){
                Query query = FirebaseDatabase.getInstance().getReference().child("users")
                        .child(GyanithUserManager.getCurrentUser().gyanithId)
                        .child("posts").orderByChild("time");
                RecyclerView feed = findViewById(R.id.profile_feed);
                final View loadFeed = findViewById(R.id.profile_feed_load);
                final SwipeRefreshLayout refreshFeed = findViewById(R.id.profile_feed_refresh);

                DatabasePagingOptions<Post> options = new DatabasePagingOptions.Builder<Post>()
                        .setLifecycleOwner(ProfileActivity.this)
                        .setQuery(query, config, Post.class)
                        .build();

                final FirebaseRecyclerPagingAdapter<Post, PostViewHolder> adapter = new FirebaseRecyclerPagingAdapter<Post, PostViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final PostViewHolder viewHolder, int position, @NonNull Post model) {
                        viewHolder.postView.SetPost(ProfileActivity.this,model);
                        PostManager.getInstance().AddLikeStateChangedLister(model.postId
                                ,viewHolder.postView.getLikeChangedListener());
                    }

                    @NonNull
                    @Override
                    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View item = new PostView(ProfileActivity.this);
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
                PostManager.getInstance().setRefreshs(adapter);

                feed.setAdapter(adapter);
                feed.setHasFixedSize(true);
                feed.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                refreshFeed.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        adapter.refresh();
                    }
                });
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
    }

    private void userPanelTransition(boolean open){

        if (open)
        {
            Anim.alpha(userInfoPanel,0,1,300,null);
            profileCard.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            topPanel.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userPanelTransition(false);
                }
            });
            profileBtn.setOnClickListener(null);
            userInfoBackReserved = true;
        }
        else
        {
            Anim.alpha(userInfoPanel, 1, 0, 300, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    userInfoPanel.setVisibility(View.GONE);
                }
            });
            profileCard.getLayoutParams().width = 0;
            topPanel.getLayoutParams().height = 0;
            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            profileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userPanelTransition(true);
                }
            });
            userInfoBackReserved = false;

        }
    }

    private void qrPanelTransition(boolean open){
        if (open){
            qrBack.setVisibility(View.VISIBLE);
            qrCard.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            topPanel.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            qrBtn.setOnClickListener(null);
            qrBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    qrPanelTransition(false);
                }
            });
        }else {
            qrBack.setVisibility(View.GONE);
            qrCard.getLayoutParams().width = 0;
            topPanel.getLayoutParams().height = 0;
            qrBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    qrPanelTransition(true);
                }
            });
            qrBack.setOnClickListener(null);
        }
    }

    @Override
    public void onBackPressed() {
        if (userInfoBackReserved) {
            userPanelTransition(false);
            userInfoBackReserved = false;
            return;
        }

        if (qrBackReserved){
            qrPanelTransition(false);
            qrBackReserved = false;
            return;
        }
        super.onBackPressed();
    }
}
