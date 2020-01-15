package com.barebrains.gyanith20.statics;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.barebrains.gyanith20.activities.MainActivity;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.ArrayResource;
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

import static com.barebrains.gyanith20.gyanith20.sp;
import static com.barebrains.gyanith20.others.LoaderException.DATA_EMPTY;
import static com.barebrains.gyanith20.others.LoaderException.NO_DATA_AND_NET;

public class DataRepository {

    //PUBLIC FUNCTIONS
    static MutableLiveData<ArrayResource<ScheduleItem>> getAllScheduleItems(){
        if (scheduleItems == null){
            scheduleItems = new MutableLiveData<>();
            fetchSchedules();
        }
        return scheduleItems;
    }

    public static MutableLiveData<ArrayResource<EventItem>> getAllEventItems(){
        if (eventItems == null){
            eventItems = new MutableLiveData<>();
            fetchEventsData();
        }
        return eventItems;
    }







    //EVENT ITEMS FETCHING
    private static MutableLiveData<ArrayResource<EventItem>> eventItems;

    private static final String allEventsKey = "allEvents";

    private static void fetchEventsData(){
        String url = "http://gyanith.org/api.php?action=fetchAll&key=2ppagy0";
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                eventItems.postValue(new ArrayResource<>(gson.fromJson(response.toString(), EventItem[].class),new LoaderException(null)));

                sp.edit().putString(allEventsKey,response.toString()).apply();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if error continue using cache
                EventItem[] a = getEventItemsFromCache();
                if (a == null || a.length == 0)
                    eventItems.postValue(new ArrayResource<EventItem>(null,new LoaderException(NO_DATA_AND_NET)));
                else
                    eventItems.postValue(new ArrayResource<>(a, new LoaderException(null)));

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


    //SCHEDULE ITEMS FETCHING

    private static MutableLiveData<ArrayResource<ScheduleItem>> scheduleItems;

    private static void fetchSchedules(){
        FirebaseDatabase.getInstance().getReference().child("Schedule")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            scheduleItems.postValue(new ArrayResource<ScheduleItem>(null,new LoaderException(DATA_EMPTY,null)));
                            return;
                        }

                        try {
                            ArrayList<ScheduleItem> items = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                items.add(snapshot.getValue(ScheduleItem.class));

                            scheduleItems.postValue(new ArrayResource<>(items.toArray(new ScheduleItem[0]),new LoaderException(null,null)));
                        }catch (DatabaseException e){
                            scheduleItems.postValue(new ArrayResource<ScheduleItem>(null,new LoaderException(null,"Internal Error")));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (NetworkManager.internet_value)
                            scheduleItems.postValue(new ArrayResource<ScheduleItem>(null,new LoaderException(DATA_EMPTY,null)));
                        else
                            scheduleItems.postValue(new ArrayResource<ScheduleItem>(null,new LoaderException(NO_DATA_AND_NET,null)));
                    }
                });
    }


    //NOTIFICATION ITEMS FETCHING
    private static MutableLiveData<ArrayResource<NotificationItem>> notiItems;
    public static ArrayResource<NotificationItem> notiItems_value;


    public static MutableLiveData<ArrayResource<NotificationItem>> getNotiItems(){
        if (notiItems == null)
        {
            notiItems = new MutableLiveData<>();
            fetchNotificationItems();
        }

        return notiItems;
    }


    public static void fetchNotificationItems() {
        FirebaseDatabase.getInstance().getReference().child("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists()) {
                            notiItems.postValue(new ArrayResource<NotificationItem>(null, new LoaderException(DATA_EMPTY, null)));
                            return;
                        }

                        ArrayList<NotificationItem> items = new ArrayList<>();

                        for (DataSnapshot snap : dataSnapshot.getChildren())
                            items.add(snap.getValue(NotificationItem.class));

                        if (items.size() != 0)
                            notiItems.setValue(new ArrayResource<>(items.toArray(new NotificationItem[0]), new LoaderException(null)));
                        else
                            notiItems.setValue(new ArrayResource<NotificationItem>(null, new LoaderException(DATA_EMPTY)));


                        if (MainActivity.botNav != null)
                            MainActivity.botNav.updateCount(3, items.size());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (NetworkManager.internet_value)
                            notiItems.postValue(new ArrayResource<NotificationItem>(null, new LoaderException(DATA_EMPTY, null)));
                        else
                            notiItems.postValue(new ArrayResource<NotificationItem>(null, new LoaderException(NO_DATA_AND_NET, null)));

                    }
                });

        notiItems.observeForever(new Observer<ArrayResource<NotificationItem>>() {
            @Override
            public void onChanged(ArrayResource<NotificationItem> notificationItemArrayResource) {
                notiItems_value = notificationItemArrayResource;
            }
        });
    }
}
