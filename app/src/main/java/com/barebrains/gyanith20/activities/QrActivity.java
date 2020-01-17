package com.barebrains.gyanith20.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QrActivity extends AppCompatActivity {

    Loader loader;
    ImageView qrImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        //VIEW BINDINGS
        loader = findViewById(R.id.qr_act_loader);
        qrImg = findViewById(R.id.qr_img_act);


        (findViewById(R.id.qr_act_back_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loader.loading();

        GyanithUserManager.getCurrentUser().observe(this, new Observer<Resource<GyanithUser>>() {
            @Override
            public void onChanged(final Resource<GyanithUser> res) {
                if (res.value == null){
                    Intent intent = new Intent(QrActivity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    return;
                }

                NetworkManager.internet.observe(QrActivity.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean internet) {
                        if (!internet) {
                            loader.error();
                        } else {
                            showQR(res.value.gyanithId);
                        }
                    }
                });
            }
        });
    }

    private void showQR(String value){
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;

        QRGEncoder qrgEncoder = new QRGEncoder(value, null, QRGContents.Type.TEXT,smallerDimension);
        try {
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            qrImg.setImageBitmap(bitmap);
            loader.loaded();
        } catch (WriterException e) {
            loader.error();
        }
    }
}
