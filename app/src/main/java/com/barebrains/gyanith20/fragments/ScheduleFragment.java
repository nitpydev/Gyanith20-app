package com.barebrains.gyanith20.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.activities.EventDetailsActivity;
import com.barebrains.gyanith20.adapters.LiveListAdapter;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.models.ScheduleItem;
import com.barebrains.gyanith20.others.mFragment;
import com.barebrains.gyanith20.statics.ScheduleModel;
import com.barebrains.gyanith20.statics.Util;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;


public class ScheduleFragment extends mFragment {

    private ScheduleModel viewModel;
    int res = R.layout.item_schedule;

    public ScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ScheduleModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root= inflater.inflate(R.layout.fragment_schedule, container, false);
        TabLayout mtabLayout = root.findViewById(R.id.schtabLayout);
        ViewPager viewPager = root.findViewById(R.id.viewpager);
        viewPager.setAdapter(new pager());
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(4);
        mtabLayout.setupWithViewPager(viewPager);
        return root;
    }

    private class pager extends PagerAdapter{

        private String[] titles = new String[]{"LIVE","DAY 1","DAY 2","DAY 3","DAY 4"};

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            ListView item = new ListView(getContext());
            scheduleAdapter adapter = getSchduleAdapter(position);
            item.setAdapter(adapter);

            adapter.setLoader(new Loader(getContext()));
            if (position == 0)
                adapter.getLoader().set_empty_error("No Events Live !");
            else
                adapter.getLoader().set_empty_error("Will be Updated Soon");

            adapter.getLoader().addView(item);
            container.addView(adapter.getLoader());
            adapter.observe();
            return adapter.getLoader();
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    private scheduleAdapter getSchduleAdapter(int pos){
        switch (pos){
            case 0:
                return new scheduleAdapter() {
                    @Override
                    public LiveData<ArrayResource<ScheduleItem>> getLiveData() {
                        return viewModel.getLiveSchedules(System.currentTimeMillis());
                    }
                };
            case 1://26 th February
                return new scheduleAdapter() {
                    @Override
                    public LiveData<ArrayResource<ScheduleItem>> getLiveData() {
                        return viewModel.getSchedulesOfDay(1582655400000L,1582741800000L);
                    }
                };
            case 2://27 th February
                return new scheduleAdapter() {
                    @Override
                    public LiveData<ArrayResource<ScheduleItem>> getLiveData() {
                        return viewModel.getSchedulesOfDay(1582741800000L,1582828200000L);
                    }
                };
            case 3://28 th February
                return new scheduleAdapter() {
                    @Override
                    public LiveData<ArrayResource<ScheduleItem>> getLiveData() {
                        return viewModel.getSchedulesOfDay(1582828200000L,1582914600000L);
                    }
                };
            case 4://29 th February
                return new scheduleAdapter() {
                    @Override
                    public LiveData<ArrayResource<ScheduleItem>> getLiveData() {
                        return viewModel.getSchedulesOfDay(1582914600000L,1583001000000L);
                    }
                };
            default:
                return null;

        }
    }



    //SINGLETON
    private static ScheduleFragment instance;

    public static ScheduleFragment getInstance(){
        if (instance == null)
            instance = new ScheduleFragment();
        return instance;
    }

    private abstract class scheduleAdapter extends LiveListAdapter<ScheduleItem> {

        scheduleAdapter(){
            super(ScheduleFragment.this.getContext()
                    ,ScheduleFragment.this.getViewLifecycleOwner(),res);
        }

        @Override
        public abstract LiveData<ArrayResource<ScheduleItem>> getLiveData();



        @Override
        public void bindView(View view, final ScheduleItem data) {
            TextView time = view.findViewById(R.id.time);
            TextView title = view.findViewById(R.id.title);
            TextView venue = view.findViewById(R.id.venue);
            View live = view.findViewById(R.id.liveindicator);
            View btn = view.findViewById(R.id.btn);
            time.setText(formatTime(data.start_time));
            title.setText(data.title);
            venue.setText(data.venue);

            if (data.isLive())
                live.setVisibility(View.VISIBLE);
            else
                live.setVisibility(View.INVISIBLE);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.id != null){
                        Intent intent = new Intent(getContext(),EventDetailsActivity.class);
                        intent.putExtra("EXTRA_ID",data.id);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public View createView() {
            return LayoutInflater.from(getContext()).inflate(res, null);
        }

        private String formatTime(Long time){
            Calendar cl = Calendar.getInstance();
            cl.setTimeInMillis(time);
            return cl.get(Calendar.HOUR) + ":"
                    + cl.get(Calendar.MINUTE) + " "
                    + Util.amPm(cl.get(Calendar.AM_PM));
        }
    }
}
