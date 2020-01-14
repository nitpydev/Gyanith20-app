package com.barebrains.gyanith20.statics;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.models.ScheduleItem;
import com.barebrains.gyanith20.others.LoaderException;

import java.util.ArrayList;

import static com.barebrains.gyanith20.statics.DataRepository.getAllScheduleItems;

public class ScheduleModel extends ViewModel {


    public LiveData<ArrayResource<ScheduleItem>> getLiveSchedules(final Long curTime){
        return Transformations.map(getAllScheduleItems(), new Function<ArrayResource<ScheduleItem>, ArrayResource<ScheduleItem>>() {
            @Override
            public ArrayResource<ScheduleItem> apply(ArrayResource<ScheduleItem> input) {
                if (input.value == null)
                    return input;
                ArrayList<ScheduleItem> items = new ArrayList<>();

                for (ScheduleItem item : input.value) {
                    if (curTime >= item.start_time && curTime <= item.end_time) {
                        items.add(item);
                    }
                }
                if (items.size() != 0)
                    return new ArrayResource<>(items.toArray(new ScheduleItem[0]),input.error);
                else
                    return new ArrayResource<>(null, new LoaderException(0,(input.error != null)?input.error.getMessage():null));            }
        });
    }

    public LiveData<ArrayResource<ScheduleItem>> getSchedulesOfDay(final Long dayStart, final Long dayEnd){
        return Transformations.map(getAllScheduleItems(), new Function<ArrayResource<ScheduleItem>, ArrayResource<ScheduleItem>>() {
            @Override
            public ArrayResource<ScheduleItem> apply(ArrayResource<ScheduleItem> input) {
                if (input.value == null)
                    return input;

                ArrayList<ScheduleItem> items = new ArrayList<>();

                for (ScheduleItem item : input.value) {
                    if (item.start_time >= dayStart && item.end_time <= dayEnd) {
                        items.add(item);
                    }
                }
                if (items.size() != 0)
                    return new ArrayResource<>(items.toArray(new ScheduleItem[0]),input.error);
                else
                    return new ArrayResource<>(null, new LoaderException(0,input.error.getMessage()));
            }
        });
    }


}
