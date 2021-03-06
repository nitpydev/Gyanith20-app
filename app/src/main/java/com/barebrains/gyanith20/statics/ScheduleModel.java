package com.barebrains.gyanith20.statics;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.models.ScheduleItem;
import com.barebrains.gyanith20.others.Response;

import java.util.ArrayList;
import java.util.List;

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
                        insertSort(items,item);
                    }
                }

                return input.passFilter(items.toArray(new ScheduleItem[0]));
            }
        });
    }

    public LiveData<ArrayResource<ScheduleItem>> getSchedulesOfDay(final Long dayStart){
        final Long dayEnd = dayStart + 86400000;
        return Transformations.map(getAllScheduleItems(), new Function<ArrayResource<ScheduleItem>, ArrayResource<ScheduleItem>>() {
            @Override
            public ArrayResource<ScheduleItem> apply(ArrayResource<ScheduleItem> input) {
                if (input.value == null)
                    return input;

                ArrayList<ScheduleItem> items = new ArrayList<>();

                for (ScheduleItem item : input.value) {
                    if (item.start_time >= dayStart && item.end_time <= dayEnd) {

                        insertSort(items,item);
                    }
                }

                return input.passFilter(items.toArray(new ScheduleItem[0]));
            }
        });
    }

    void insertSort(ArrayList<ScheduleItem> list,ScheduleItem item){
        int i = list.size() - 1;

        while (i >= 0 && list.get(i).start_time > item.start_time)
            i--;

        list.add(i + 1,item);
    }
}
