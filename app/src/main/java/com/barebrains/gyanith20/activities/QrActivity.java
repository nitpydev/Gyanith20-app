package com.barebrains.gyanith20.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.NetworkStateListener;
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

        final String value = getIntent().getStringExtra("Value");

        if (value == null)
        {
            finish();
            return;
        }

        loader = findViewById(R.id.qr_act_loader);
        qrImg = findViewById(R.id.qr_img_act);
        loader.loading();
        NetworkManager.getInstance().addListener(-5,new NetworkStateListener(){
            @Override
            public void OnAvailable() {
                QrActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showQR(value);
                        loader.loaded();
                    }
                });

            }

            @Override
            public void OnDisconnected() {
                QrActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QrActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                        loader.error();
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
        } catch (WriterException e) {
            loader.error();
        }
    }
}
