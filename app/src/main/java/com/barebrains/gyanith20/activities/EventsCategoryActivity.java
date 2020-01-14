package com.barebrains.gyanith20.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;

import android.view.View;
import android.view.Window;

import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.adapters.eventCatAdapter;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.statics.EventsModel;


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

        findViewById(R.id.backbtn).setOnClickListener(new View.OnClickListener() {
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
            public LiveData<ArrayResource<EventItem>> getLiveData() {
                return viewModel.getEventsOfType(catType);
            }
        };
        adapter.setLoader(loader);
        eventCatList.setAdapter(adapter);
        adapter.observe();
    }
}


