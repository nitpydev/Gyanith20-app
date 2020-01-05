package com.barebrains.gyanith20.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;

import android.view.View;
import android.view.Window;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.barebrains.gyanith20.adapters.eventCategoriesAdapter;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.interfaces.NetworkStateListener;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.barebrains.gyanith20.statics.eventsManager;

import java.util.ArrayList;
import java.util.Arrays;


public class EventCategoriesActivity extends AppCompatActivity {

    ArrayList<EventItem> eventItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        setContentView(R.layout.activity_event_categories);

        if (!NetworkManager.getInstance().isNetAvailable())
            Toast.makeText(this, "No Internet!", Toast.LENGTH_SHORT).show();

        final ListView eventListView = findViewById(R.id.eveitlv);

        findViewById(R.id.backbut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Intent i1=new Intent(this,EventDetailsActivity.class);
        Intent i =getIntent();
        String catName = i.getStringExtra("category");
        final String catType = i.getStringExtra("type");

        ((TextView)findViewById(R.id.cattitle)).setText(catName);

        final View emptyState = findViewById(R.id.textView14);
        final View progress = findViewById(R.id.catload);
        eventItems = new ArrayList<>();
        final eventCategoriesAdapter adapter =new eventCategoriesAdapter(R.layout.item_event_category
                ,emptyState
                ,progress
                ,eventItems
                ,EventCategoriesActivity.this);
        eventListView.setAdapter(adapter);

        NetworkManager.getInstance().addListener(8448,new NetworkStateListener(){
            @Override
            public void OnChange() {
                eventsManager.getCatEvents(catType,new ResultListener<EventItem[]>(){
                    @Override
                    public void OnResult(final EventItem[] eventItems1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.clear();
                                for (EventItem item : eventItems1)
                                    adapter.add(item);
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }

                    @Override
                    public void OnError(String error) {
                        Toast.makeText(EventCategoriesActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


    @Override
    protected void onDestroy() {
        NetworkManager.getInstance().removeListener(8448);
        super.onDestroy();
    }
}
