package com.barebrains.gyanith20.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.barebrains.gyanith20.activities.AddScheduleActivity;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.adapters.LiveListAdapter;
import com.barebrains.gyanith20.adapters.scheduleViewHolder;
import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.models.ScheduleItem;
import com.barebrains.gyanith20.others.mFragment;
import com.barebrains.gyanith20.statics.ScheduleModel;
import com.google.android.material.tabs.TabLayout;


public class ScheduleFragment extends mFragment {

    private ScheduleModel viewModel;
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
        final View root = inflater.inflate(R.layout.fragment_schedule, container, false);

        root.findViewById(R.id.add_schedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleFragment.this.getActivity(), AddScheduleActivity.class);
                startActivity(intent);
            }
        });

        TabLayout mtabLayout = root.findViewById(R.id.schtabLayout);
        ViewPager viewPager = root.findViewById(R.id.viewpager);
        viewPager.setAdapter(new pager());
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(4);
        mtabLayout.setupWithViewPager(viewPager);
        super.onCreateView(inflater, container, savedInstanceState);
        return root;
    }

    private class pager extends PagerAdapter{

        private String[] titles = new String[]{"LIVE","DAY 1","DAY 2","DAY 3","DAY 4"};

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            RecyclerView item = new RecyclerView(getContext());
            scheduleAdapter adapter = getSchduleAdapter(position);

            if (position == 0)
                adapter.getLoader().set_empty_error("No Events Live !");
            else
                adapter.getLoader().set_empty_error("Will be Updated Soon");

            item.setAdapter(adapter);
            container.addView(adapter.getLoader());
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

    public abstract class scheduleAdapter extends LiveListAdapter<ScheduleItem, scheduleViewHolder> {


        public scheduleAdapter() {
            super(ScheduleFragment.this.getContext(),ScheduleFragment.this.getViewLifecycleOwner(), R.layout.item_schedule);
        }

        @Override
        public abstract LiveData<ArrayResource<ScheduleItem>> getLiveData();

        @NonNull
        @Override
        public scheduleViewHolder createViewHolder(View ItemView) {
            return new scheduleViewHolder(ItemView,ScheduleFragment.this.getActivity());
        }

    }
}
