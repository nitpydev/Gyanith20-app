package com.barebrains.gyanith20.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.barebrains.gyanith20.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Instruction extends AppCompatActivity {
        Button btn, clg_fev;
        Context context;
        AlertDialog al;
        AlertDialog.Builder bu ;
        Intent in;
        char choosen;
        int i;
        DatabaseReference ins;
        String url , mem, eve_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        load_instruct();
        btn = findViewById(R.id.ins_btn);
        clg_fev = findViewById(R.id.clf_fever);
        context = this;
        bu = new AlertDialog.Builder(context, R.style.Dialogue);
        in =  getIntent();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogue_layout(context);

            }
        });

    }
    private  void Register(String id,char ptps)
    {
        url = "http://gyanith.org/register.php?id="+id+"&ptps="+ptps;
        Web.WebFactory.with(Instruction.this).title("Register").load(url).start();
    }
    private void Dialogue_layout(final Context cnt)
    {
         mem = in.getStringExtra("EXTRAS_PTPS");
         eve_id = in.getStringExtra("EXTRAS_ID");
        LinearLayout lin = new LinearLayout(cnt);
        lin.setOrientation(LinearLayout.VERTICAL);
        Button[] btn = new Button[5];
        if(mem != null)
        for( i = 0; i < mem.length(); i++ )
        {
            choosen = mem.charAt(i);
            btn[i] = new Button(cnt);
            btn[i].setText("Register For "+ mem.charAt(i));
            btn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Register(eve_id,choosen);

                }
            });
            lin.addView(btn[i]);
        }
        bu.setTitle("REGISTER");
        bu.setView(lin);
        al = bu.create();
        al.show();
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

