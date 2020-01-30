package com.barebrains.gyanith20.activities;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.ImageSlider;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.statics.EventsModel;

public class TShirtActivity extends AppCompatActivity {

    EventItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tshirt);

        final Loader loader = findViewById(R.id.tshirt_loader);

        EventsModel model = ViewModelProviders.of(this).get(EventsModel.class);

        model.getItem("44").observe(this, new Observer<Resource<EventItem>>() {
            @Override
            public void onChanged(Resource<EventItem> res) {
                if (res.handleWithLoader(loader))
                    return;

                item = res.value;

                setUpImgSlider();

                TextView desc = findViewById(R.id.tshirt_desc);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    desc.setText(Html.fromHtml(item.des, Html.FROM_HTML_MODE_LEGACY));
                else
                    desc.setText(Html.fromHtml(item.des));

                if (item.cost != null && !item.cost.equals(""))
                desc.append("\nRegistration cost :\n"+ cost_parse(item.cost)+"\n \n \n");
            }
        });


        findViewById(R.id.tshirt_buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TShirtActivity.this, "Will be Available Soon!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setUpImgSlider(){
        ImageSlider slider = findViewById(R.id.tshirt_img_slider);
        slider.load(new Object[]{item.img1,item.img2}).start();
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
