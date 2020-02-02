package com.barebrains.gyanith20.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.AnimatedToggle;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.statics.EventsModel;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.HashSet;
import java.util.Set;

import static com.barebrains.gyanith20.activities.Instruction.EXTRA_EVENT_ID;
import static com.barebrains.gyanith20.activities.Instruction.EXTRA_MAX_PTPS;
import static com.barebrains.gyanith20.gyanith20.sp;

public class EventDetailsActivity extends AppCompatActivity {

    ImageView eveimage;
    AppBarLayout appBarLayout;
    TextView title;
    TabLayout tabLayout;
    String ptps, id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            //getWindow().setSharedElementReturnTransition(null);
            //getWindow().setSharedElementReenterTransition(null);
        }

        setContentView(R.layout.activity_event_details);



        //VIEW BINDINGS
        final Loader loader = findViewById(R.id.details_loader);
        findViewById(R.id.backbut2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        EventsModel model = ViewModelProviders.of(this).get(EventsModel.class);
        eveimage = findViewById(R.id.eveimv);
        appBarLayout = findViewById(R.id.appbar);
        title = findViewById(R.id.event_title);

        loader.loading();

        model.getItem(getIntent().getStringExtra("EXTRA_ID")).observe(this, new Observer<Resource<EventItem>>() {
            @Override
            public void onChanged(Resource<EventItem> res) {

                if (res.handleWithLoader(loader))
                    return;
                id = res.value.id;
                ptps = res.value.getMax_ptps();
                fillTopUI(res.value);
                setUpViewPager(res.value);
            }
        });







    }

    private void fillTopUI(final EventItem eventItem){
        final ImageView f= findViewById(R.id.fh);
        AnimatedToggle favBtn =findViewById(R.id.favButton);


        Toolbar toolbar = findViewById(R.id.tool);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                title.setAlpha(1+(verticalOffset/100f));
            }
        });
        setSupportActionBar(toolbar);
        Glide.with(this)
                .load(eventItem.img1)
                .placeholder(R.drawable.abbg1)
                .error(R.drawable.abbg1)
                .centerCrop()
                .into(eveimage);
        toolbar.setTitle(eventItem.name);
        title.setText(eventItem.name);

        Set<String> favIds = sp.getStringSet(getString(R.string.favSet), new HashSet<String>());

        favBtn.setChecked(favIds.contains(eventItem.id));


        favBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Set<String> favIds = sp.getStringSet(getString(R.string.favSet), new HashSet<String>());
                if (isChecked)
                    favIds.add(eventItem.id);
                else
                    favIds.remove(eventItem.id);
                sp.edit().putStringSet(getString(R.string.favSet), favIds).apply();


                if(isChecked){
                    ObjectAnimator fa=ObjectAnimator.ofFloat(f,"alpha",1,0);
                    fa.setDuration(500);
                    fa.start();
                    ObjectAnimator fa1=ObjectAnimator.ofFloat(f,"scaleX",1,5);
                    fa1.setDuration(500);
                    fa1.start();
                    ObjectAnimator fa2=ObjectAnimator.ofFloat(f,"scaleY",1,5);
                    fa2.setDuration(500);
                    fa2.start();

                }
            }
        });

    }

    private void setUpViewPager(final EventItem eventItem){

        ViewPager viewPager = findViewById(R.id.event_details_viewpager);
        tabLayout = findViewById(R.id.dtab);

        viewPager.setAdapter(new pager(eventItem));
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

        findViewById(R.id.reg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkManager.internet_value) {

                    GyanithUserManager.getCurrentUser().observe(EventDetailsActivity.this, new Observer<Resource<GyanithUser>>() {
                        @Override
                        public void onChanged(Resource<GyanithUser> user) {
                            if (user.value != null) {
                                Intent i = new Intent(EventDetailsActivity.this, Instruction.class);
                                i.putExtra(EXTRA_MAX_PTPS,ptps);
                                i.putExtra(EXTRA_EVENT_ID, id);
                                startActivity(i);
                            } else {
                                Toast.makeText(EventDetailsActivity.this, "Sign in to Register", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else
                {
                    Toast.makeText(EventDetailsActivity.this, "No Internet",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private class pager extends PagerAdapter {

        private EventItem eventItem;

        private String[] pageTitles = new String[]{"ABOUT", "RULES", "CONTACT"};

        private pager(EventItem eventItem) {
            this.eventItem = eventItem;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Loader loader = new Loader(EventDetailsActivity.this);
            loader.set_empty_error("Will be Updated soon");
            TextView textView = new TextView(new ContextThemeWrapper(EventDetailsActivity.this, R.style.eventDes));

            //Adjustment for guestlectures and proshows
            if(eventItem.type.equals("g") || eventItem.type.equals("p"))
                if(position == 1)
                    position++;

            NestedScrollView scrollView = new NestedScrollView(EventDetailsActivity.this);
            scrollView.addView(textView);
            loader.addView(scrollView);
            switch (position) {
                case 0:
                    if (eventItem.des == null || eventItem.des.equals("")) {
                        loader.error(0);
                        break;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        textView.setText(Html.fromHtml(eventItem.des, Html.FROM_HTML_MODE_LEGACY));
                    else
                        textView.setText(Html.fromHtml(eventItem.des));

                    if (eventItem.cost != null && !eventItem.cost.equals(""))
                        textView.append("\nRegistration Cost : \n" + cost_parse(eventItem.cost)+ "\n \n \n");
                    loader.loaded();
                    break;
                case 1:
                    if (eventItem.type.equals("w")) {
                        pageTitles[1] = "REQUISITES";
                        tabLayout.getTabAt(1).setText(pageTitles[1]);
                    }

                    if (eventItem.rules == null || eventItem.rules.equals("")) {
                        loader.error();
                        Log.d("asd","error");
                        break;
                    }
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {
                        textView.setText(Html.fromHtml(eventItem.rules,Html.FROM_HTML_MODE_LEGACY));
                    }
                    else
                    {
                        textView.setText(Html.fromHtml(eventItem.rules));
                    }
                    loader.loaded();
                    break;
                case 2:
                    if (eventItem.contact == null || eventItem.contact.equals("")) {
                        loader.error();
                        break;
                    }
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {
                        textView.setText(Html.fromHtml(eventItem.contact,Html.FROM_HTML_MODE_LEGACY));
                    }
                    else
                    {
                        textView.setText(Html.fromHtml(eventItem.contact));
                    }
                    loader.loaded();
                    break;
            }
            container.addView(loader);
            return loader;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        }

        @Override
        public int getCount() {
            return (eventItem.type.equals("g") || eventItem.type.equals("p"))?2:3;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles[position];
        }
    }

    private  String cost_parse(String cost)
    {    String parsed = "";
        if(cost != null){
        String[] cost_arr = cost.split(",");
        for(int i = 0; i < cost_arr.length; i++)
        {
            if(i%2 == 0)
                parsed =  " For " + cost_arr[i] + " person" + "\n" + parsed;
            else
                parsed = " Rs." + cost_arr[i] +parsed;
        }}
        return parsed;

    }
}




