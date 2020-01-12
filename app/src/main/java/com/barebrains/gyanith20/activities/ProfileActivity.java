package com.barebrains.gyanith20.activities;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.NetworkStateListener;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ProfileActivity extends AppCompatActivity {


    //VIEWS
    private Loader qrLoader;
    private ImageView qrImg;
    private View btn;
    private View signOutBtn;
    private TextView username;
    private TextView emailTop;
    private TextView email;
    private TextView phone;
    private TextView clg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        }

        GyanithUser user = GyanithUserManager.getCurrentUser();
        if(user == null) {
            GyanithUserManager.resolveUserState(getApplicationContext());
            finish();
            return;
        }
        setContentView(R.layout.activity_profile);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Binding Images
        qrLoader = findViewById(R.id.qr_loader);
        qrImg = findViewById(R.id.qr_img);
        btn = findViewById(R.id.regd_btn);
        username = findViewById(R.id.username);
        emailTop = findViewById(R.id.profile_top_email);
        email = findViewById(R.id.user_info_email);
        phone = findViewById(R.id.user_info_mobile);
        clg = findViewById(R.id.user_info_clg);
        signOutBtn = findViewById(R.id.signout_btn);

        qrLoader.loading();
        setUIData(user);
    }

    private void setUIData(final GyanithUser user){
        username.setText("@" + user.userName);
        email.setText(user.email);
        emailTop.setText(user.email);
        phone.setText(user.phoneNo);
        clg.setText(user.clg);

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GyanithUserManager.SignOutUser(ProfileActivity.this);
                finish();
            }
        });

        NetworkManager.getInstance().addListener(-5,new NetworkStateListener(){
            @Override
            public void OnAvailable() {
                Log.d("asd","avaiable");
                showQR(user.gyanithId);
                qrLoader.loaded();
            }

            @Override
            public void OnDisconnected() {
                Toast.makeText(ProfileActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                Log.d("asd","c 1");
                qrLoader.error();
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
            qrLoader.error();
        }
    }


}
