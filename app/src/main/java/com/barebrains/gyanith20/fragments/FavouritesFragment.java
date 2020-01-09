package com.barebrains.gyanith20.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.barebrains.gyanith20.activities.EventDetailsActivity;
import com.barebrains.gyanith20.adapters.eventCategoriesAdapter;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.others.mFragment;
import com.barebrains.gyanith20.statics.eventsManager;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class FavouritesFragment extends mFragment {


    eventCategoriesAdapter adapter;
    SharedPreferences sp;

    private FavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
     if(!hidden)
        refreshFavs();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_favourites, container, false);

        View emptyState = root.findViewById(R.id.textView13);
        View progress = root.findViewById(R.id.favload);

        adapter = new eventCategoriesAdapter(R.layout.item_event_category
        ,emptyState
        ,progress
        ,new ArrayList<EventItem>()
        ,getContext());

        ListView favListView = root.findViewById(R.id.favlv);

        favListView.setAdapter(adapter);

        refreshFavs();
        return root;

    }



    private void refreshFavs(){
        sp = getContext().getSharedPreferences(getString(R.string.package_name), Context.MODE_PRIVATE);
        Set<String> favIds = sp.getStringSet(getString(R.string.favSet),new HashSet<String>());

        eventsManager.getEventsbyId(favIds.toArray(new String[0]),new ResultListener<EventItem[]>(){
            @Override
            public void OnResult(EventItem[] eventItems) {
                adapter.clear();
                for (EventItem item : eventItems)
                    adapter.add(item);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void OnError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    //SINGLETON
    private static FavouritesFragment instance;

    public static FavouritesFragment getInstance(){
        if (instance == null)
            instance = new FavouritesFragment();
        return instance;
    }
}
