package com.barebrains.gyanith20.adapters;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.fragments.botSheet;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.models.NotificationItem;
import com.barebrains.gyanith20.statics.Configs;
import com.barebrains.gyanith20.statics.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import static com.barebrains.gyanith20.gyanith20.appContext;
import static com.barebrains.gyanith20.others.PostViewHolder.DELETE_RESPONSE_DELAY;

public class notiViewHolder extends LiveViewHolder<NotificationItem>{

    private TextView sender;
    private TextView time;
    private TextView text;

    public notiViewHolder(@NonNull View itemView, Activity activity) {
        super(itemView, activity);
        sender = itemView.findViewById(R.id.notificationSender);
        time = itemView.findViewById(R.id.notificationTime);
        text = itemView.findViewById(R.id.notificationText);
    }

    @Override
    public void bindView(final NotificationItem data) {
        sender.setText(data.title);
        time.setText(Util.BuildScheduleDateString(data.time));
        text.setText(data.body);
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Configs.isValidAdmin())
                {   String content = text.getText().toString();
                    final botSheet prompt = botSheet.makeBotSheet(activity.getSupportFragmentManager());
                    prompt.setTitle("Delete Notification")
                            .setBody("Are u sure ? [" + content.substring(0,(content.length() < 40)?content.length():40) + "...]")
                            .setAction("DELETE")
                            .setActionListener(new CompletionListener(){
                                @Override
                                public void OnComplete() {
                                    FirebaseDatabase.getInstance().getReference().child("Notifications")
                                            .child(data.key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull final Task<Void> task) {
                                            prompt.dismiss();

                                            final botSheet deletingPrompt = botSheet.makeBotSheet(activity.getSupportFragmentManager())
                                                    .setTitle("Delete Notification")
                                                    .setBody("Deleting Notification ...")
                                                    .show();

                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    deletingPrompt.dismiss();
                                                    if (task.isSuccessful())
                                                        Toast.makeText(appContext, "Notification Deleted !", Toast.LENGTH_SHORT).show();
                                                    else
                                                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            },DELETE_RESPONSE_DELAY);
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
}
