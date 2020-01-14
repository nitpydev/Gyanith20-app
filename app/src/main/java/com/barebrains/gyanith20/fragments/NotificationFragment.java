package com.barebrains.gyanith20.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.activities.AddNotificationActivity;
import com.barebrains.gyanith20.adapters.LiveListAdapter;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.models.NotificationItem;
import com.barebrains.gyanith20.others.mFragment;
import com.barebrains.gyanith20.statics.AppNotiManager;
import com.barebrains.gyanith20.statics.DataRepository;
import com.barebrains.gyanith20.statics.Util;

public class NotificationFragment extends mFragment {
    //SINGLETON
    private static NotificationFragment instance;

    public static NotificationFragment getInstance(){
        if (instance == null)
            instance = new NotificationFragment();
        return instance;
    }


    public NotificationFragment() {
        markBadges(3);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        ListView notiListView = root.findViewById(R.id.notificationListView);
        final Loader loader = root.findViewById(R.id.ad);
        loader.loading();

        LiveListAdapter<NotificationItem> adapter = new LiveListAdapter<NotificationItem>(getContext(), getViewLifecycleOwner(), R.layout.item_notification) {
            @NonNull
            @Override
            public LiveData<ArrayResource<NotificationItem>> getLiveData() {
                return DataRepository.getNotiItems();
            }

            @NonNull
            @Override
            public void bindView(View view, NotificationItem data) {
                ((TextView) view.findViewById(R.id.notificationSender)).setText(data.title);
                ((TextView) view.findViewById(R.id.notificationTime)).setText(Util.BuildDateString(data.time));
                ((TextView) view.findViewById(R.id.notificationText)).setText(data.body);
            }

            @NonNull
            @Override
            public View createView() {
                return inflater.inflate(getResId(), null);
            }
        };
        adapter.setLoader(loader);
        notiListView.setAdapter(adapter);
        adapter.observe();


        View addBtn = root.findViewById(R.id.add_notification);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddNotificationActivity.class);
                startActivity(intent);
            }
        });

        super.onCreateView(inflater, container, savedInstanceState);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppNotiManager.removeNotificationListener(789);
    }
}
