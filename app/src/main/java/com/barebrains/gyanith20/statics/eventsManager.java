package com.barebrains.gyanith20.statics;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.interfaces.NetworkStateListener;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.EventItem;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.barebrains.gyanith20.gyanith20.sp;

public class eventsManager {

    private static EventItem[] eventItems;
    private static SharedPreferences sp;
    private static String allEventsKey;


    public static void getCatEvents(final String catType, final ResultListener<EventItem[]> listener){
        if (eventItems == null)
            eventItems = getEventItemsFromCache();

        if (eventItems == null)
        {
            fetchEventsData(new ResultListener<EventItem[]>(){
                @Override
                public void OnResult(EventItem[] eventItems) {
                    eventsManager.eventItems = eventItems;
                    getCatEvents(catType,listener);
                }

                @Override
                public void OnError(String error) {
                    listener.OnError(error);
                }
            });
            return;
        }
        ArrayList<EventItem> list = new ArrayList<>();
        for (EventItem item : eventItems) {
            if (item.type.equals(catType))
                list.add(item);
        }
        listener.OnResult(list.toArray(new EventItem[0]));
    }

    public static void getEventsbyId(final String[] ids,final ResultListener<EventItem[]> listener){
        if (eventItems == null)
            eventItems = getEventItemsFromCache();

        if (eventItems == null)
        {
            fetchEventsData(new ResultListener<EventItem[]>(){
                @Override
                public void OnResult(EventItem[] eventItems) {
                    eventsManager.eventItems = eventItems;
                    getEventsbyId(ids,listener);
                }

                @Override
                public void OnError(String error) {
                    listener.OnError(error);
                }
            });
            return;
        }

        ArrayList<EventItem> list = new ArrayList<>();
        for (EventItem item : eventItems) {
            for (String id : ids) {
                if (item.id.equals(id))
                    list.add(item);
            }
        }
        listener.OnResult(list.toArray(new EventItem[0]));
    }

    public static void getRegEventsPair(final String[] ids,final ResultListener<Pair<ArrayList<EventItem>,ArrayList<EventItem>>> listener){
        if (eventItems == null)
            eventItems = getEventItemsFromCache();

        if (eventItems == null)
        {
            fetchEventsData(new ResultListener<EventItem[]>(){
                @Override
                public void OnResult(EventItem[] eventItems) {
                    eventsManager.eventItems = eventItems;
                    getRegEventsPair(ids,listener);
                }

                @Override
                public void OnError(String error) {
                    listener.OnError(error);
                }
            });
        }
        getEventsbyId(ids, new ResultListener<EventItem[]>(){
            @Override
            public void OnResult(EventItem[] eventItems) {
                ArrayList<EventItem> w = new ArrayList<>();
                ArrayList<EventItem> te = new ArrayList<>();

                for (EventItem item : eventItems) {
                    if (item.type.equals("w"))
                        w.add(item);
                    else if (item.type.equals("te"))
                        te.add(item);
                }

                listener.OnResult(new Pair<>(w,te));
            }

            @Override
            public void OnError(String error) {
                listener.OnError(error);
            }
        });



    }



    public static void initialize(Context context){
        sp = context.getSharedPreferences(context.getString(R.string.package_name), Context.MODE_PRIVATE);
        allEventsKey = context.getString(R.string.eventItemsKey);

        fetchEventsData(new ResultListener<EventItem[]>(){
            @Override
            public void OnResult(EventItem[] eventItems) {
                eventsManager.eventItems = eventItems;
            }
        });
    }

    private static void fetchEventsData(final ResultListener<EventItem[]> listener){
        String url = "http://gyanith.org/api.php?action=fetchAll&key=2ppagy0";
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                listener.OnResult(gson.fromJson(response.toString(), EventItem[].class));
                sp.edit().putString(allEventsKey,response.toString()).apply();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if error continue using cache
                eventItems = getEventItemsFromCache();
                if (eventItems != null)
                    listener.OnResult(eventItems);
                else
                    listener.OnError("Network Error");
        }
        });
        VolleyManager.requestQueue.add(request);
    }

    //Fetches reg events ids
    private static EventItem[] getEventItemsFromCache(){
        String response = sp.getString(allEventsKey,"");
        if (response.equals(""))
            return null;
        Gson gson = new Gson();
        return gson.fromJson(response, EventItem[].class);
    }
}

class EventsModel extends ViewModel{

    public LiveData<EventItem> getItem(final String id){
        return Transformations.map(getAllEventItems(), new Function<Map<String, EventItem>, EventItem>() {
            @Override
            public EventItem apply(Map<String, EventItem> input) {
                if (input.containsKey(id))
                    return input.get(id);
                else
                    return null;
            }
        });
    }

    public LiveData<EventItem[]> getEventsOfType(final String type){
        return Transformations.map(getAllEventItems(), new Function<Map<String, EventItem>, EventItem[]>() {
            @Override
            public EventItem[] apply(Map<String, EventItem> input) {
                ArrayList<EventItem> items = new ArrayList<>();

                for (EventItem item : input.values())
                    if (item.type.equals(type))
                        items.add(item);

                return items.toArray(new EventItem[0]);
            }
        });
    }

    public LiveData<EventItem[]> getEventsofIds(final ArrayList<String> ids){
        return Transformations.map(getAllEventItems(), new Function<Map<String, EventItem>, EventItem[]>() {
            @Override
            public EventItem[] apply(Map<String, EventItem> input) {
                ArrayList<EventItem> items = new ArrayList<>();

                for (String id : input.keySet()) {
                    if (ids.contains(id)) {
                        items.add(input.get(id));
                    }
                }

                return items.toArray(new EventItem[0]);
            }
        });
    }









    //UnderLying DATA MECHANISM


    MutableLiveData<Map<String,EventItem>> eventItems;

    private static final String allEventsKey = "allEvents";

    private MutableLiveData<Map<String,EventItem>> getAllEventItems(){
        if (eventItems == null){
            fetchEventsData(new ResultListener<EventItem[]>(){
                @Override
                public void OnResult(EventItem[] eventItems) {
                    Map<String,EventItem> itemMap = map(eventItems);
                    if (itemMap != null)
                        EventsModel.this.eventItems.setValue(itemMap);
                }
            });
            return (eventItems = new MutableLiveData<>());
        }
        return eventItems;
    }

    private static void fetchEventsData(final ResultListener<EventItem[]> listener){
        String url = "http://gyanith.org/api.php?action=fetchAll&key=2ppagy0";
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                listener.OnResult(gson.fromJson(response.toString(), EventItem[].class));

                sp.edit().putString(allEventsKey,response.toString()).apply();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if error continue using cache
                EventItem[] a = getEventItemsFromCache();
                if (a != null)
                    listener.OnResult(a);
                else
                    listener.OnError("Network Error");

                NetworkManager.getInstance().addListener(5,new NetworkStateListener(){
                    @Override
                    public void OnAvailable() {
                        fetchEventsData(listener);
                    }
                });
            }
        });
        VolleyManager.requestQueue.add(request);
    }

    //Fetches reg events ids
    private static EventItem[] getEventItemsFromCache(){
        String response = sp.getString(allEventsKey,"");
        if (response.equals(""))
            return null;
        Gson gson = new Gson();
        return gson.fromJson(response, EventItem[].class);
    }

    private static Map<String,EventItem> map(EventItem[] array){
        if (array == null)
            return null;
        Map<String,EventItem> itemMap = new HashMap<>();
        for (EventItem item : array)
            itemMap.put(item.id,item);

        return itemMap;
    }
}
