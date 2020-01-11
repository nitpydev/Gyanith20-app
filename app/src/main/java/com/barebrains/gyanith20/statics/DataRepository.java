package com.barebrains.gyanith20.statics;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.barebrains.gyanith20.activities.MainActivity;
import com.barebrains.gyanith20.interfaces.NetworkStateListener;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.models.NotificationItem;
import com.barebrains.gyanith20.models.ScheduleItem;
import com.barebrains.gyanith20.others.LoaderException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.barebrains.gyanith20.gyanith20.sp;

class DataRepository {

    //PUBLIC FUNCTIONS
    static MutableLiveData<Resource<ScheduleItem>> getAllScheduleItems(){
        if (scheduleItems == null)
        {
            scheduleItems = new MutableLiveData<>();
            fetchSchedules(new ResultListener<ScheduleItem[]>(){
                @Override
                public void OnResult(ScheduleItem[] items) {

                    if (items != null)
                        scheduleItems.setValue(new Resource<>(items, null));
                    else
                        scheduleItems.setValue(new Resource<ScheduleItem>(null,new LoaderException(0)));
                }

                @Override
                public void OnError(String error) {
                    scheduleItems.setValue(new Resource<ScheduleItem>(null,new LoaderException(0)));
                }
            });

        }
        return scheduleItems;
    }

    static MutableLiveData<Resource<EventItem>> getAllEventItems(){
        if (eventItems == null){
            eventItems = new MutableLiveData<>();
            fetchEventsData(new ResultListener<EventItem[]>(){
                @Override
                public void OnResult(EventItem[] items) {
                    if (items == null || items.length == 0)
                        eventItems.setValue(new Resource<EventItem>(null,new LoaderException(0)));
                    else
                        eventItems.setValue(new Resource<>(items,null));
                }

                @Override
                public void OnError(String error) {
                    eventItems.setValue(new Resource<EventItem>(null,new LoaderException(0)));
                }
            });
        }
        return eventItems;
    }







    //EVENT ITEMS FETCHING
    private static MutableLiveData<Resource<EventItem>> eventItems;

    private static final String allEventsKey = "allEvents";

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


    //SCHEDULE ITEMS FETCHING

    private static MutableLiveData<Resource<ScheduleItem>> scheduleItems;

    private static void fetchSchedules(final ResultListener<ScheduleItem[]> listener){
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


    /*//NOTIFICATION ITEMS FETCHING
    private static MutableLiveData<Resource<NotificationItem>> notiItems;


    public static void fetchNotificationItems(final ResultListener<NotificationItem[]> listener){
        FirebaseDatabase.getInstance().getReference().child("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<NotificationItem> items = new ArrayList<>();

                        for (DataSnapshot snap : dataSnapshot.getChildren())
                            items.add(snap.getValue(NotificationItem.class));

                        if (items.size() != 0)
                            notiItems.setValue(new Resource<NotificationItem>(items.toArray(new NotificationItem[0]),null));
                        else
                            notiItems.setValue(new Resource<NotificationItem>());


                        if (MainActivity.botNav != null)
                            MainActivity.botNav.updateCount(3,items.size());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (!NetworkManager.getInstance().isNetAvailable()) {
                                listener.OnError("No Internet");
                            return;
                        }

                        listener.OnError(null);//TODO:CHANGE EXCEPTION

                    }
                });
    }
    
     */

}
