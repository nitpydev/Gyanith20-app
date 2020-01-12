package com.barebrains.gyanith20.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.barebrains.gyanith20.adapters.eventCatAdapter;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.others.mFragment;
import com.barebrains.gyanith20.statics.EventsModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.barebrains.gyanith20.gyanith20.sp;


public class FavouritesFragment extends mFragment {

    private EventsModel eventsModel;
    private eventCatAdapter adapter;

    public FavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsModel = ViewModelProviders.of(this).get(EventsModel.class);
    }

    @Override
    public void onShow() {
        super.onShow();
        if (adapter != null)
            adapter.refresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_favourites, container, false);

        Loader loader = root.findViewById(R.id.fav_loader);
        ListView favListView = root.findViewById(R.id.favlv);

        adapter = new eventCatAdapter(getContext(),getViewLifecycleOwner(),R.layout.item_event_category){
            @Nullable
            @Override
            public LiveData<Resource<EventItem>> getLiveData() {
                sp = getContext().getSharedPreferences(getString(R.string.package_name), Context.MODE_PRIVATE);
                Set<String> favIds = sp.getStringSet(getString(R.string.favSet),new HashSet<String>());
                return eventsModel.getEventsofIds(new ArrayList<>(favIds));
            }
        };
        adapter.setLoader(loader);
        favListView.setAdapter(adapter);
        return root;

    }

    //SINGLETON
    private static FavouritesFragment instance;

    public static FavouritesFragment getInstance(){
        if (instance == null)
            instance = new FavouritesFragment();
        return instance;
    }
}
