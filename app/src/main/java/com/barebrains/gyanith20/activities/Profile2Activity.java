package com.barebrains.gyanith20.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.adapters.eventCatAdapter;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.components.PostsFeed;
import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.EventsModel;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Profile2Activity extends AppCompatActivity {

    GyanithUser user;
    TabLayout tabLayout;
    ViewPager viewPager;
    EventsModel eventsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = GyanithUserManager.getCurrentUser();
        if (user == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_profile2);

        tabLayout = findViewById(R.id.profile2_tabs);
        viewPager = findViewById(R.id.profile2_viewpager);
        eventsModel = ViewModelProviders.of(this).get(EventsModel.class);
        viewPager.setAdapter(new pager());
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

        findViewById(R.id.profile2_backbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class pager extends PagerAdapter{

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
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                        .child(GyanithUserManager.getCurrentUser().gyanithId);
                Query query = userRef.child("posts").orderByChild("time");

                PostsFeed postsFeed = new PostsFeed(Profile2Activity.this);
                postsFeed.load(Profile2Activity.this,query,userRef.child("postCount"));


                container.addView(postsFeed);
                return postsFeed;
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
    }
}

