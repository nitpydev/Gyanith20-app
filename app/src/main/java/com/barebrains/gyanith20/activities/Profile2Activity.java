package com.barebrains.gyanith20.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.adapters.eventCatAdapter;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.components.PostsFeed;
import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.others.MoveUpwardBehavior;
import com.barebrains.gyanith20.others.mActivity;
import com.barebrains.gyanith20.statics.EventsModel;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.google.android.material.circularreveal.coordinatorlayout.CircularRevealCoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class Profile2Activity extends mActivity {

    Loader loader;
    TabLayout tabLayout;
    ViewPager viewPager;
    EventsModel eventsModel;

    View root;
    private CompletionListener postUploadedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        syncWithPostService(new CompletionListener(){
            @Override
            public void OnComplete() {
                if (root != null)
                    Snackbar.make(root,"Posted Successfully !", BaseTransientBottomBar.LENGTH_LONG)
                            .setAction("REFRESH FEED", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (postUploadedListener != null)
                                        postUploadedListener.OnComplete();
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

        //VIEW BINDINGS
        tabLayout = findViewById(R.id.profile2_tabs);
        viewPager = findViewById(R.id.profile2_viewpager);
        findViewById(R.id.profile2_backbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        eventsModel = ViewModelProviders.of(this).get(EventsModel.class);
        loader = findViewById(R.id.profile2_loader);

        GyanithUserManager.getCurrentUser().observe(this, new Observer<Resource<GyanithUser>>() {
            @Override
            public void onChanged(Resource<GyanithUser> res) {
                if (res.value == null)
                {
                    Intent intent = new Intent(Profile2Activity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    return;
                }
                loader.loaded();
                setUpViewPagerAdapter(res.value);
            }
        });






    }

    private void setUpViewPagerAdapter(final GyanithUser user){
        viewPager.setAdapter(new PagerAdapter() {
            String[] emptyStates = new String[]{"Your Registered Workshops show up here"
                    ,"Your Registered Technical Events show up here"};

            String[] pageTitles = new String[]{"WORKSHOPS","EVENTS","COMMUNITY"};

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, final int position) {

                if (position != 2){
                    Loader loader = new Loader(Profile2Activity.this);
                    loader.set_empty_error(emptyStates[position]);

                    ListView listView = new ListView(Profile2Activity.this);
                    eventCatAdapter eventCatAdapter = new eventCatAdapter(Profile2Activity.this, Profile2Activity.this, R.layout.item_event_category) {

                        @Nullable
                        @Override
                        public LiveData<ArrayResource<EventItem>> getLiveData() {
                            return eventsModel.getEventsofIdsandType((position == 0) ? "w" : "te", user.regEventIds);
                        }
                    };
                    eventCatAdapter.setLoader(loader);
                    listView.setAdapter(eventCatAdapter);
                    loader.addView(listView );
                    eventCatAdapter.observe();
                    container.addView(loader);
                    return loader;
                }
                else {
                    CircularRevealCoordinatorLayout coordinatorLayout = new CircularRevealCoordinatorLayout(Profile2Activity.this);

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                            .child(user.gyanithId);
                    Query query = userRef.child("posts").orderByChild("time");

                    final PostsFeed postsFeed = new PostsFeed(Profile2Activity.this);
                    postsFeed.load(getSupportFragmentManager(),Profile2Activity.this,query,true);

                    coordinatorLayout.addView(postsFeed);

                    final FloatingActionButton addPostBtn = new FloatingActionButton(new ContextThemeWrapper(Profile2Activity.this,R.style.fab));
                    CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
                    layoutParams.gravity = Gravity.BOTTOM|Gravity.END;
                    layoutParams.setBehavior(new  MoveUpwardBehavior());
                    layoutParams.rightMargin = layoutParams.bottomMargin = 50;
                    addPostBtn.setLayoutParams(layoutParams);
                    addPostBtn.setBackgroundTintList(ContextCompat.getColorStateList(Profile2Activity.this,R.color.colorAccent));
                    addPostBtn.setBackgroundColor(ContextCompat.getColor(Profile2Activity.this,R.color.colorAccent));

                   GyanithUserManager.getCurrentUser().observe(Profile2Activity.this,new Observer<Resource<GyanithUser>>() {
                       @Override
                       public void onChanged(Resource<GyanithUser> res) {

                           if (res.value != null){
                               addPostBtn.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       Intent intent = new Intent(Profile2Activity.this, StartPostActivity.class);
                                       startActivity(intent);
                                   }
                               });
                           }
                           else
                           {
                               addPostBtn.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       Toast.makeText(Profile2Activity.this, "Sign in to Post", Toast.LENGTH_SHORT).show();
                                   }
                               });
                           }

                       }});

                    coordinatorLayout.addView(addPostBtn,layoutParams);
                    container.addView(coordinatorLayout);
                    root = coordinatorLayout;
                    postUploadedListener = new CompletionListener(){
                        @Override
                        public void OnComplete() {
                            postsFeed.refresh();
                        }
                    };
                    return coordinatorLayout;
                }


            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                //No Need to destroy
            }

            @Override
            public int getCount() {
                return 3;
            }


            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return pageTitles[position];
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }
        });


        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
    }
}

