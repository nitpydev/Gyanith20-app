package com.barebrains.gyanith20.activities;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
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
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.statics.EventsModel;
import com.google.android.material.tabs.TabLayout;

public class Accommodation extends AppCompatActivity {

    TabLayout tabs;
    Button btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accom);
        final  Loader loader = findViewById(R.id.accom_loader);
        btn = findViewById(R.id.reg_acc);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Accommodation.this, "Registration is yet to open", Toast.LENGTH_SHORT).show();

            }
        });


        findViewById(R.id.backbu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        EventsModel model = ViewModelProviders.of(this).get(EventsModel.class);
        loader.loading();

        model.getItem("43").observe(this, new Observer<Resource<EventItem>>() {
            @Override
            public void onChanged(Resource<EventItem> res) {

                if(res.handleWithLoader(loader)){
                    return;
                }
                ViewPager viewPager = findViewById(R.id.accom_viewpager);

                tabs = findViewById(R.id.accom_tabs);

                viewPager.setAdapter(new Viewpager(res.value));

                tabs.setupWithViewPager(viewPager);
            }
        });



    }

    // parsing cost param
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
    private class Viewpager extends PagerAdapter
    {
        private EventItem acc;

        private String[] titles = new String[]{"DETAILS","CONTACT"};

        private Viewpager(EventItem item)
        {
            this.acc = item;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Loader loader = new Loader(Accommodation.this);
            loader.set_empty_error("will be updated soon");
            TextView textView = new TextView(new ContextThemeWrapper(Accommodation.this, R.style.eventDes));
            ScrollView scrollView = new ScrollView(Accommodation.this);


            switch(position)
            {
                case 0:
                    scrollView.addView(textView);
                  loader.addView(scrollView);
                  if (acc.des == null || acc.des.equals("")){
                      loader.error(0);
                      break;
                  }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        textView.setText(Html.fromHtml(acc.des, Html.FROM_HTML_MODE_LEGACY));
                    else
                        textView.setText(Html.fromHtml(acc.des));
                    if (acc.cost != null && !acc.cost.equals(""))
                    textView.append("\nRegistration cost :\n"+ cost_parse(acc.cost)+"\n \n \n");

                  loader.loaded();
                    break;
                case 1:
                    loader.addView(textView);
                    if(acc.contact == null || acc.contact.equals(""))
                    {
                        loader.error(0);
                        break;
                    }
                    textView.setText(acc.contact);
                    loader.loaded();
                    break;


            }

            container.addView(loader);
            return loader;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);

        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return  titles[position];
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
