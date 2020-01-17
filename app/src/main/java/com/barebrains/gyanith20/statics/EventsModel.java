package com.barebrains.gyanith20.statics;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.others.Response;

import java.util.ArrayList;

import static com.barebrains.gyanith20.others.Response.DATA_EMPTY;
import static com.barebrains.gyanith20.statics.DataRepository.getAllEventItems;

public class EventsModel extends ViewModel {

    public LiveData<Resource<EventItem>> getItem(final String id){
        return Transformations.map(getAllEventItems(), new Function<ArrayResource<EventItem>, Resource<EventItem>>() {
            @Override
            public Resource<EventItem> apply(ArrayResource<EventItem> input) {
               if (input.value == null)
                   return Resource.onlyResponse(input.response);

               for (EventItem item : input.value) {
                   if (item.id.equals(id))
                       return Resource.withValue(item);
               }

               return Resource.onlyCode(DATA_EMPTY);
            }
        });
    }

    public LiveData<ArrayResource<EventItem>> getEventsOfType(final String type){
        return Transformations.map(getAllEventItems(), new Function<ArrayResource<EventItem>, ArrayResource<EventItem>>() {
            @Override
            public ArrayResource<EventItem> apply(ArrayResource<EventItem> input) {
                if (input.value == null)
                    return input;

                ArrayList<EventItem> items = new ArrayList<>();

                for (EventItem item : input.value)
                    if (item.type.equals(type))
                        items.add(item);

                EventItem[] result = items.toArray(new EventItem[0]);

                return input.passFilter(result);
            }
        });
    }

    public LiveData<ArrayResource<EventItem>> getEventsofIds(final ArrayList<String> ids){
        return Transformations.map(getAllEventItems(), new Function<ArrayResource<EventItem>, ArrayResource<EventItem>>() {
            @Override
            public ArrayResource<EventItem> apply(ArrayResource<EventItem> input) {
                if (input.value == null)
                    return input;

                EventItem[] items = new EventItem[ids.size()];

                for (EventItem item : input.value) {
                    if (ids.contains(item.id)) {
                        items[ids.indexOf(item.id)] = item;
                    }
                }

                ArrayList<EventItem> result = new ArrayList<>();
               for (EventItem item : items)
                   if (item != null)
                       result.add(item);

                   items = result.toArray(new EventItem[0]);

                return input.passFilter(items);
            }
        });
    }

    public LiveData<ArrayResource<EventItem>> getEventsofIdsandType(final String type, final ArrayList<String> ids){
        return Transformations.map(getAllEventItems(), new Function<ArrayResource<EventItem>, ArrayResource<EventItem>>() {
            @Override
            public ArrayResource<EventItem> apply(ArrayResource<EventItem> input) {

                if (input.value == null)
                    return input;

                ArrayList<EventItem> items = new ArrayList<>();
                for (EventItem item : input.value) {
                    if (ids.contains(item.id) && item.type.equals(type)) {
                        items.add(item);
                    }
                }

                EventItem[] result = items.toArray(new EventItem[0]);

                return input.passFilter(result);
            }
        });
    }

}
