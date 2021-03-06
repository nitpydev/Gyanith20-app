package com.barebrains.gyanith20.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.activities.TechExpoActivity;
import com.barebrains.gyanith20.activities.AboutActivity;
import com.barebrains.gyanith20.activities.Accommodation;
import com.barebrains.gyanith20.activities.EventDetailsActivity;
import com.barebrains.gyanith20.activities.EventsCategoryActivity;
import com.barebrains.gyanith20.activities.Profile2Activity;
import com.barebrains.gyanith20.activities.TShirtActivity;
import com.barebrains.gyanith20.activities.Web;
import com.barebrains.gyanith20.components.ImageSlider;
import com.barebrains.gyanith20.interfaces.ArrayResource;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.others.mFragment;
import com.barebrains.gyanith20.statics.DataRepository;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.Util;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class HomeFragment extends mFragment {

    private ImageSlider imgSlider;

    public HomeFragment() {    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        imgSlider = root.findViewById(R.id.img_slider);
        FloatingActionButton trend = root.findViewById(R.id.accomdation);
        FloatingActionButton random = root.findViewById(R.id.tshirt);
        FloatingActionButton dev = root.findViewById(R.id.fab);
        DatabaseReference imgurl = FirebaseDatabase.getInstance().getReference().child("ImageUrls");
        final StorageReference slidesFolderRef = FirebaseStorage.getInstance().getReference().child("/HomeImageSlides");

        imgurl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> imgnames = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getValue() != null)
                        imgnames.add(data.getValue().toString());
                }
                imgSlider.load(Util.getStorageRefs(imgnames, slidesFolderRef)).apply((new RequestOptions()).centerCrop()).start();
                imgSlider.autoScroll(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imgSlider.getIndicator().setVisibility(View.GONE);

        CardView w = root.findViewById(R.id.w);
        CardView te = root.findViewById(R.id.te);
        CardView nte = root.findViewById(R.id.ne);
        CardView ps = root.findViewById(R.id.p);
        CardView t = root.findViewById(R.id.t);
        CardView au = root.findViewById(R.id.au);
        CardView pe = root.findViewById(R.id.pe);
        CardView h = root.findViewById(R.id.h);

        trend.setOnClickListener(accclk);
        dev.setOnClickListener(devclk);
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TShirtActivity.class);
                startActivity(intent);
            }
        });


        w.setOnClickListener(eventCategoryClick);
        te.setOnClickListener(eventCategoryClick);
        nte.setOnClickListener(eventCategoryClick);
        ps.setOnClickListener(eventCategoryClick);


        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TechExpoActivity.class);
                startActivity(intent);
            }
        });

        au.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AboutActivity.class);
                startActivity(i);
            }
        });

        pe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EventDetailsActivity.class);
                intent.putExtra("EXTRA_ID","30");
                getContext().startActivity(intent);
            }
        });

        h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EventDetailsActivity.class);
                intent.putExtra("EXTRA_ID","26");
                getContext().startActivity(intent);
            }
        });

        Animation[] fromLeft = new Animation[3];
        Animation[] fromRight = new Animation[3];

        for (int i = 0;i < 3 ; i++) {
            fromLeft[i] = AnimationUtils.loadAnimation(getContext(),R.anim.item_intro_left);
            fromLeft[i].setStartOffset(i*fromLeft[i].getDuration()/6);

            fromRight[i] = AnimationUtils.loadAnimation(getContext(),R.anim.item_intro_right);
            fromRight[i].setStartOffset(i*fromRight[i].getDuration()/6);
        }

        w.setAnimation(fromLeft[0]);
        nte.setAnimation(fromLeft[1]);
        ps.setAnimation(fromLeft[2]);

        te.setAnimation(fromRight[0]);
        t.setAnimation(fromRight[1]);
        au.setAnimation(fromRight[2]);
        /*
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

        ObjectAnimator ua = ObjectAnimator.ofFloat(au, "translationX", -300f, 0f);
        ua.setInterpolator(new DecelerateInterpolator());
        ua.setStartDelay(delay1);
        ua.setDuration(300);
        ua.start();
        ObjectAnimator ul = ObjectAnimator.ofFloat(au, "alpha", 0, 1);
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

 */


        return root;
    }



    private View.OnClickListener eventCategoryClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(getContext(), EventsCategoryActivity.class);
            i.putExtra("category", view.getTag().toString());
            i.putExtra("type", getResources().getResourceEntryName(view.getId()));
            startActivity(i);
        }
    };


    private View.OnClickListener accclk = new View.OnClickListener(){
        @Override
        public void  onClick(View view){
           Intent acc = new Intent(getContext(), Accommodation.class);
           //acc.putExtra("EXTRA_ID","o");
           startActivity(acc);
        }
    };

    @Override
    public void onHide() {
        super.onHide();
        if (getView() == null)
            return;
        GyanithUserManager.getCurrentUser().removeObservers(getViewLifecycleOwner());
    }

    private View.OnClickListener devclk = new View.OnClickListener(){
        @Override
        public void  onClick(View view){
            GyanithUserManager.getCurrentUser().removeObservers(getViewLifecycleOwner());
            GyanithUserManager.getCurrentUser().observe(HomeFragment.this.getViewLifecycleOwner(), new Observer<Resource<GyanithUser>>() {
                @Override
                public void onChanged(Resource<GyanithUser> resource) {
                    if (resource.value != null) {
                        Intent redirect = new Intent(getContext(), Profile2Activity.class);
                        startActivity(redirect);
                    }
                    else
                        Toast.makeText(HomeFragment.this.getContext(), "You are not Signed In!", Toast.LENGTH_SHORT).show();

                    GyanithUserManager.getCurrentUser().removeObserver(this);
                }
            });
        }
    };
}
