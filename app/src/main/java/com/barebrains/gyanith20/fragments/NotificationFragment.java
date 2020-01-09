package com.barebrains.gyanith20.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.barebrains.gyanith20.activities.AddNotificationActivity;
import com.barebrains.gyanith20.activities.MainActivity;
import com.barebrains.gyanith20.adapters.notificationAdapter;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.NotificationItem;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.others.mFragment;
import com.barebrains.gyanith20.statics.AppNotiManager;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class NotificationFragment extends mFragment {
    //SINGLETON
    private static NotificationFragment instance;

    public static NotificationFragment getInstance(){
        if (instance == null)
            instance = new NotificationFragment();
        return instance;
    }


    public NotificationFragment() {
        // Required empty public constructor
        markBadges(3);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d("asd","start");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("asd","stop");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root= inflater.inflate(R.layout.fragment_notifications, container, false);
        ListView notiListView = root.findViewById(R.id.notificationListView);

        final View emptyState = root.findViewById(R.id.textView2);


        emptyState.setVisibility(View.GONE);

        root.findViewById(R.id.notload).setVisibility(View.VISIBLE);

        final notificationAdapter madapter = new notificationAdapter(getContext(), new ArrayList<NotificationItem>(), R.layout.item_notification);
        notiListView.setAdapter(madapter);

        AppNotiManager.addNotificationListener(789,new ResultListener<NotificationItem[]>(){
            @Override
            public void OnResult(NotificationItem[] notificationItems) {
                if (notificationItems.length == 0)
                    emptyState.setVisibility(View.VISIBLE);
                else
                    emptyState.setVisibility(View.GONE);

                madapter.clear();
                for (NotificationItem item : notificationItems){
                    madapter.add(item);
                }
                madapter.notifyDataSetChanged();
                root.findViewById(R.id.notload).setVisibility(View.GONE);
            }

            @Override
            public void OnError(String error) {
                root.findViewById(R.id.notload).setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(),error, Toast.LENGTH_SHORT).show();
            }
        });


        View addBtn = root.findViewById(R.id.add_notification);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddNotificationActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppNotiManager.removeNotificationListener(789);
    }
}
