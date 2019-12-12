package com.barebrains.gyanith20.Activities;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.barebrains.gyanith20.Fragments.CommunityFragment;
import com.barebrains.gyanith20.Fragments.FavouritesFragment;
import com.barebrains.gyanith20.Fragments.HomeFragment;
import com.barebrains.gyanith20.Fragments.NotificationFragment;
import com.barebrains.gyanith20.Fragments.ScheduleFragment;
import com.barebrains.gyanith20.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView botnav;
    private TextView title;
    boolean doubleBackToExitPressedOnce;
    private ImageView imageView;
    SharedPreferences notif;

    private FragmentManager fragmentManager;
    private Fragment activeFragment;
    private HomeFragment homeFragment;
    private ScheduleFragment scheduleFragment;
    private FavouritesFragment favouritesFragment;
    private NotificationFragment notificationFragment;
    private CommunityFragment communityFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (homeFragment == null)
                        initFragment((homeFragment = new HomeFragment()), "1");
                    else
                        setActiveFragment(homeFragment);
                    title.setText(R.string.topbar_home);
                    return true;
                case R.id.navigation_schedule:
                    if (scheduleFragment == null)
                        initFragment((scheduleFragment = new ScheduleFragment()), "2");
                    else
                        setActiveFragment(scheduleFragment);
                    title.setText(R.string.topbar_schedule);
                    return true;
                case R.id.navigation_favourites:
                    if (favouritesFragment == null)
                        initFragment((favouritesFragment = new FavouritesFragment()), "3");
                    else
                        setActiveFragment(favouritesFragment);
                    title.setText(R.string.topbar_favourites);
                    return true;
                case R.id.navigation_notifications:
                    if (notificationFragment == null)
                        initFragment((notificationFragment = new NotificationFragment()), "4");
                    else
                        setActiveFragment(notificationFragment);
                    title.setText(R.string.topbar_notification);
                    item.setIcon(R.drawable.ic_baseline_notifications_24px);
                    notif.edit().putBoolean("newnot",false).apply();
                    return true;
                case R.id.navigation_community:
                    if (communityFragment == null)
                        initFragment((communityFragment = new CommunityFragment()), "5");
                    else
                        setActiveFragment(communityFragment);
                    title.setText("Community");
                    return true;
            }
            return false;
        }
    };

    private void setActiveFragment(Fragment fragment)
    {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.hide(activeFragment);
        ft.show(fragment).commit();
        activeFragment = fragment;
    }

    private void initFragment(Fragment fragment,String tag){
        FragmentTransaction ft = fragmentManager.beginTransaction()
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.mainframe,fragment,tag);
        if (activeFragment != null)
            ft.hide(activeFragment);
        ft.commit();
        activeFragment = fragment;
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
        setContentView(R.layout.activity_main);

        doubleBackToExitPressedOnce = false;
        fragmentManager = getSupportFragmentManager();
        imageView=findViewById(R.id.topbaricon);
        title = findViewById(R.id.title);
        notif = getSharedPreferences(getString(R.string.package_name), Context.MODE_PRIVATE);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

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
                Toast.makeText(MainActivity.this,"Presented to you by HV,BP,KR,SR,AM",Toast.LENGTH_SHORT).show();
            }
        });

        if(notif.getBoolean("newnot",false)){
            navigation.getMenu().getItem(3).setIcon(R.drawable.ic_notification);
        }
}
}
