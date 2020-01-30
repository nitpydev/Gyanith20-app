package com.barebrains.gyanith20.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.ScheduleItem;
import com.barebrains.gyanith20.statics.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddScheduleActivity extends AppCompatActivity {

    TextView ttl;
    TextView venue;
    TextView id;
    
    Long startTime;
    Long endTime;

    TextView st;
    TextView et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        ttl = findViewById(R.id.sch_ttl);
        venue = findViewById(R.id.sch_venue);
        id = findViewById(R.id.sch_id);

        st = findViewById(R.id.start_time);
        et = findViewById(R.id.end_time);


        findViewById(R.id.time_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPickedTime(new ResultListener<Long>(){
                    @Override
                    public void OnResult(Long aLong) {
                        startTime = aLong;
                        st.setText(Util.BuildScheduleDateString(startTime));
                    }
                });
            }
        });

        findViewById(R.id.time_end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPickedTime(new ResultListener<Long>(){
                    @Override
                    public void OnResult(Long aLong) {
                        endTime = aLong;
                    }
                });
                et.setText(Util.BuildScheduleDateString(endTime));
            }
        });


        findViewById(R.id.add_sch_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitScheduleToDB();
            }
        });
    }
    
    private void commitScheduleToDB(){
        if (ttl.getText() == "" || venue.getText() == "" || startTime == null || endTime == null ) {
            Toast.makeText(this, "FILL EVERY FIELD", Toast.LENGTH_SHORT).show();
            return;
        }

        ScheduleItem item = new ScheduleItem();
        item.id = id.getText().toString();
        if (item.id.equals(""))
            item.id = null;
        item.title = ttl.getText().toString();
        item.venue = venue.getText().toString();
        item.start_time = startTime;
        item.end_time = endTime;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Schedule");
        ref.push().setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddScheduleActivity.this, "Schedule Added", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                    Toast.makeText(AddScheduleActivity.this, "Error: Unknown", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPickedTime(final ResultListener<Long> listener){
        final View dialogView = View.inflate(this, R.layout.date_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        dialogView.findViewById(R.id.date_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
                alertDialog.dismiss();

                getTime(new ResultListener<TimePicker>(){
                    @Override
                    public void OnResult(TimePicker timePicker) {
                        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute());

                        listener.OnResult(calendar.getTimeInMillis());
                    }
                });

            }});
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    private void getTime(final ResultListener<TimePicker> listener){
        final View dialogView = View.inflate(this, R.layout.time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        dialogView.findViewById(R.id.time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePicker timePicker = dialogView.findViewById(R.id.time_picker);
                alertDialog.dismiss();

                listener.OnResult(timePicker);
            }});
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

}
