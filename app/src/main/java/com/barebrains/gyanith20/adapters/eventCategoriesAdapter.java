package com.barebrains.gyanith20.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;

import android.graphics.Bitmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;

import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.barebrains.gyanith20.models.eventitem;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.others.ImageVolley;
import com.barebrains.gyanith20.statics.Util;

import java.io.File;
import java.util.ArrayList;

public class eventCategoriesAdapter extends ArrayAdapter{

    int res;
    String url ;
    ArrayList<eventitem> ei;
    Context c;
    int pos=0;
    ImageLoader imageLod;
    NetworkImageView imageView;

    public eventCategoriesAdapter(int res, ArrayList<eventitem> ei, Context c) {
        super(c, res, ei);
        this.res = res;
        this.ei = ei;
        this.c = c;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(c);
        View root = li.inflate(res, null, false);
        ((TextView) root.findViewById(R.id.eveitname)).setText(ei.get(position).getName());
        ((TextView) root.findViewById(R.id.eveittime)).setText(ei.get(position).getTime());
        if (pos==position) {


        root.setAlpha(0);


        Long delay = Long.valueOf(position * 150);
        ObjectAnimator a = ObjectAnimator.ofFloat(root, "alpha", 0, 1);
        a.setStartDelay(delay);
        a.start();
        ObjectAnimator o = ObjectAnimator.ofFloat(root, "scaleX", 0.5f, 1f);
        o.setStartDelay(delay);
        o.setInterpolator(new DecelerateInterpolator());
        o.setDuration(500);
        o.start();
       pos++;
    }

        ((TextView)root.findViewById(R.id.eveitname)).setText(ei.get(position).getName());
        ((TextView)root.findViewById(R.id.eveittime)).setText(ei.get(position).getTime());

        try {
           url = ei.get(position).getImg2();
        }
        catch(NullPointerException e)
        {
            url = "null";
        }

        imageView =(NetworkImageView)root.findViewById(R.id.logo);
        imageLod = ImageVolley.getInstance(c).getImageLoader();
        imageLod.get(url, new ImageLoader.ImageListener(){

                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        imageView.setImageResource(R.drawable.l2);

                        Bitmap img = response.getBitmap();



                      if(img != null)
                        {
                            imageView.setImageBitmap(img);

                             try {
                                 File imgfile = new File(c.getCacheDir().getPath() , ei.get(position).getTag());
                                 Util.putBitmaptoFile(img, imgfile);
                             }catch(Exception e)
                             {
                                 e.printStackTrace();
                             }
                        }




                    }
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        imageView.setImageResource(R.drawable.l2);


                        File imgfile = new File(c.getCacheDir().getPath(),ei.get(position).getTag());
                        try{
                            Bitmap btm = Util.decodeFile(imgfile);
                            if(btm != null)
                                imageView.setImageBitmap(btm);
                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }


                    }
                }
        );
        imageView.setImageUrl(url, imageLod);



        return root;
    }
}
