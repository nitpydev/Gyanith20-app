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
import android.widget.TableLayout;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.adapters.eventCatAdapter;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.EventsModel;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

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
    }

    private class pager extends PagerAdapter{

        String[] emptyStates = new String[]{"Your Registered Workshops show up here"
                                    ,"Your Registered Technical Events show up here"
                                    ,"Tap post button and add a post that end up here"};
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            Loader loader = new Loader(Profile2Activity.this);
            loader.set_empty_error(emptyStates[position]);

            if (position != 2){
                final ListView listView = new ListView(Profile2Activity.this);
                final eventCatAdapter adapter;
                adapter = new eventCatAdapter(Profile2Activity.this,Profile2Activity.this,R.layout.item_event_category) {

                    @Nullable
                    @Override
                    public LiveData<Resource<EventItem>> getLiveData() {
                        return eventsModel.getEventsofIdsandType((position == 0)?"w":"te",user.regEventIds);
                    }
                };


                listView.setAdapter(adapter);
                loader.addView(listView );
                adapter.observe();
                return loader;
            }
            else {
                return loader;
            }

        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}

