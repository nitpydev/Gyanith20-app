package com.barebrains.gyanith20.statics;

import android.content.Context;
import android.content.SharedPreferences;

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

public class eventsManager {

    private static EventItem[] eventItems;

    private static SharedPreferences sp;
    private static String key;


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

    public static void getEventsbyId(final ArrayList<String> ids,final ResultListener<EventItem[]> listener){
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
                if (item.id.equals(id)) {
                    list.add(item);
                    ids.remove(id);
                }
            }
        }
        listener.OnResult(list.toArray(new EventItem[0]));
    }



    public static void initialize(Context context){
        sp = context.getSharedPreferences(context.getString(R.string.package_name), Context.MODE_PRIVATE);
        key = context.getString(R.string.eventItemsKey);

        fetchEventsData(new ResultListener<EventItem[]>(){
            @Override
            public void OnResult(EventItem[] eventItems) {
                eventsManager.eventItems = eventItems;
            }
        });
        /*NetworkManager.getInstance().addListener(87,new NetworkStateListener(){
            @Override
            public void OnAvailable() {
                fetchEventsData(new ResultListener<EventItem[]>(){
                    @Override
                    public void OnResult(EventItem[] eventItems) {
                        eventsManager.eventItems = eventItems;
                    }
                });
            }

            @Override
            public void OnDisconnected() {
                eventsManager.eventItems = getEventItemsFromCache();
            }
        });

         */
    }

    private static void fetchEventsData(final ResultListener<EventItem[]> listener){
        String url = "http://gyanith.org/api.php?action=fetchAll&key=2ppagy0";
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                listener.OnResult(gson.fromJson(response.toString(), EventItem[].class));
                sp.edit().putString(key,response.toString()).apply();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if error continue using cache
                EventItem[] eventItems = getEventItemsFromCache();
                if (eventItems != null)
                    listener.OnResult(eventItems);
                else
                    listener.OnError("Network Error");
        }
        });
        VolleyManager.requestQueue.add(request);
    }

    private static EventItem[] getEventItemsFromCache(){
        String response = sp.getString(key,"");
        if (response.equals(""))
            return null;
        Gson gson = new Gson();
        return gson.fromJson(response, EventItem[].class);
    }
}
