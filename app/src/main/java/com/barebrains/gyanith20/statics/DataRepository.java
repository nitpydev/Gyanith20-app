package com.barebrains.gyanith20.statics;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.barebrains.gyanith20.activities.MainActivity;
import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.models.NotificationItem;
import com.barebrains.gyanith20.models.ScheduleItem;
import com.barebrains.gyanith20.models.TechExpoData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;

import static com.barebrains.gyanith20.gyanith20.sp;
import static com.barebrains.gyanith20.others.Response.DATA_EMPTY;
import static com.barebrains.gyanith20.others.Response.NO_DATA_AND_NET;

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

    public static MutableLiveData<ArrayResource<NotificationItem>> getAllNotiItems(){
        if (notiItems == null){
            notiItems = new MutableLiveData<>();
            fetchNotificationItems();
        }

        return notiItems;
    }

    public static MutableLiveData<Resource<TechExpoData>> getTechExpoData(){
        if (techExpoData == null){
            techExpoData = new MutableLiveData<>();
            fetchTechExpoData();
        }

        return techExpoData;
    }


    //EVENT ITEMS FETCHING
    private static MutableLiveData<ArrayResource<EventItem>> eventItems;

    private static final String allEventsKey = "allEvents";

    private static void fetchEventsData(){
        String url = "http://gyanith.org/api.php?action=fetchAll&key=2ppagy0";
        JsonArrayRequest request = new JsonArrayRequest(url, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                eventItems.postValue(ArrayResource.withValue(gson.fromJson(response.toString(), EventItem[].class)));

                //Caching
                sp.edit().putString(allEventsKey,response.toString()).apply();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if error continue using cache
                EventItem[] a = getEventItemsFromCache();

                if (a == null)
                    eventItems.postValue(ArrayResource.<EventItem>onlyCode(NO_DATA_AND_NET));
                else
                    eventItems.postValue(ArrayResource.withValue(a));

                NetworkManager.internet.observeForever(new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean internet) {
                        if (internet) {
                            fetchEventsData();
                            NetworkManager.internet.removeObserver(this);
                        }
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


    //SCHEDULE ITEMS FETCHING

    private static MutableLiveData<ArrayResource<ScheduleItem>> scheduleItems;

    private static void fetchSchedules(){
        FirebaseDatabase.getInstance().getReference().child("Schedule")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            scheduleItems.postValue(ArrayResource.<ScheduleItem>onlyCode(DATA_EMPTY));
                            return;
                        }

                        try {
                            ArrayList<ScheduleItem> items = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ScheduleItem item = snapshot.getValue(ScheduleItem.class);
                                item.key = snapshot.getKey();
                                items.add(item);
                            }

                            scheduleItems.postValue(ArrayResource.withValue(items.toArray(new ScheduleItem[0])));
                        }catch (DatabaseException e){
                            scheduleItems.postValue(ArrayResource.<ScheduleItem>onlyToasts("Internal Error"));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                       scheduleItems.postValue(ArrayResource.<ScheduleItem>autoRespond());
                    }
                });
    }


    //NOTIFICATION ITEMS FETCHING
    private static MutableLiveData<ArrayResource<NotificationItem>> notiItems;
    public static ArrayResource<NotificationItem> notiItems_value;

    public static void fetchNotificationItems() {
        FirebaseDatabase.getInstance().getReference().child("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists()) {
                            notiItems.postValue(ArrayResource.<NotificationItem>onlyCode(DATA_EMPTY));
                            return;
                        }

                        NotificationItem[] items = new NotificationItem[((Long)dataSnapshot.getChildrenCount()).intValue()];

                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                        for (int i = items.length - 1;i >= 0;i--) {
                            DataSnapshot snapshot = iterator.next();
                            try {
                                items[i] = snapshot.getValue(NotificationItem.class);
                                items[i].key = snapshot.getKey();
                            }catch (DatabaseException ignored){}
                        }
                        notiItems.postValue(ArrayResource.withValue(items));

                        if (MainActivity.botNav != null)
                            MainActivity.botNav.updateCount(3, items.length);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                       notiItems.postValue(ArrayResource.<NotificationItem>autoRespond());
                    }
                });

        notiItems.observeForever(new Observer<ArrayResource<NotificationItem>>() {
            @Override
            public void onChanged(ArrayResource<NotificationItem> notificationItemArrayResource) {
                notiItems_value = notificationItemArrayResource;
            }
        });
    }

    //NOTIFICATION ITEMS FETCHING
    private static MutableLiveData<Resource<TechExpoData>> techExpoData;

    public static void fetchTechExpoData() {
        FirebaseDatabase.getInstance().getReference().child("TechExpoData")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists()) {
                            techExpoData.postValue(Resource.<TechExpoData>onlyCode(DATA_EMPTY));
                            return;
                        }

                        TechExpoData data = dataSnapshot.getValue(TechExpoData.class);

                        techExpoData.postValue(Resource.withValue(data));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        techExpoData.postValue(Resource.<TechExpoData>autoRespond());
                    }
                });
    }

    //LOAD URL FOR REGISTRATION
    public static String clg_fever_url;

    public static void fetchClgFeverUrl(){
        FirebaseDatabase.getInstance().getReference().child("clg_fever_url").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    clg_fever_url = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
