package com.barebrains.gyanith20.activities;

import android.content.Intent;
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
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ProfileActivity extends AppCompatActivity {


    //VIEWS
    private Loader loader;
    private Loader qrLoader;
    private ImageView qrImg;
    private View profile2Btn;
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
        setContentView(R.layout.activity_profile);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Binding Images
        loader = findViewById(R.id.profile_loader);
        qrLoader = findViewById(R.id.qr_loader);
        qrImg = findViewById(R.id.qr_img);
        profile2Btn = findViewById(R.id.regd_btn);
        username = findViewById(R.id.username);
        emailTop = findViewById(R.id.profile_top_email);
        email = findViewById(R.id.user_info_email);
        phone = findViewById(R.id.user_info_mobile);
        clg = findViewById(R.id.user_info_clg);
        signOutBtn = findViewById(R.id.signout_btn);

        findViewById(R.id.profile_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        qrLoader.loading();
        loader.loading();

        GyanithUserManager.getCurrentUser().observe(this, new Observer<Resource<GyanithUser>>() {
            @Override
            public void onChanged(final Resource<GyanithUser> res) {
                if (res.value == null){
                    if (res.error.getMessage() != null)//TODO:SEND THIS MESSAGE
                        Toast.makeText(ProfileActivity.this, res.error.getMessage(), Toast.LENGTH_SHORT).show();                    Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    return;
                }
                loader.loaded();
                FillUIWithData(res.value);

                qrLoader.setLoaderListener(new Loader.LoaderListener(){
                    @Override
                    protected void onLoaded() {
                        qrLoader.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileActivity.this,qrImg, "profile");
                                Intent intent = new Intent(ProfileActivity.this,QrActivity.class);
                                startActivity(intent, options.toBundle());
                            }
                        });
                    }

                    @Override
                    protected void onError() {
                        qrLoader.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(ProfileActivity.this, "You are Offline", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                NetworkManager.internet.observe(ProfileActivity.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean internet) {
                        if (!internet) {
                            Log.d("asd","errors");
                            qrLoader.error();
                        } else {
                            refreshQr(res.value.gyanithId);
                        }
                    }
                });
            }
        });



    }

    private void FillUIWithData(final GyanithUser user){
        username.setText("@" + user.userName);
        email.setText(user.email);
        emailTop.setText(user.email);
        phone.setText(user.phoneNo);
        clg.setText(user.clg);

        profile2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, Profile2Activity.class);
                startActivity(intent);
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GyanithUserManager.SignOutUser("Sign Out Successful");
                finish();
            }
        });
    }


    private void refreshQr(String value){
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
            qrLoader.loaded();
        } catch (WriterException e) {
            qrLoader.error();
        }
    }
}
