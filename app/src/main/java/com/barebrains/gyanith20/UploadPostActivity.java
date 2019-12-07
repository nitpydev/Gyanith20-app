package com.barebrains.gyanith20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.StringPrepParseException;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import java.net.URI;
import java.sql.Timestamp;

public class UploadPostActivity extends AppCompatActivity {

    Bitmap[] imgBitmaps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);
        Bundle bundle = getIntent().getExtras();
        final String[] imgPaths = bundle.getStringArray("EXTRA_IMG_PATHS");

        if (imgPaths != null)
            imgBitmaps = getImgBitmaps(imgPaths);
        else
            Log.d("asd","img path null");

        ViewPager viewPager = findViewById(R.id.uploadpost_viewpager);
        viewPager.setAdapter(new ImageViewPagerAdaptor(this));
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
        Intent intent = new Intent(this,PostUploadService.class);
        Bundle bundle  = new Bundle();
        bundle.putStringArray("EXTRA_IMG_PATHS",imgPaths);
        intent.putExtras(bundle);
        EditText captionsTxt = findViewById(R.id.post_captions);
        intent.putExtra("EXTRA_CAPTIONS",captionsTxt.getText().toString());
        startService(intent);

    }
    class ImageViewPagerAdaptor extends PagerAdapter {

        LayoutInflater layoutInflater;

        public ImageViewPagerAdaptor(Context context){
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return imgBitmaps.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((LinearLayout) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View itemView = layoutInflater.inflate(R.layout.uploadpost_pager_item, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.pager_item_img);

            if (imgBitmaps[position] != null)
                imageView.setImageBitmap(imgBitmaps[position]);

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    private Bitmap[] getImgBitmaps(String[] imgPaths){
        Bitmap[] bitmaps = new Bitmap[imgPaths.length];
        for (int i =0;i<imgPaths.length;i++) {
            bitmaps[i] = BitmapFactory.decodeFile(imgPaths[i]);
            Log.d("asd",imgPaths[i]);
        }

        return bitmaps;
    }
}


