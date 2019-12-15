package com.barebrains.gyanith20.fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.barebrains.gyanith20.activities.AboutActivity;
import com.barebrains.gyanith20.activities.EventCategoriesActivity;
import com.barebrains.gyanith20.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;



public class HomeFragment extends Fragment {
    private long delay1 = 0;
    private long delay2 = 0;
    CarouselView carouselView;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    int[] sampleImages = {R.drawable.abbg1, R.drawable.tab, R.drawable.about};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        carouselView = (CarouselView) root.findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);

        carouselView.setImageListener(imageListener);
        ((CardView) root.findViewById(R.id.w)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EventCategoriesActivity.class);
                i.putExtra("category", "Workshop");
                startActivity(i);
            }
        });
        ((CardView) root.findViewById(R.id.te)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EventCategoriesActivity.class);
                i.putExtra("category", "Technical Events");
                startActivity(i);
            }
        });
        ((CardView) root.findViewById(R.id.nte)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EventCategoriesActivity.class);
                i.putExtra("category", "Non Technical Events");
                startActivity(i);
            }
        });
        ((CardView) root.findViewById(R.id.ps)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EventCategoriesActivity.class);
                i.putExtra("category", "Pro Shows");
                startActivity(i);
            }
        });
        ((CardView) root.findViewById(R.id.ud)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AboutActivity.class);
                //  i.putExtra("category","Unnamed");
                startActivity(i);
            }
        });
        ((CardView) root.findViewById(R.id.gl)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EventCategoriesActivity.class);
                i.putExtra("category", "Guest Lectures");
                startActivity(i);
            }
        });

        final CardView w = (CardView) root.findViewById(R.id.w);
        CardView te = (CardView) root.findViewById(R.id.te);
        CardView nte = (CardView) root.findViewById(R.id.nte);
        CardView ps = (CardView) root.findViewById(R.id.ps);
        CardView gl = (CardView) root.findViewById(R.id.gl);
        CardView un = (CardView) root.findViewById(R.id.ud);

        Intent n = new Intent("gyanith.notify");
        getContext().sendBroadcast(n);




        ObjectAnimator wa = ObjectAnimator.ofFloat(w, "translationX", -300f, 0f);
        wa.setInterpolator(new DecelerateInterpolator());
        wa.setStartDelay(delay1);
        wa.setDuration(300);
        wa.start();
        delay1 += 150;

        ObjectAnimator nta = ObjectAnimator.ofFloat(nte, "translationX", -300f, 0f);
        nta.setInterpolator(new DecelerateInterpolator());
        nta.setStartDelay(delay1);
        nta.setDuration(300);
        nta.start();
        ObjectAnimator ntl = ObjectAnimator.ofFloat(nte, "alpha", 0, 1);
        ntl.setStartDelay(delay1);
        ntl.start();
        delay1 += 150;

        ObjectAnimator ua = ObjectAnimator.ofFloat(un, "translationX", -300f, 0f);
        ua.setInterpolator(new DecelerateInterpolator());
        ua.setStartDelay(delay1);
        ua.setDuration(300);
        ua.start();
        ObjectAnimator ul = ObjectAnimator.ofFloat(un, "alpha", 0, 1);
        ul.setStartDelay(delay1);
        ul.start();
        delay1 += 150;

        ObjectAnimator ta = ObjectAnimator.ofFloat(te, "translationX", 300f, 0f);
        ta.setInterpolator(new DecelerateInterpolator());
        ta.setStartDelay(delay2);
        ta.setDuration(300);
        ta.start();
        delay2 += 150;

        ObjectAnimator pa = ObjectAnimator.ofFloat(ps, "translationX", 300f, 0f);
        pa.setInterpolator(new DecelerateInterpolator());
        pa.setStartDelay(delay2);
        pa.setDuration(300);
        pa.start();
        ObjectAnimator pl = ObjectAnimator.ofFloat(ps, "alpha", 0, 1);
        pl.setStartDelay(delay2);
        pl.start();
        delay2 += 150;

        ObjectAnimator ga = ObjectAnimator.ofFloat(gl, "translationX", 300f, 0f);
        ga.setInterpolator(new DecelerateInterpolator());
        ga.setStartDelay(delay2);
        ga.setDuration(300);
        ga.start();
        ObjectAnimator gll = ObjectAnimator.ofFloat(gl, "alpha", 0, 1);
        gll.setStartDelay(delay2);
        gll.start();
        delay2 += 150;


        return root;
    }
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

}


//
//
//
//
//}
