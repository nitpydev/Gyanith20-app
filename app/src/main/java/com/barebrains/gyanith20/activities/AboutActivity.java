package com.barebrains.gyanith20.activities;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.barebrains.gyanith20.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AboutActivity extends AppCompatActivity {
    private Boolean show=false;
    private String furl,iurl,wurl,desc;
    private DatabaseReference db,fb;
    Typeface font,fontt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        (findViewById(R.id.backabt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        db= FirebaseDatabase.getInstance().getReference().child("misc");
        fb=FirebaseDatabase.getInstance().getReference().child("feedback");
        final FloatingActionButton main= findViewById(R.id.mainbut);
        final FloatingActionButton share= findViewById(R.id.sharebut);
        final FloatingActionButton directions= findViewById(R.id.direcbut);
        final FloatingActionButton feed= findViewById(R.id.feebut);
        fontt=Typeface.createFromAsset(getAssets(),"fonts/sofiaprolight.otf");
        font=Typeface.createFromAsset(getAssets(),"fonts/pnreg.otf");


        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{desc = dataSnapshot.child("desc").getValue().toString();
                furl=dataSnapshot.child("fburl").getValue().toString();
                iurl=dataSnapshot.child("inurl").getValue().toString();
                wurl=dataSnapshot.child("wburl").getValue().toString();}catch (NullPointerException n){
                    if(desc == null)
                    ((TextView)findViewById(R.id.descr)).setText(R.string.about);
                    if(furl == null)
                        furl = "https://www.facebook.com/gyanith.nitpy/";
                    if(iurl == null)
                        iurl = "https://www.instagram.com/nitpy_gyanith/";
                    if(wurl == null)
                        wurl = "https://www.gyanith.org/index.html";}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ((TextView)findViewById(R.id.descr)).setText(desc);
        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Uri gm= Uri.parse("http://maps.google.com/maps?&daddr=nit puducherry,thiruvettakudy,karaikal");
                Uri gmmIntentUri = Uri.parse("google.navigation:q=nit+puducherry+thiruvetakudy+karaikal");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gm);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder b=new AlertDialog.Builder(AboutActivity.this);
                b.setView(R.layout.feedlay);
                b.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      String key=fb.push().getKey();
                      View v=getLayoutInflater().inflate(R.layout.feedlay,null);
                      RatingBar r= v.findViewById(R.id.rating);
                      EditText f= v.findViewById(R.id.feedin);
                      fb.child(key).child("comment").setValue(f.getText().toString());
                      fb.child(key).child("rating").setValue(r.getNumStars());
                        Snackbar.make(findViewById(R.id.ll),"Thanks for giving your feedback",Snackbar.LENGTH_LONG).show();






                    }
                });
                b.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                b.setNeutralButton("Rate us", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent w=new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.barebrains.gyanith19"));
                        startActivity(w);
                    }
                });
                b.show();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("body/plain");
                String shareBody = "Check out the Gyanith app\n https://play.google.com/store/apps/details?id=com.barebrains.gyanith19";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download Gyanith app");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });



        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!show){
                ObjectAnimator s=ObjectAnimator.ofFloat(share,"translationY",0f,-160f);
                s.setDuration(400);
                s.setInterpolator(new DecelerateInterpolator());
                s.start();
                ObjectAnimator d=ObjectAnimator.ofFloat(directions,"translationY",0f,-320f);
                d.setDuration(400);
                    d.setInterpolator(new DecelerateInterpolator());
                    d.start();
                ObjectAnimator f=ObjectAnimator.ofFloat(feed,"translationY",0f,-480f);
                f.setDuration(400);
                    f.setInterpolator(new DecelerateInterpolator());

                    f.start();
                    ObjectAnimator m=ObjectAnimator.ofFloat(main,"rotation",0f,-45f);
                    m.setDuration(400);
                    m.setInterpolator(new DecelerateInterpolator());
                    m.start();
                    show=!show;
            }else {

                    ObjectAnimator s=ObjectAnimator.ofFloat(share,"translationY",-130f,0f);
                    s.setDuration(400);
                    s.setInterpolator(new DecelerateInterpolator());
                    s.start();
                    ObjectAnimator d=ObjectAnimator.ofFloat(directions,"translationY",-260f,0f);
                    d.setDuration(400);
                    d.setInterpolator(new DecelerateInterpolator());
                    d.start();
                    ObjectAnimator f=ObjectAnimator.ofFloat(feed,"translationY",-390f,0f);
                    f.setDuration(400);
                    f.setInterpolator(new DecelerateInterpolator());

                    f.start();
                    ObjectAnimator m=ObjectAnimator.ofFloat(main,"rotation",-45f,0f);
                    m.setDuration(400);
                    m.setInterpolator(new DecelerateInterpolator());
                    m.start();
                    show=!show;

                }
            }

        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent w=new Intent(Intent.ACTION_VIEW,Uri.parse(furl));
                startActivity(w);
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent w=new Intent(Intent.ACTION_VIEW,Uri.parse(iurl));
                startActivity(w);
            }
        });
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent w=new Intent(Intent.ACTION_VIEW,Uri.parse(wurl));
                startActivity(w);
            }
        });

    }
}
