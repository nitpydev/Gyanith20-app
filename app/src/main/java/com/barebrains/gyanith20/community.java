package com.barebrains.gyanith20;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.app.Activity.RESULT_OK;

public class community extends Fragment {
    private static final int IMAGE_GALLERY_CODE = 12;
    private static final int UPLOAD_POST_COMPLETED = 18;

    public community() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_community,container,false);

        ((FloatingActionButton)root.findViewById(R.id.add_post_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_GALLERY_CODE);
            }
        });

        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case IMAGE_GALLERY_CODE:
                Uri uri = data.getData();
                Intent intent = new Intent(getActivity(),UploadPostActivity.class);

                intent.putExtra("EXTRA_IMG_URI", uri.toString());
                startActivityForResult(intent,UPLOAD_POST_COMPLETED);
                return;
        }

    }
}
