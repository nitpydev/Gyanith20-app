package com.barebrains.gyanith20.statics;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.ScheduleItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleModel extends ViewModel {


    public LiveData<Resource<ScheduleItem>> getLiveSchedules(final Long curTime){
        return Transformations.map(getAllScheduleItems(), new Function<Resource<ScheduleItem>, Resource<ScheduleItem>>() {
            @Override
            public Resource<ScheduleItem> apply(Resource<ScheduleItem> input) {
                Log.d("asd","a : " + input.value.length);
                if (input.value == null)
                    return input;
                ArrayList<ScheduleItem> items = new ArrayList<>();

                for (ScheduleItem item : input.value) {
                    if (curTime >= item.start_time && curTime <= item.end_time) {
                        items.add(item);
                    }
                }
                return new Resource<>(items.toArray(new ScheduleItem[0]),input.error);
            }
        });
    }

    public LiveData<Resource<ScheduleItem>> getSchedulesOfDay(final Long dayStart, final Long dayEnd){
        return Transformations.map(getAllScheduleItems(), new Function<Resource<ScheduleItem>, Resource<ScheduleItem>>() {
            @Override
            public Resource<ScheduleItem> apply(Resource<ScheduleItem> input) {
                if (input.value == null)
                    return input;


                ArrayList<ScheduleItem> items = new ArrayList<>();

                for (ScheduleItem item : input.value) {
                    if (item.start_time >= dayStart && item.end_time <= dayEnd) {

                        items.add(item);
                    }
                }

                return new Resource<>(items.toArray(new ScheduleItem[0]),input.error);
            }
        });
    }


    //DATA FETCHING MECHANISM
    private MutableLiveData<Resource<ScheduleItem>> scheduleItems;

    private MutableLiveData<Resource<ScheduleItem>> getAllScheduleItems(){
        if (scheduleItems == null)
        {
            scheduleItems = new MutableLiveData<>();
            fetchSchedules(new ResultListener<ScheduleItem[]>(){
                @Override
                public void OnResult(ScheduleItem[] items) {

                    if (items != null)
                        scheduleItems.setValue(new Resource<>(items, null));
                    else
                        scheduleItems.setValue(new Resource<ScheduleItem>(null,new NullPointerException()));
                    Log.d("asd","af " + scheduleItems.getValue().value.length);
                }

                @Override
                public void OnError(String error) {
                    scheduleItems.setValue(new Resource<ScheduleItem>(null,new NullPointerException()));
                }
            });

        }
        return scheduleItems;
    }

    private void fetchSchedules(final ResultListener<ScheduleItem[]> listener){
        FirebaseDatabase.getInstance().getReference().child("Schedule")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChildren()) {
                            listener.OnError("NO DATA FOUND");
                            return;
                        }

                        try {
                            ArrayList<ScheduleItem> items = new ArrayList<>();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                items.add(snapshot.getValue(ScheduleItem.class));
                            listener.OnResult(items.toArray(new ScheduleItem[0]));
                        }catch (DatabaseException e){
                            Log.d("asd","error : " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.OnError("NO DATA FOUND");
                    }
                });
    }
}
