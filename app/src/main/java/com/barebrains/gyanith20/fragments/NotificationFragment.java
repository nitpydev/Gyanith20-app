package com.barebrains.gyanith20.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.activities.AddNotificationActivity;
import com.barebrains.gyanith20.adapters.LiveListAdapter;
import com.barebrains.gyanith20.adapters.notiViewHolder;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.models.NotificationItem;
import com.barebrains.gyanith20.others.mFragment;
import com.barebrains.gyanith20.statics.Configs;
import com.barebrains.gyanith20.statics.DataRepository;

public class NotificationFragment extends mFragment {


    public NotificationFragment() {
        markBadges(3);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root =  inflater.inflate(R.layout.fragment_notifications, container, false);
        RecyclerView notiRecyclerView = root.findViewById(R.id.notificationListView);
        Loader loader = root.findViewById(R.id.noti_loader);
        LiveListAdapter<NotificationItem, notiViewHolder> adapter = new LiveListAdapter<NotificationItem,notiViewHolder>(getContext(), getViewLifecycleOwner(), R.layout.item_notification,loader) {
            @NonNull
            @Override
            public LiveData<ArrayResource<NotificationItem>> getLiveData() {
                return DataRepository.getAllNotiItems();
            }

            @NonNull
            @Override
            public notiViewHolder createViewHolder(View ItemView) {
                return new notiViewHolder(ItemView,getActivity());
            }

        };
        notiRecyclerView.setAdapter(adapter);
        View addBtn = root.findViewById(R.id.add_notification);

        if (Configs.isValidAdmin()) {
            addBtn.setVisibility(View.VISIBLE);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), AddNotificationActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            addBtn.setVisibility(View.GONE);
        }


        super.onCreateView(inflater, container, savedInstanceState);
        return root;
    }
}