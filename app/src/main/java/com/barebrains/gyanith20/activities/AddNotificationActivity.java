package com.barebrains.gyanith20.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.models.NotificationItem;
import com.barebrains.gyanith20.statics.VolleyManager;
import com.google.firebase.database.FirebaseDatabase;

import java.security.InvalidParameterException;

public class AddNotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notification);

        View btn = findViewById(R.id.sendNotiBtn);
        final TextView ttl = findViewById(R.id.noti_ttl);
        final TextView bdy = findViewById(R.id.noti_bdy);

        View backBtn = findViewById(R.id.notBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final NotificationItem notiItem = new NotificationItem(ttl.getText().toString()
                            , System.currentTimeMillis(), bdy.getText().toString());

/*
                    String url = Uri.parse("https://us-central1-gyanith19-9fdcb.cloudfunctions.net/notify?ttl=key1&bdy=key2")
                            .buildUpon()
                            .appendQueryParameter("key1", notiItem.title)
                            .appendQueryParameter("key2",notiItem.body)
                            .build().toString();

 */
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("https")
                            .authority("us-central1-gyanith19-9fdcb.cloudfunctions.net")
                            .appendPath("notify")
                            .appendQueryParameter("ttl", notiItem.title)
                            .appendQueryParameter("bdy", notiItem.body);
                    String url = builder.build().toString();

                    StringRequest notiRequest = new StringRequest(url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            FirebaseDatabase.getInstance().getReference().child("Notifications")
                                    .push().setValue(notiItem);

                            Toast.makeText(getApplicationContext(), "Notification Sent", Toast.LENGTH_SHORT).show();

                            AddNotificationActivity.this.finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("asd","AddNotification Error : " + error.getMessage());

                            Toast.makeText(getApplicationContext(), "Error : Notification not sent", Toast.LENGTH_SHORT).show();
                        }
                    });

                    VolleyManager.requestQueue.add(notiRequest);
                    finish();

                }catch (InvalidParameterException e){
                    Toast.makeText(AddNotificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}