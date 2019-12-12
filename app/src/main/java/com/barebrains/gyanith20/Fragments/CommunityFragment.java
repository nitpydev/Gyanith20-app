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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.barebrains.gyanith20.Activities.UploadPostActivity;
import com.barebrains.gyanith20.Adapters.feedViewPagerAdapter;
import com.barebrains.gyanith20.Models.Post;
import com.barebrains.gyanith20.Others.PostViewHolder;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.Statics.Util;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.polyak.iconswitch.IconSwitch;

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
    private void NewPostBtn(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, IMAGE_GALLERY_CODE);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_community,container,false);
        ((FloatingActionButton)root.findViewById(R.id.add_post_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewPostBtn();
            }
        });

        final ViewPager viewPager = root.findViewById(R.id.feedViewPager);

        final IconSwitch iconSwitch = (IconSwitch)root.findViewById(R.id.trendingSwitch);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    iconSwitch.setChecked(IconSwitch.Checked.LEFT);
                else
                    iconSwitch.setChecked(IconSwitch.Checked.RIGHT);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        iconSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {

                if (current == IconSwitch.Checked.RIGHT)
                    viewPager.setCurrentItem(1);
                else
                    viewPager.setCurrentItem(0);
            }
        });


        viewPager.setAdapter(new feedViewPagerAdapter(getContext(),this,(ProgressBar)root.findViewById(R.id.progressBar)));
        viewPager.setOffscreenPageLimit(1);

        return root;
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
        NewPostBtn();
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
    @Override
    public void onDestroy() {
      /*  if (hotFeedAdapter != null)
        hotFeedAdapter.removeListeners();
        if (trendingFeedAdapter != null)
        trendingFeedAdapter.removeListeners();

       */
        super.onDestroy();
    }
}
