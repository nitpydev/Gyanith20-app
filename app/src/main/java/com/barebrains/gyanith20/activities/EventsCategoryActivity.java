package com.barebrains.gyanith20.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.adapters.LiveListAdapter;
import com.barebrains.gyanith20.adapters.eventCatAdapter;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.statics.EventsModel;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class EventsCategoryActivity extends AppCompatActivity {

    private int res = R.layout.item_schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        setContentView(R.layout.activity_event_categories);

        //VIEW BINDINGS
        ListView eventCatList = findViewById(R.id.eve_cat);
        Loader loader = findViewById(R.id.eve_cat_loader);
        TextView catTitleText = findViewById(R.id.cattitle);



        if (!NetworkManager.getInstance().isNetAvailable())
            Toast.makeText(this, "No Internet!", Toast.LENGTH_SHORT).show();


        findViewById(R.id.backbut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Intent i = getIntent();
        String catName = i.getStringExtra("category");
        final String catType = i.getStringExtra("type");
        catTitleText.setText(catName);

        final EventsModel viewModel = ViewModelProviders.of(this).get(EventsModel.class);

        eventCatAdapter adapter = new eventCatAdapter(this,this,res) {
            @NonNull
            @Override
            public LiveData<Resource<EventItem>> getLiveData() {
                return viewModel.getEventsOfType(catType);
            }
        };
        adapter.setLoader(loader);
        eventCatList.setAdapter(adapter);
        adapter.observe();
    }
}


