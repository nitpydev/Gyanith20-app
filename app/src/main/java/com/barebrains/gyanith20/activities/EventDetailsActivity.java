package com.barebrains.gyanith20.activities;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.AnimatedToggle;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.statics.EventsModel;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.HashSet;
import java.util.Set;

import static com.barebrains.gyanith20.gyanith20.sp;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(R.layout.activity_event_details);


        //VIEW BINDINGS
        final Loader loader = findViewById(R.id.detailsLoader);
        findViewById(R.id.backbut2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        EventsModel model = ViewModelProviders.of(this).get(EventsModel.class);

        loader.loading();

        model.getItem(getIntent().getStringExtra("EXTRA_ID")).observe(this, new Observer<Resource<EventItem>>() {
            @Override
            public void onChanged(Resource<EventItem> res) {

                if (res.handleLoader(loader))
                    return;

                fillTopUI(res.value[0]);
                setUpViewPager(res.value[0]);

                loader.loaded();
            }
        });







    }

    private void fillTopUI(final EventItem eventItem){
        final ImageView f= findViewById(R.id.fh);
        TextView title=findViewById(R.id.evedttitle);
        ImageView eveimage= findViewById(R.id.eveimv);
        AnimatedToggle favBtn =findViewById(R.id.favButton);

        Glide.with(this)
                .load(eventItem.img1)
                .placeholder(R.drawable.l2)
                .error(R.drawable.gyanith_error)
                .into(eveimage);
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

    private void setUpViewPager(EventItem eventItem){

        ViewPager viewPager = findViewById(R.id.event_details_viewpager);
        TabLayout tabLayout =findViewById(R.id.dtab);

        viewPager.setAdapter(new pager(eventItem));
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

        findViewById(R.id.reg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EventDetailsActivity.this, "Registration is yet to open", Toast.LENGTH_SHORT).show();
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
            loader.addView(textView);
            container.addView(loader);
            switch (position) {
                case 0:
                    if (eventItem.des == null || eventItem.des.isEmpty()) {
                        loader.error(0);
                        break;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        textView.setText(Html.fromHtml(eventItem.des, Html.FROM_HTML_MODE_LEGACY));
                    else
                        textView.setText(Html.fromHtml(eventItem.des));
                    if (eventItem.cost != null)
                        textView.append("\nRegistration Cost : Rs." + eventItem.cost + " per person");
                    loader.loaded();
                    break;
                case 1:
                    if (eventItem.type.equals("w"))
                        pageTitles[1] = "REQUISITES";

                    if (eventItem.rules == null || eventItem.rules.isEmpty()) {
                        loader.error();
                        break;
                    }
                    textView.setText(eventItem.rules);
                    loader.loaded();
                    break;
                case 2:
                    if (eventItem.contact == null) {
                        loader.error();
                        break;
                    }
                    textView.setText(eventItem.contact);
                    break;
            }
            return loader;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        }

        @Override
        public int getCount() {
            return 3;
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

}



