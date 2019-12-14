package com.barebrains.gyanith20.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EventCategoriesActivity extends AppCompatActivity {

    String s,name,date,eventtag;
    String url_event = "http://gyanith.org/api.php?type=w&action=fetch&key=2ppagy0";
    String jsonobject;
    eventCategoriesAdapter ada;
    ArrayList<eventitem> items;
    eventitem it;
    ListView lvi;
    ArrayList tag;

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
                                name = jsonobject.getString("name");

                                date = jsonobject.getString("date"); // json file didnt have date attribute,and will be added soon
                                eventtag = jsonobject.getString("id");

                                it=new eventitem(name,timeFormatter(date),eventtag);
                                items.add(it);
                                tag.add(eventtag);
                                }
                                catch (JSONException e) {
                                    //catch exeption
                                    e.printStackTrace();
                                }

                            }

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
                       Toast.makeText(EventCategoriesActivity.this, volleyError.getMessage(),Toast.LENGTH_LONG).show();
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
