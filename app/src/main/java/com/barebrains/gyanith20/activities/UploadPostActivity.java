package com.barebrains.gyanith20.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.barebrains.gyanith20.adapters.postImagesAdaptor;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.ClickableViewPager;
import com.barebrains.gyanith20.services.PostUploadService;
import com.barebrains.gyanith20.statics.Anim;
import com.barebrains.gyanith20.statics.Util;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

public class UploadPostActivity extends AppCompatActivity {

    Bitmap[] imgBitmaps;

    ClickableViewPager viewPager;
    View tapPanel;
    EditText captionsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);

        tapPanel = findViewById(R.id.upd_post_tap_panel);
        viewPager = findViewById(R.id.uploadpost_viewpager);
        captionsText = findViewById(R.id.post_captions);
        findViewById(R.id.upload_post_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        tapPanel.setVisibility(View.GONE);

        viewPager.setOnViewPagerClickListener(new ClickableViewPager.OnClickListener() {
            @Override
            public void onViewPagerClick(ViewPager viewPager) {
                captionStateToggle();
            }
        });

        Bundle bundle = getIntent().getExtras();
        final String[] imgPaths = bundle.getStringArray("EXTRA_IMG_PATHS");

        if (imgPaths != null)
            imgBitmaps = Util.getImgBitmaps(imgPaths);
        else
            Log.d("asd","img path null");

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

    private void captionStateToggle(){
        if (tapPanel.getVisibility() == View.VISIBLE) {
            Anim.crossfade(tapPanel,viewPager,0f,350);
            Anim.zoom(tapPanel,1f, 0.7f,450);
        } else {
            Anim.crossfade(viewPager,tapPanel,0.3f,350);
            Anim.zoom(tapPanel,0.7f,1f,400);
            captionsText.requestFocus();
        }
    }

}


