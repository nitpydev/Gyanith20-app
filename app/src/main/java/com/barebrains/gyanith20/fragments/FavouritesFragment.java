package com.barebrains.gyanith20.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.barebrains.gyanith20.activities.EventDetailsActivity;
import com.barebrains.gyanith20.adapters.eventCategoriesAdapter;
import com.barebrains.gyanith20.models.eventitem;
import com.barebrains.gyanith20.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class FavouritesFragment extends Fragment {


    DatabaseReference ref;
    eventCategoriesAdapter ada;
    ArrayList<eventitem> items;
    eventitem it;
    ListView lvi;
    SharedPreferences sp, json_string;
    ArrayList tag;
    String cat = "cat", PREFS = "shared_prefs", PREF_KEY = "JSON_CACHE";
    String cache, name, date, id, timestamp;

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
     if(hidden)
     {

     }
     else{

         final View root = getView();
        update(root);


     }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_favourites, container, false);

        update(root);
        return root;

    }

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

        sp = getContext().getSharedPreferences("com.barebrains.Gyanith19", Context.MODE_PRIVATE);
        json_string = getContext().getSharedPreferences(PREFS,Context.MODE_PRIVATE);
        items=new ArrayList<eventitem>();

        final Intent i1 =new Intent(this.getContext(), EventDetailsActivity.class);
        ada=new eventCategoriesAdapter(R.layout.item_event_category,items,this.getContext());
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

                try {
                    date = timeFormatter(timestamp);
                }
                catch (NumberFormatException n)
                {
                    date = null;
                }

                if(sp.getBoolean(id,false)) {
                    it = new eventitem(name, date, id);
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
                switch(tag.get(position).toString().charAt(0)){
                    case 'W':
                        cat= "Workshop";
                        break;
                    case 'G':
                        cat="Guest Lectures";
                        break;
                    case 'T':
                        cat="Technical Events";
                        break;
                    case 'N':
                        cat="Non Technical Events";
                        break;
                    case 'P':
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


}
