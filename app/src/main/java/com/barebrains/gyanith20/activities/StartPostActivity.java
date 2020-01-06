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
import android.util.Log;
import android.widget.Toast;

import com.barebrains.gyanith20.statics.Util;

public class StartPostActivity extends AppCompatActivity {
    private static final int UPLOAD_POST_COMPLETED = 18;
    private static final int PERMISSIONS_REQUEST = 25;
    private static final int IMAGE_GALLERY_CODE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    return;
                }
            }
        }
        else {
            Toast.makeText(this,"Cannot Post Without Permission",Toast.LENGTH_LONG).show();
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
                String[] imgPaths;
                ClipData clipData = data.getClipData();
                if (clipData == null)//USER SELECTS SINGLE IMAGE
                {
                    imgPaths = new String[1];
                    imgPaths[0] = Util.UriAbsPath(this,data.getData());
                }
                else//USER SELECTS MULTIPLE IMAGES
                {
                    imgPaths = new String[clipData.getItemCount()];
                    for (int i = 0;i< imgPaths.length;i++)
                        imgPaths[i] = Util.UriAbsPath(this,clipData.getItemAt(i).getUri());
                }
                Intent intent = new Intent(this, UploadPostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArray("EXTRA_IMG_PATHS",imgPaths);
                Log.d("asd", "img path : " + imgPaths[0]);
                intent.putExtras(bundle);
                startActivityForResult(intent,UPLOAD_POST_COMPLETED);
                return;
            case UPLOAD_POST_COMPLETED:
                finish();
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

}
