package com.barebrains.gyanith20.Fragments;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.barebrains.gyanith20.Activities.UploadPostActivity;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.Statics.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.app.Activity.RESULT_OK;

public class CommunityFragment extends Fragment {
    private static final int IMAGE_GALLERY_CODE = 12;
    private static final int UPLOAD_POST_COMPLETED = 18;
    private static final int PERMISSIONS_REQUEST = 25;

    public CommunityFragment() {
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
               NewPost();
            }
        });

        return root;
    }

    private void NewPost(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, IMAGE_GALLERY_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSIONS_REQUEST)
            return;
        if (grantResults.length != 0) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(),"Cannot Post Without Permission",Toast.LENGTH_LONG).show();
                    return;
                }
             }
        }
        else {
            Toast.makeText(getContext(),"Cannot Post Without Permission",Toast.LENGTH_LONG).show();
            return;
        }
        NewPost();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case IMAGE_GALLERY_CODE:
                String[] imgPaths;
                ClipData clipData = data.getClipData();
                if (clipData == null)//USER SELECTS SINGLE IMAGE
                {
                    imgPaths = new String[1];
                    imgPaths[0] = Util.UriAbsPath(getContext(),data.getData());
                }
                else//USER SELECTS MULTIPLE IMAGES
                {
                    imgPaths = new String[clipData.getItemCount()];
                    for (int i = 0;i< imgPaths.length;i++)
                        imgPaths[i] = Util.UriAbsPath(getContext(),clipData.getItemAt(i).getUri());
                }
                Intent intent = new Intent(getActivity(), UploadPostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArray("EXTRA_IMG_PATHS",imgPaths);
                Log.d("asd", "img path : " + imgPaths[0]);
                intent.putExtras(bundle);
                startActivityForResult(intent,UPLOAD_POST_COMPLETED);
                return;
            case UPLOAD_POST_COMPLETED:
                Log.d("gyanith20", "Post_Uploaded");
        }

    }
}
