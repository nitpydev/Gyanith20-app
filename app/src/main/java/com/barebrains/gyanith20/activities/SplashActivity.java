package com.barebrains.gyanith20.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.barebrains.gyanith20.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Fade());
        }

        setContentView(R.layout.activity_splash);

        ImageView l=(ImageView)findViewById(R.id.imageView4);
/*
        ObjectAnimator a=ObjectAnimator.ofFloat(l,"alpha",0,1);
        a.setDuration(500);
        a.setInterpolator(new AccelerateInterpolator());
        a.start();
        ObjectAnimator s=ObjectAnimator.ofFloat(l,"scaleX",2,1);
        s.setDuration(500);
        s.setInterpolator(new AccelerateInterpolator());
        s.start();
        ObjectAnimator s1=ObjectAnimator.ofFloat(l,"scaleY",2,1);
        s1.setDuration(500);
        s1.setInterpolator(new AccelerateInterpolator());
        s1.start();

 */


        Timer t=new Timer();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent i=new Intent(SplashActivity.this,MainActivity.class);

                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();


            }
        },2000);
    }
}
