package com.barebrains.gyanith20.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.barebrains.gyanith20.adapters.eventCategoriesAdapter;
import com.barebrains.gyanith20.models.eventitem;
import com.barebrains.gyanith20.R;

import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;


public class EventCategoriesActivity extends AppCompatActivity {

    String s,name,date,eventtag, type, cat;
    String url_event = "http://gyanith.org/api.php?action=fetchAll&key=2ppagy0";
    eventCategoriesAdapter ada;
    ArrayList<eventitem> items;


    eventitem it;
    ListView lvi;
    ArrayList tag;

    SharedPreferences prefs;
    String PREF_KEY = "json_string", PREFS = "shared_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }


        items=new ArrayList<eventitem>();
        setContentView(R.layout.activity_event_categories);
        lvi=findViewById(R.id.eveitlv);
        final Intent i1=new Intent(this,EventDetailsActivity.class);
        /*if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        }*/



        ((Button)findViewById(R.id.backbut)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent i=getIntent();
        ((TextView)findViewById(R.id.cattitle)).setText(i.getStringExtra("category"));
        s=i.getStringExtra("category");

        switch(s)
        {
            case "Workshop":
                cat = "w";
                break;
            case "Technical Events":
                cat = "te";
                break;
            case "Non Technical Events":
                cat = "nte";
                break;
            case "Pro Shows":
                cat = "ps";
            case "Guest Lectures":
                cat = "gl";
                break;
             default:
                 cat = "not found";
                 break;
        }

        ada=new eventCategoriesAdapter(R.layout.item_event_category,items,this);
        tag = new ArrayList();

        //Json request

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url_event, new Response.Listener<JSONArray>()
        {
                    @Override
                    public void onResponse(JSONArray jsonArray) {



                            for(int i = 0; i < jsonArray.length(); i++) {
                                try
                                {
                                JSONObject jsonobject = jsonArray.getJSONObject(i);
                                type = jsonobject.getString("type");
                                    name = jsonobject.getString("name");

                                    date = jsonobject.getString("timestamp");
                                    eventtag = jsonobject.getString("id");



                                    it = new eventitem(name, timeFormatter(date), eventtag);
                                    it.setType(type);

                                if(type.equals(cat)) {


                                    items.add(it);

                                    tag.add(eventtag);
                                }
                                }
                                catch (JSONException e) {
                                    //catch exception
                                    e.printStackTrace();
                                }

                            }

                        // caches the
                        if(!items.isEmpty()){

                        String cache_json = jsonArray.toString();
                        prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(PREF_KEY,cache_json);
                        editor.apply();}

                            if(items.isEmpty())
                                ((TextView)findViewById(R.id.textView14)).setVisibility(View.VISIBLE);
                            else
                                ((TextView)findViewById(R.id.textView14)).setVisibility(View.GONE);
                                ((ProgressBar)findViewById(R.id.catload)).setVisibility(View.GONE);
                            ada.notifyDataSetChanged();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        SharedPreferences prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                        String string = prefs.getString(PREF_KEY, "no input");
                        try {
                            JSONArray jsonArray = new JSONArray(string);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                name = jsonObject.getString("name");

                                date = jsonObject.getString("timestamp");
                                eventtag = jsonObject.getString("id");
                                type = jsonObject.getString("type");

                                it = new eventitem(name, date, eventtag);
                                if(type.equals(cat)){
                                items.add(it);
                                tag.add(eventtag);
                                }

                            }

                            if(items.isEmpty())
                                ((TextView)findViewById(R.id.textView14)).setVisibility(View.VISIBLE);
                            else
                                ((TextView)findViewById(R.id.textView14)).setVisibility(View.GONE);
                            ((ProgressBar)findViewById(R.id.catload)).setVisibility(View.GONE);
                            ada.notifyDataSetChanged();
                        }
                        catch(JSONException e)
                        {
                            Toast.makeText(EventCategoriesActivity.this,"network unavailable",Toast.LENGTH_LONG).show();
                        }






                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);


        lvi.setAdapter(ada);

        lvi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                i1.putExtra("category",s);
                i1.putExtra("tag",tag.get(position).toString());
                startActivity(i1);
            }
        });



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


}
