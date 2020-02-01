package com.barebrains.gyanith20.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.barebrains.gyanith20.R;

public class Instruction extends AppCompatActivity {
        Button btn;
        Context context;
        AlertDialog al;
        AlertDialog.Builder bu ;
        Intent in;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        btn = findViewById(R.id.ins_btn);
        context = this;
        bu = new AlertDialog.Builder(context);
        in =  getIntent();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogue_layout(context);
            }
        });

    }
    private void Dialogue_layout(Context cnt)
    {
        String mem = in.getStringExtra("EXTRAS_PTPS");
        LinearLayout lin = new LinearLayout(cnt);
        lin.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        lin.setOrientation(LinearLayout.VERTICAL);
        Button[] btn = new Button[5];
        if(mem != null)
        for(int i = 0; i < mem.length(); i++ )
        {
            btn[i] = new Button(cnt);
            btn[i].setText("Register For "+ mem.charAt(i));
            btn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // so things
                }
            });
            lin.addView(btn[i]);
        }
        bu.setTitle("REGISTER");
        bu.setView(lin);
        al = bu.create();
        al.show();
    }
}

