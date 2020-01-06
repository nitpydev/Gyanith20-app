package com.barebrains.gyanith20.activities;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.barebrains.gyanith20.fragments.CommunityFragment;
import com.barebrains.gyanith20.fragments.FavouritesFragment;
import com.barebrains.gyanith20.fragments.HomeFragment;
import com.barebrains.gyanith20.fragments.NotificationFragment;
import com.barebrains.gyanith20.fragments.ScheduleFragment;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.NotificationItem;
import com.barebrains.gyanith20.statics.AppNotiManager;
import com.barebrains.gyanith20.statics.PostManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView title;
    boolean doubleBackToExitPressedOnce;
   // private ImageView imageView;
    SharedPreferences sp;

    private FragmentManager fragmentManager;
    private Fragment activeFragment;
    private HomeFragment homeFragment;
    private ScheduleFragment scheduleFragment;
    private FavouritesFragment favouritesFragment;
    private NotificationFragment notificationFragment;
    private Fragment communityFragment;

    private BottomNavigationView navigation;

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
                    MarkNotiAsRead();
                    return true;
                case R.id.navigation_community:
                    if (communityFragment == null)
                        initFragment((communityFragment = new CommunityFragment()), "5");
                    else
                        setActiveFragment(communityFragment);
                    title.setText(R.string.topbar_community);
                    MarkPostsAsRead();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        PostManager.StartListeningPostCount(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Fade());
        }
        setContentView(R.layout.activity_main);



        doubleBackToExitPressedOnce = false;
        fragmentManager = getSupportFragmentManager();
        //imageView=findViewById(R.id.topbaricon);
        title = findViewById(R.id.title);
        sp = getSharedPreferences(getString(R.string.package_name), Context.MODE_PRIVATE);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setItemIconTintList(null);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

        ((TextView)findViewById(R.id.title)).setText(R.string.topbar_home);

        findViewById(R.id.account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
            }
        });

        LastReadPostCount = sp.getInt("lastReadPostCount",0);
        NotifyCommunityPosts();
        LastReadNotiCount = sp.getInt("lastReadNotiCount",0);
        NotifyNotiPosts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppNotiManager.addNotificationListener(1,new ResultListener<NotificationItem[]>(){
            @Override
            public void OnResult(NotificationItem[] notificationItems) {
                NotifyNotiPosts();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppNotiManager.removeNotificationListener(1);
    }

    public int LastReadPostCount;

public void NotifyCommunityPosts(){
        if (PostManager.postCount > LastReadPostCount)
            navigation.getMenu().getItem(4).setIcon(R.drawable.ic_people_dot);
        else
            navigation.getMenu().getItem(4).setIcon(R.drawable.nav_people_selector);
}

public void MarkPostsAsRead(){
    LastReadPostCount = PostManager.postCount;
    sp.edit().putInt("lastReadPostCount",LastReadPostCount).apply();
    NotifyCommunityPosts();
}

public int LastReadNotiCount;

public void NotifyNotiPosts(){
    if (AppNotiManager.notiItems == null) {
        navigation.getMenu().getItem(3).setIcon(R.drawable.nav_not_selector);
        return;
    }


    if (AppNotiManager.notiItems.length > LastReadNotiCount)
        navigation.getMenu().getItem(3).setIcon(R.drawable.ic_notification_dot);
    else
        navigation.getMenu().getItem(3).setIcon(R.drawable.nav_not_selector);


    if (activeFragment.getTag().equals("4"))
        MarkPostsAsRead();
}

public void MarkNotiAsRead(){
    if (AppNotiManager.notiItems == null){
        return;
    }

    LastReadNotiCount = AppNotiManager.notiItems.length;
    sp.edit().putInt("lastReadNotiCount",LastReadNotiCount).apply();
    NotifyNotiPosts();
}
}
