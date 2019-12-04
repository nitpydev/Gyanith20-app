package com.barebrains.gyanith20;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import java.net.URI;

public class UploadPostActivity extends AppCompatActivity {

    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);
        img = findViewById(R.id.upload_post_img);
        Uri uri = Uri.parse(getIntent().getStringExtra("EXTRA_IMG_URI"));
        img.setImageURI(uri);

    }
}
