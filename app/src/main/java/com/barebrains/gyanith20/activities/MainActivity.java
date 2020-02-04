package com.barebrains.gyanith20.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.BotNavView;
import com.barebrains.gyanith20.fragments.CommunityFragment;
import com.barebrains.gyanith20.fragments.FavouritesFragment;
import com.barebrains.gyanith20.fragments.HomeFragment;
import com.barebrains.gyanith20.fragments.NotificationFragment;
import com.barebrains.gyanith20.fragments.ScheduleFragment;
import com.barebrains.gyanith20.fragments.botSheet;
import com.barebrains.gyanith20.statics.Configs;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private TextView title;
    boolean doubleBackToExitPressedOnce = false;

    private FragmentManager fragmentManager;
    public Fragment activeFragment;

    public static BotNavView botNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Fade());
        }
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        Configs.checkUpdate(this,fragmentManager);


        title = findViewById(R.id.title);
        botNav = findViewById(R.id.navigation);
        botNav.setOnNavigationItemSelectedListener(this);

        for (int i = 0; i<botNav.getMenu().size(); i++){
            Fragment fragment = fragmentManager.findFragmentByTag(Integer.toString(i));
                if (fragment != null){
                    activeFragment = fragment;
                    break;
                }
        }


        if (activeFragment == null)
        botNav.setSelectedItemId(R.id.navigation_home);

        findViewById(R.id.account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try {
            Fragment fragment = fragmentManager.findFragmentByTag(((Integer)item.getOrder()).toString());
            if (fragment == null)
                initFragment(item.getOrder());
            else
                setActiveFragment(fragment);
            title.setText(item.getTitle());
            return true;

        }catch (Exception e){
            Log.d("asd","BotNav.ItemSelected : " + e.getMessage());
            return false;
        }
    }


    private void setActiveFragment(Fragment fragment) {

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.hide(activeFragment);
        ft.show(fragment).commit();
        activeFragment = fragment;
    }

    private void initFragment(Integer order) throws Exception {
        Fragment fragment = getNewFragment(order);

        FragmentTransaction ft = fragmentManager.beginTransaction()
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.mainframe, fragment, order.toString());
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
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private Fragment getNewFragment(int order) throws Exception{
        switch (order){
            case 0:
                return new  HomeFragment();
            case 1:
                return new  ScheduleFragment();
            case 2:
                return new FavouritesFragment();
            case 3:
                return new  NotificationFragment();
            case 4:
                return new CommunityFragment();
            default:
                throw new Exception("No fragment defined for id");
        }
    }

}

