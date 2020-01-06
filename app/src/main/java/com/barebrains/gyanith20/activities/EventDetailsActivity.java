package com.barebrains.gyanith20.activities;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.models.EventItem;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

public class EventDetailsActivity extends AppCompatActivity {

    EventItem eventItem;

    TextView title,desc;
    ImageView eveimage;
    ToggleButton favtb;
    DatabaseReference reg;
    Intent intent;
    String catType, eventId;
    TabLayout dtab;
    SharedPreferences sp;
    Button backBtn;
    String tab1,tab2,tab3;
    Context context;

    String id="", tm, cost;


    AlertDialog.Builder a;
    AlertDialog vi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(R.layout.activity_event_details);
        a=new AlertDialog.Builder(this);


        sp= this.getSharedPreferences(getString(R.string.package_name),MODE_PRIVATE);

        backBtn =findViewById(R.id.backbut2);
        intent = getIntent();
        String eiJSON = intent.getStringExtra("eventItem");
        eventItem = (new Gson()).fromJson(eiJSON,EventItem.class);
        catType = eventItem.type;
        eventId = eventItem.id;
        title=findViewById(R.id.evedttitle);
        desc=findViewById(R.id.evedesc);
        dtab=findViewById(R.id.dtab);
        context =this;
         cost = eventItem.cost;


        if(eventItem.max_ptps == null){tm = "1";}else{tm = eventItem.max_ptps;}

        eveimage= findViewById(R.id.eveimv);
        favtb=findViewById(R.id.favButton);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Glide.with(this)
                .load(eventItem.img1)
                .placeholder(R.drawable.l2)
                .error(R.drawable.gyanith_error)
                .into(eveimage);

        if(eventItem.id.equals("w"))
        {
            dtab.getTabAt(1).setText("Requisites");
        }

        title.setText(eventItem.name);

        if(eventItem.des == null){tab1 = "";}else{tab1 = eventItem.des;}
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            desc.setText(Html.fromHtml(tab1,Html.FROM_HTML_MODE_LEGACY));
        else
            desc.setText(Html.fromHtml(tab1));

        if(cost != null)
            desc.append("\nRegistration Cost : Rs." + cost + " per person");

        tab2 = eventItem.rules;
        tab3 = eventItem.contact;


        final ImageView f= findViewById(R.id.fh);

        Set<String> favIds = sp.getStringSet(getString(R.string.favSet), new HashSet<String>());

        favtb.setChecked(favIds.contains(eventId));


        favtb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Set<String> favIds = sp.getStringSet(getString(R.string.favSet), new HashSet<String>());
                    favIds.add(eventId);
                    sp.edit().putStringSet(getString(R.string.favSet), favIds).apply();
                }
                else {
                    Set<String> favIds = sp.getStringSet(getString(R.string.favSet), new HashSet<String>());
                    favIds.remove(eventId);
                    sp.edit().putStringSet(getString(R.string.favSet), favIds).apply();
                }

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
                    if(cost != null)
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
        if(eventId.charAt(0)=='N'|| eventId.charAt(0)=='P'|| eventId.charAt(0)=='G')
            findViewById(R.id.reg).setVisibility(View.GONE);
        if(eventId.equals("P1"))
            findViewById(R.id.reg).setVisibility(View.GONE);



                Button[] b = new Button[2];
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
                            intent.putExtra("id", eventId);
                            intent.putExtra("token", "");
                            if (eventId.equals("W7"))
                                intent.putExtra("ex", eventId);
                            else
                                intent.putExtra("ex", "");
                            startActivity(intent);
                        }
                    });
                }

                a.setTitle("Register");
                a.setView(linlay);
                vi=a.create();



        findViewById(R.id.reg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vi.show();
            }
        });



    }
}
