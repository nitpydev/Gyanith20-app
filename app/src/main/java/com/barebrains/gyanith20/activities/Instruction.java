package com.barebrains.gyanith20.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.fragments.CommunityFragment;
import com.barebrains.gyanith20.fragments.botSheet;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Instruction extends AppCompatActivity {

    public final static String EXTRA_EVENT_ID = "EXTRA_EVENT_ID";
    public final static String EXTRA_MAX_PTPS = "EXTRA_MAX_PTPS";

        Button web_reg_btn, clg_fev;

        AlertDialog.Builder bu ;
        Intent in;
        char choosen;
        int i;
        DatabaseReference ins;
        String url , mem, eve_id;

        private Integer max_ptps;
        private String eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        load_instruct();
        web_reg_btn = findViewById(R.id.web_reg_btn);
        clg_fev = findViewById(R.id.clf_fever);

        extractData();

        web_reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GyanithUserManager.getCurrentUser().observe(Instruction.this, new Observer<Resource<GyanithUser>>() {
                    @Override
                    public void onChanged(Resource<GyanithUser> res) {
                        if (res.value != null){//USER SIGNED IN
                            if (!NetworkManager.internet_value) {
                                Toast.makeText(Instruction.this, "No Internet", Toast.LENGTH_SHORT).show();
                                return;
                            }

                           showDialog();
                        }
                        else {//NOT SIGNED USER
                            Toast.makeText(Instruction.this, "Sign in to Register", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Instruction.this,LoginActivity.class);
                            startActivity(intent);
                        }
                        //Removing observer as it is just a click
                        GyanithUserManager.getCurrentUser().removeObserver(this);
                    }


                });

            }
        });

    }

    private void showDialog(){
        final botSheet bs = botSheet.makeBotSheet(getSupportFragmentManager())
                .setTitle("Register For : ")
                .setContentRes(R.layout.bot_content_ptps_count);

        bs.setContentListener(new ResultListener<View>(){
                    @Override
                    public void OnResult(View content) {
                        BottomNavigationView navView = (BottomNavigationView)content;

                        for (int i = getIndex(max_ptps) + 1;i < 3;i++)
                            navView.getMenu().getItem(i).setVisible(false);

                        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                showWeb(item.getOrder());

                                bs.dismiss();
                                return true;
                            }
                        });
                    }
                }).show();
    }

    private void showWeb(Integer index){
        url = "http://gyanith.org/register.php?id="+eventId+"&ptps="+ getPtps(index);

        Web.WebFactory.with(this)
                .title("Register")
                .load(url)
                .start();
    }

    private void extractData(){
        Intent intent = getIntent();
        String mp = intent.getStringExtra(EXTRA_MAX_PTPS);
        max_ptps = (mp != null)?Integer.parseInt(mp):5;
        eventId = intent.getStringExtra(EXTRA_EVENT_ID);
    }

    private String getPtps(Integer i){
       return ((Integer)(2*i + 1)).toString();
    }

    private int getIndex(int i){
       return (i-1)/2;
    }

    private void load_instruct()
    {
        final TextView instruct = findViewById(R.id.instruct);
        ins = FirebaseDatabase.getInstance().getReference().child("instructions");
        ins.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String count ;
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    int i = 1;
                    count = Integer.toString(i);
                    instruct.append("\n"+count + ". " +ds.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

