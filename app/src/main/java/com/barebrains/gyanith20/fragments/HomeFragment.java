package com.barebrains.gyanith20.fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.barebrains.gyanith20.activities.AboutActivity;
import com.barebrains.gyanith20.activities.EventCategoriesActivity;
import com.barebrains.gyanith20.R;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class HomeFragment extends Fragment {
    private long delay1 = 0;
    private long delay2 = 0;
    private SliderLayout imgSlider;
    private boolean urlLoaded;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        imgSlider = root.findViewById(R.id.img_slider);

        imgSlider.addSlider((new  DefaultSliderView(getContext())).image(R.drawable.l2));
        urlLoaded = false;
        final RequestOptions requestOptions = (new RequestOptions())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.l2)
                .error(R.drawable.gyanith_error);

        StorageReference slidesFolderRef = FirebaseStorage.getInstance().getReference().child("/HomeImageSlides");
        slidesFolderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> imgRefs = listResult.getItems();
                for (final StorageReference imgRef : imgRefs)
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            DefaultSliderView item = new DefaultSliderView(HomeFragment.this.getContext());
                            item.image(uri.toString())
                                    .setRequestOption(requestOptions)
                                    .setProgressBarVisible(true);
                            if (!urlLoaded)
                                imgSlider.removeSliderAt(0);
                            imgSlider.addSlider(item);
                            urlLoaded = true;
                        }
                    });
            }
        });
        CardView w = root.findViewById(R.id.w);
        CardView te = root.findViewById(R.id.te);
        CardView nte = root.findViewById(R.id.ne);
        CardView ps = root.findViewById(R.id.ps);
        CardView gl = root.findViewById(R.id.gl);
        CardView au = root.findViewById(R.id.au);

        w.setOnClickListener(eventCategoryClick);
        te.setOnClickListener(eventCategoryClick);
        nte.setOnClickListener(eventCategoryClick);
        ps.setOnClickListener(eventCategoryClick);
        gl.setOnClickListener(eventCategoryClick);
        au.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AboutActivity.class);
                startActivity(i);
            }
        });




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


        return root;
    }

    private View.OnClickListener eventCategoryClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(getContext(), EventCategoriesActivity.class);
            i.putExtra("category", view.getTag().toString());
            i.putExtra("type", getResources().getResourceEntryName(view.getId()));
            startActivity(i);
        }
    };
}

