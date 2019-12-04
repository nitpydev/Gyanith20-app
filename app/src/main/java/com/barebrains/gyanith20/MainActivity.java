package com.barebrains.gyanith20;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView botnav;
    private TextView title;
    boolean doubleBackToExitPressedOnce;
    private ImageView imageView;
    Context context;
    SharedPreferences notif;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    replace(new home());
                    title.setText(R.string.topbar_home);
                    return true;
                case R.id.navigation_schedule:
                    replace(new schedule());
                    title.setText(R.string.topbar_schedule);
                    return true;
                case R.id.navigation_favourites:
                    replace(new favourites());
                    title.setText(R.string.topbar_favourites);
                    return true;
                case R.id.navigation_notifications:
                   replace(new notifications());
                    title.setText(R.string.topbar_notification);
                    item.setIcon(R.drawable.ic_baseline_notifications_24px);
                    notif.edit().putBoolean("newnot",false).apply();
                    return true;
                case R.id.navigation_community:
                    replace(new community());
                    title.setText("Community");
                    return true;
            }
            return false;
        }
    };

    private void replace(Fragment m){
        Fragment f=m;
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.mainframe,f).commit();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
  //  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  //  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Fade());
        }

        setContentView(R.layout.main_layout);
        doubleBackToExitPressedOnce = false;
        imageView=findViewById(R.id.topbaricon);
        context=this;

        notif = context.getSharedPreferences("com.barebrains.Gyanith19", Context.MODE_PRIVATE);
        botnav=findViewById(R.id.navigation);
        title=findViewById(R.id.title);

        BottomNavigationView navigation = (BottomNavigationView) botnav;
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Fragment f=new home();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.mainframe,f).commit();

        ((TextView)findViewById(R.id.title)).setText(R.string.topbar_home);

        ((Button)findViewById(R.id.account)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	//Toast.makeText(getApplicationContext(), "Will be updated soon!", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Presented to you by HV,BP,KR,SR,AM",Toast.LENGTH_SHORT).show();
            }
        });

        if(notif.getBoolean("newnot",false)){
            navigation.getMenu().getItem(3).setIcon(R.drawable.ic_notification);
        }
}

}
