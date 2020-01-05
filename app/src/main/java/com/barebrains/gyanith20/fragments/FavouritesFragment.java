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


public class FavouritesFragment extends Fragment {


    eventCategoriesAdapter adapter;
    SharedPreferences sp;

    public FavouritesFragment() {
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

        eventsManager.getEventsbyId(new ArrayList<>(favIds),new ResultListener<EventItem[]>(){
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

/*
    public String timeFormatter(String time)
    {
        long timeInt = Long.parseLong(time);
        SimpleDateFormat s=new SimpleDateFormat("MMM dd");

        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(timeInt);
        if(c.getTime().getDate()== Calendar.getInstance().getTime().getDate())
            return "Today";
        else if(c.getTime().getDate()== Calendar.getInstance().getTime().getDate()+1)
            return "Tommorow";

        Date d=new Date(timeInt);
        return s.format(d);
    }
        // updating the favorite
    public void update(View root)
    {
        lvi=root.findViewById(R.id.favlv);

        sp = getContext().getSharedPreferences(getString(R.string.package_name), Context.MODE_PRIVATE);
        json_string = getContext().getSharedPreferences(PREFS,Context.MODE_PRIVATE);
        items=new ArrayList<EventItem>();

        final Intent i1 =new Intent(this.getContext(), EventDetailsActivity.class);
        ada=new eventCategoriesAdapter(R.layout.item_event_category,(EventItem[]) items.toArray(),this.getContext());
        tag = new ArrayList();
        cache = json_string.getString(PREF_KEY, "NOTHING");
        items.clear();
        try
        {
            JSONArray jsonArray = new JSONArray(cache);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                name = jsonobject.getString("name");
                timestamp = jsonobject.getString("timestamp");
                id = jsonobject.getString("id");
                type = jsonobject.getString("type");
                img2 = jsonobject.getString("iconImgUrl");

                try {
                    date = timeFormatter(timestamp);
                }
                catch (NumberFormatException n)
                {
                    date = "Feb 26 10 am";
                }

                if(sp.getBoolean(id,false)) {
                    //it = new EventItem(name, date, id);
                    //it.setType(type);
                    //it.setIconImgUrl(img2);
                    items.add(it);
                    tag.add(id);
                    ada.notifyDataSetChanged();
                }

            }


        }
        catch (JSONException e) {
            //catch exception
            e.printStackTrace();
        }


        lvi.setAdapter(ada);

        ((ProgressBar)root.findViewById(R.id.favload)).setVisibility(View.GONE);
        if(items.isEmpty()){

            ((TextView)root.findViewById(R.id.textView13)).setVisibility(View.VISIBLE);


        }else((TextView)root.findViewById(R.id.textView13)).setVisibility(View.GONE);



        lvi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(items.get(position).type){
                    case "w":
                        cat= "Workshop";
                        break;
                    case "gl":
                        cat="Guest Lectures";
                        break;
                    case "te":
                        cat="Technical Events";
                        break;
                    case "nte":
                        cat="Non Technical Events";
                        break;
                    case "ps":
                        cat="Pro Shows";
                        break;
                }
                Intent i1= new Intent(getContext(),EventDetailsActivity.class);
                i1.putExtra("category",cat);
                i1.putExtra("tag",tag.get(position).toString());
                startActivity(i1);
            }
        });



    }
*/

}
