package com.barebrains.gyanith20.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.barebrains.gyanith20.Adapters.scheduleAdapter;
import com.barebrains.gyanith20.Models.schitem;
import com.barebrains.gyanith20.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class ScheduleTab3_Fragment extends Fragment {

    private ListView scheduleList;
    private DatabaseReference ref;
    private schitem it1;
    private scheduleAdapter adapter;
    private ArrayList<schitem> list;

    public ScheduleTab3_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root= inflater.inflate(R.layout.fragment_schtab3, container, false);
        scheduleList = root.findViewById(R.id.sch3);
        ref = FirebaseDatabase.getInstance().getReference().child("ScheduleFragment");
        list = new ArrayList<schitem>();
        adapter = new scheduleAdapter(getContext(), list, R.layout.item_schedule);

        ref.child("day2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String starttime = snapshot.child("startTime").getValue().toString();//timeFormatter("gsadg");
                    String endtime = snapshot.child("endTime").getValue().toString();

                    Long now = Calendar.getInstance().getTimeInMillis();
                    Long start = Long.parseLong(starttime);
                    Long end = Long.parseLong(endtime);
                    Boolean live;

                    if(now >= start && now <= end ) live = true;
                    else live = false;

                    it1 = new schitem(snapshot.child("venue").getValue().toString(),timeFormatter(start),snapshot.child("name").getValue().toString(), live );
                    list.add(it1);
                }
                Collections.sort(list,new mycomparator());
                adapter.notifyDataSetChanged();
                ScheduleFragment.gone();

                if (list.isEmpty()) ((TextView)root.findViewById(R.id.update3)).setVisibility(View.VISIBLE);
                else ((TextView)root.findViewById(R.id.update3)).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        scheduleList.setAdapter(adapter);

        return root;
    }

    public String timeFormatter(Long timeInt)
    {
        SimpleDateFormat s=new SimpleDateFormat("HH:mm");
        Date d=new Date(timeInt);
        return s.format(d);
    }

    public class mycomparator implements Comparator<schitem> {

        @Override
        public int compare(schitem o1, schitem o2) {
            return o1.getTime().compareTo(o2.getTime());
        }
    }
}
