package com.barebrains.gyanith20.fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.barebrains.gyanith20.activities.AboutActivity;
import com.barebrains.gyanith20.activities.EventCategoriesActivity;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.activities.EventDetailsActivity;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.statics.eventsManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.List;
import java.util.Random;


public class HomeFragment extends Fragment {
    private long delay1 = 0;
    private long delay2 = 0;
    private SliderLayout imgSlider;
    private boolean urlLoaded;
    private  String trendurl;
    private FloatingActionButton trend, random, fab;
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
        trend = (FloatingActionButton) root.findViewById(R.id.trend);
        fab = (FloatingActionButton) root.findViewById(R.id.fab);
        random = (FloatingActionButton) root.findViewById(R.id.random);
        imgSlider.addSlider((new  DefaultSliderView(getContext())).image(R.drawable.l2));
        urlLoaded = false;
        trendurl ="https://www.youtube.com/channel/UCL8yfte7HZy_KGE-fckMsqQ/videos";
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

        DatabaseReference trendref = FirebaseDatabase.getInstance().getReference().child("/HomeFragment_btnurl");
        trendref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              try{ trendurl = dataSnapshot.getValue().toString();}catch (NullPointerException n){n.printStackTrace();}
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        CardView w = root.findViewById(R.id.w);
        CardView te = root.findViewById(R.id.te);
        CardView nte = root.findViewById(R.id.ne);
        CardView ps = root.findViewById(R.id.ps);
        CardView gl = root.findViewById(R.id.gl);
        CardView au = root.findViewById(R.id.au);
        trend.setOnClickListener(trendclk);
        random.setOnClickListener(randomevent);
        fab.setOnClickListener(fabclk);
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
    private  View.OnClickListener fabclk = new View.OnClickListener(){
        @Override
        public void onClick(View view)
        {

        }
    };
    private View.OnClickListener randomevent = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            Gson gson = new Gson();
            EventItem[] events = eventsManager.getEventItemsFromCache();
            Intent rndeve = new Intent(getContext(), EventDetailsActivity.class);
            Random r = new Random();
            try{
            if(events != null){
            int rnd = r.nextInt(events.length - 1);
            rndeve.putExtra("eventItem",gson.toJson(events[rnd]) );
            startActivity(rndeve);}
            else
                Toast.makeText(getContext(), "network error", Toast.LENGTH_SHORT).show();}
            catch (Exception c){  Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();}
        }
    };

    private  View.OnClickListener trendclk = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try{
                Intent trending = new Intent(Intent.ACTION_VIEW,Uri.parse(trendurl));
                startActivity(trending);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };
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

