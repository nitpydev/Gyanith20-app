package com.barebrains.gyanith20.statics;

import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.others.LoaderException;

import java.util.ArrayList;
import java.util.Map;

import static com.barebrains.gyanith20.statics.DataRepository.getAllEventItems;

public class EventsModel extends ViewModel {

    public LiveData<Resource<EventItem>> getItem(final String id){
        return Transformations.map(getAllEventItems(), new Function<Resource<EventItem>, Resource<EventItem>>() {
            @Override
            public Resource<EventItem> apply(Resource<EventItem> input) {
               if (input.value == null)
                   return input;

               for (EventItem item : input.value) {
                   if (item.id.equals(id))
                       return new Resource<>(new EventItem[]{item},null);
               }

               return new Resource<>(null, new LoaderException(0));
            }
        });
    }

    public LiveData<Resource<EventItem>> getEventsOfType(final String type){
        return Transformations.map(getAllEventItems(), new Function<Resource<EventItem>,Resource<EventItem>>() {
            @Override
            public Resource<EventItem> apply(Resource<EventItem> input) {
                if (input.value == null)
                    return input;

                ArrayList<EventItem> items = new ArrayList<>();

                for (EventItem item : input.value)
                    if (item.type.equals(type))
                        items.add(item);

                EventItem[] result = items.toArray(new EventItem[0]);

                if (result.length == 0)
                    return new Resource<>(null,new LoaderException(0));
                else
                    return new Resource<>(result,null);
            }
        });
    }

    public LiveData<Resource<EventItem>> getEventsofIds(final ArrayList<String> ids){
        return Transformations.map(getAllEventItems(), new Function<Resource<EventItem>,Resource<EventItem>>() {
            @Override
            public Resource<EventItem> apply(Resource<EventItem> input) {

                if (input.value == null)
                    return input;

                ArrayList<EventItem> items = new ArrayList<>();
                for (EventItem item : input.value) {
                    if (ids.contains(item.id)) {
                        items.add(item);
                    }
                }

                EventItem[] result = items.toArray(new EventItem[0]);
                if (result.length == 0)
                    return new Resource<>(null,new LoaderException(0));
                else
                    return new Resource<>(result,new LoaderException(null,input.error.getMessage()));
            }
        });
    }
}
