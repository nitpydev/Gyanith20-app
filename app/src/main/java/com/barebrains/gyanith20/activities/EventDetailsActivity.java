package com.barebrains.gyanith20.activities;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.barebrains.gyanith20.R;

import com.barebrains.gyanith20.others.ImageVolley;
import com.barebrains.gyanith20.statics.Util;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class EventDetailsActivity extends AppCompatActivity {

    TextView title,desc;
    NetworkImageView eveimage;
    ImageLoader imageeve;
    ToggleButton favtb;
    DatabaseReference reg;
    Intent intent;
    String child,tag;
    TabLayout dtab;
    SharedPreferences sp, cache;
    Button bb2;
    String tab1,tab2,tab3;
    Context context;

    String id="", PREFS = "shared_prefs", PREF_KEY = "JSON_CACHE", tag_id, img1,cost;

    AlertDialog.Builder a;
    AlertDialog vi;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
       // getWindow().setEnterTransition(new Explode());
       // getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_event_details);
        a=new AlertDialog.Builder(this);


        sp= this.getSharedPreferences("com.barebrains.Gyanith19",MODE_PRIVATE);

        bb2=findViewById(R.id.backbut2);
        intent = getIntent();
        child=intent.getStringExtra("category");
        tag= intent.getStringExtra("tag");
        title=findViewById(R.id.evedttitle);
        desc=findViewById(R.id.evedesc);
        dtab=findViewById(R.id.dtab);
        context =this;

        eveimage=(NetworkImageView) findViewById(R.id.eveimv);
        favtb=findViewById(R.id.favButton);



        if(child.equals("Workshop"))
        {
            dtab.getTabAt(1).setText("Requisites");
        }




        bb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cache = getSharedPreferences(PREFS,MODE_PRIVATE);
        String CACHE = cache.getString(PREF_KEY,"NO INPUT");


        try {
            JSONArray jsonArray = new JSONArray(CACHE);

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonobject = jsonArray.getJSONObject(i);

                tag_id = jsonobject.getString("id");

                if(tag_id.equals(tag)) {
                    String name = jsonobject.getString("name");

                    title.setText(name);

                    cost = jsonobject.getString("cost");

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {
                        tab1  = jsonobject.getString("des");
                        desc.setText(Html.fromHtml(tab1,Html.FROM_HTML_MODE_LEGACY));

                    }
                    else
                    {
                        desc.setText(Html.fromHtml(tab1));
                    }
                    if(cost != "null")
                        desc.append("\nRegistration Cost : Rs." + cost + " per person");

                    img1 = jsonobject.getString("img1");

                    tab2 = jsonobject.getString("rules");

                    tab3 = jsonobject.getString("contact");

                    break;
                }
            }


        }
        catch(JSONException J) {

            Toast.makeText(EventDetailsActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
        }




        imageeve = ImageVolley.getInstance(context).getImageLoader();
        imageeve.get(img1, new ImageLoader.ImageListener(){

                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        eveimage.setImageResource(R.drawable.l2);

                        Bitmap img = response.getBitmap();

                        if(img != null)
                        {
                            eveimage.setImageBitmap(img);

                            try {
                                File imgfile = new File(context.getCacheDir().getPath() ,"d"+tag_id);
                                Util.putBitmaptoFile(img, imgfile);
                            }catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }




                    }
                    @Override
                    public void onErrorResponse(VolleyError error) { }
                }
        );
        eveimage.setImageUrl(img1, imageeve);


        File imgfile = new File(context.getCacheDir().getPath(),"d"+tag_id);
        try{
            Bitmap btm = Util.decodeFile(imgfile);
            if(btm != null)
                eveimage.setImageBitmap(btm);
        }catch(Exception e)
        {
            e.printStackTrace();
        }




        final ImageView f=(ImageView)findViewById(R.id.fh) ;

        favtb.setChecked(sp.getBoolean(tag,false));
        favtb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(tag,favtb.isChecked()).commit();
                if(isChecked){
                    ObjectAnimator fa=ObjectAnimator.ofFloat(f,"alpha",1,0);
                    fa.setDuration(500);
                    fa.start();
                    ObjectAnimator fa1=ObjectAnimator.ofFloat(f,"scaleX",1,5);
                    fa1.setDuration(500);
                    fa1.start();
                    ObjectAnimator fa2=ObjectAnimator.ofFloat(f,"scaleY",1,5);
                    fa2.setDuration(500);
                    fa2.start();

                }
            }
        });



        dtab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int a=tab.getPosition();
                if(a==0){

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {
                        desc.setText(Html.fromHtml(tab1,Html.FROM_HTML_MODE_LEGACY));
                    }
                    else
                    {
                        desc.setText(Html.fromHtml(tab1));
                    }

                    if(cost != "null")
                    desc.append("\nRegistration Cost : Rs." + cost + " per person");
                }
                if(a==1){
                    desc.setText(tab2);
                }
                if(a==2){
                    desc.setText(tab3);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if(tag.charAt(0)=='N'||tag.charAt(0)=='P'||tag.charAt(0)=='G')
            ((Button)findViewById(R.id.reg)).setVisibility(View.GONE);
        if(tag.equals("P1"))
            ((Button)findViewById(R.id.reg)).setVisibility(View.GONE);




        reg = FirebaseDatabase.getInstance().getReference().child(child).child(tag);

        reg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                String tm="1";
                try {
                    tm = dataSnapshot.child("tm").getValue().toString();
                }catch(Exception e){}
                Button b[] = new Button[2];
                LinearLayout linlay = new LinearLayout(context);
                linlay.setOrientation(LinearLayout.VERTICAL);
                Log.i("alert",tm);
                for (int i = 0; i < tm.length(); i++) {
                    b[i] = new Button(context);
                    b[i].setBackgroundColor(Color.parseColor("#FFFFFF"));
                    b[i].setText("Register for " + tm.charAt(i));
                    linlay.addView(b[i]);
                    final int i1 = i;
                    b[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, RegisterActivity.class);
                            try {
                                id = dataSnapshot.child("id").getValue().toString();
                                id = id.substring(3 * i1, 3 * (i1 + 1));
                            }
                            catch(Exception e){}
                            intent.putExtra("id", id);
                            intent.putExtra("token", "");
                            if (tag.equals("W7"))
                                intent.putExtra("ex", tag);
                            else
                                intent.putExtra("ex", "");
                            startActivity(intent);
                        }
                    });
                }

                a.setTitle("Register");
                a.setView(linlay);
                vi=a.create();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });






        ((Button)findViewById(R.id.reg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vi.show();
            }
        });



    }
}
