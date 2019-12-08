package com.barebrains.gyanith20.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.barebrains.gyanith20.Adapters.postImagesAdaptor;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.Services.PostUploadService;
import com.barebrains.gyanith20.Statics.Util;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

public class UploadPostActivity extends AppCompatActivity {

    Bitmap[] imgBitmaps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);
        Bundle bundle = getIntent().getExtras();
        final String[] imgPaths = bundle.getStringArray("EXTRA_IMG_PATHS");

        if (imgPaths != null)
            imgBitmaps = Util.getImgBitmaps(imgPaths);
        else
            Log.d("asd","img path null");

        ViewPager viewPager = findViewById(R.id.uploadpost_viewpager);
        viewPager.setAdapter(new postImagesAdaptor(this,imgBitmaps));
        viewPager.setOffscreenPageLimit(imgBitmaps.length - 1);

        SpringDotsIndicator indicator = findViewById(R.id.uploadpost_dots);
        indicator.setViewPager(viewPager);

        findViewById(R.id.post_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadPost(imgPaths);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void UploadPost(String[] imgPaths){
        Intent intent = new Intent(this, PostUploadService.class);
        Bundle bundle  = new Bundle();
        bundle.putStringArray("EXTRA_IMG_PATHS",imgPaths);
        intent.putExtras(bundle);
        EditText captionsTxt = findViewById(R.id.post_captions);
        intent.putExtra("EXTRA_CAPTIONS",captionsTxt.getText().toString());
        startService(intent);
    }

}


