package com.barebrains.gyanith20.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import com.barebrains.gyanith20.statics.NetworkManager;
import com.barebrains.gyanith20.statics.Util;
import com.google.android.gms.common.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class StartPostActivity extends AppCompatActivity {


    private static final int MAX_IMAGES = 3;//CHANGE HERE FOR MAX LIMIT OF IMAGES


    private static final int UPLOAD_POST_COMPLETED = 18;
    private static final int PERMISSIONS_REQUEST = 25;
    private static final int IMAGE_GALLERY_CODE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!NetworkManager.getInstance().isNetAvailable()) {
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        startPosting();
    }

    private void startPosting(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSIONS_REQUEST)
            return;

        if (grantResults.length != 0) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Cannot Post Without Permission",Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
        }
        else {
            Toast.makeText(this,"Cannot Post Without Permission",Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        startPosting();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            finish();
            return;
        }

        switch (requestCode){
            case IMAGE_GALLERY_CODE:
                Intent intent = new Intent(this, UploadPostActivity.class);
                Bundle bundle = new Bundle();
                try {
                    bundle.putStringArray("EXTRA_IMG_PATHS",getImgPaths(data));
                } catch (IOException e) {
                    Toast.makeText(this, "Error Reading ImageFile", Toast.LENGTH_LONG).show();
                    finish();
                }
                intent.putExtras(bundle);
                startActivityForResult(intent,UPLOAD_POST_COMPLETED);
                return;
            case UPLOAD_POST_COMPLETED:
                Toast.makeText(this, "Uploading Post", Toast.LENGTH_SHORT).show();
                finish();
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private String[] getImgPaths(Intent data) throws IOException {
        String[] imgPaths;

        ClipData clipData = data.getClipData();
        if (clipData != null){//USER SELECTED MORE THAN ONE IMAGE
            int imgCount = (clipData.getItemCount() >= MAX_IMAGES)?MAX_IMAGES:clipData.getItemCount();

            imgPaths = new String[imgCount];

            for (int i = 0; i < imgCount;i++){
                ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(clipData.getItemAt(i).getUri(), "r", null);
                FileInputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                File file = new File(getCacheDir(),Util.generateUniqueId());

                FileOutputStream outputStream = new  FileOutputStream(file);
                FileUtils.copy(inputStream,outputStream);
                imgPaths[i] = file.getAbsolutePath();
            }
        }
        else //USER SELECTS ONLY ONE IMAGES
        {
            imgPaths = new String[1];
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(data.getData(), "r", null);
            FileInputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            File file = new File(getCacheDir(),Util.generateUniqueId());
            FileOutputStream outputStream = new  FileOutputStream(file);
            FileUtils.copy(inputStream,outputStream);
            imgPaths[0] = file.getAbsolutePath();
        }

        return imgPaths;
    }

}
