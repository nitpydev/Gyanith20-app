package com.barebrains.gyanith20.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.activities.EventDetailsActivity;
import com.barebrains.gyanith20.fragments.ScheduleFragment;
import com.barebrains.gyanith20.fragments.botSheet;
import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.models.ScheduleItem;
import com.barebrains.gyanith20.statics.Configs;
import com.barebrains.gyanith20.statics.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import static com.barebrains.gyanith20.gyanith20.appContext;
import static com.barebrains.gyanith20.others.PostViewHolder.DELETE_RESPONSE_DELAY;

public class scheduleViewHolder extends LiveViewHolder<ScheduleItem>{

    private TextView time;
    private TextView title;
    private TextView venue;
    private View live;
    private View btn;

    public scheduleViewHolder(@NonNull View itemView, Activity activity) {
        super(itemView, activity);
        time = itemView.findViewById(R.id.time);
        title = itemView.findViewById(R.id.title);
        venue = itemView.findViewById(R.id.venue);
        live = itemView.findViewById(R.id.liveindicator);
        btn = itemView.findViewById(R.id.btn);
    }

    @Override
    public void bindView(final ScheduleItem data) {
        time.setText(formatTime(data.start_time));
        title.setText(data.title);
        venue.setText(data.venue);

        if (data.isLive())
            live.setVisibility(View.VISIBLE);
        else
            live.setVisibility(View.INVISIBLE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.id != null){
                    Intent intent = new Intent(itemView.getContext(), EventDetailsActivity.class);
                    intent.putExtra("EXTRA_ID",data.id);
                    itemView.getContext().startActivity(intent);
                }
            }
        });

        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Configs.isValidAdmin()) {
                    String content = data.title;
                    final botSheet prompt = botSheet.makeBotSheet(activity.getSupportFragmentManager());
                    prompt.setTitle("Delete Schedule")
                            .setBody("Are u sure ? [" + content.substring(0, (content.length() < 40) ? content.length() : 40) + "...]")
                            .setAction("DELETE")
                            .setActionListener(new CompletionListener() {
                                @Override
                                public void OnComplete() {
                                    FirebaseDatabase.getInstance().getReference().child("Schedule")
                                            .child(data.key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull final Task<Void> task) {
                                            prompt.dismiss();

                                            final botSheet deletingPrompt = botSheet.makeBotSheet(activity.getSupportFragmentManager())
                                                    .setTitle("Delete Schedule")
                                                    .setBody("Deleting Schedule ...")
                                                    .show();

                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    deletingPrompt.dismiss();
                                                    if (task.isSuccessful())
                                                        Toast.makeText(appContext, "Schedule Deleted !", Toast.LENGTH_SHORT).show();
                                                    else
                                                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }, DELETE_RESPONSE_DELAY);
                                        }
                                    });


                                }
                            }).show();
                    return true;
                }
                return false;
            }
        });
    }

    private String formatTime(Long time){
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(time);
        String m=String.format("%02d",cl.get(Calendar.MINUTE));
        String h=String.format("%02d",cl.get(Calendar.HOUR_OF_DAY));

        return h +":"+m;

    }
}