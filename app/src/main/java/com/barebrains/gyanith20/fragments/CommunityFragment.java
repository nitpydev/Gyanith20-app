package com.barebrains.gyanith20.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.activities.StartPostActivity;
import com.barebrains.gyanith20.components.PostsFeed;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.others.mFragment;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.polyak.iconswitch.IconSwitch;

public class CommunityFragment extends mFragment {


    private View root;
    private Observer<Resource<GyanithUser>> observer;
    private CompletionListener postUploadedListener;

    public CommunityFragment() {
        markBadges(4);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncWithPostService(new CompletionListener(){
            @Override
            public void OnComplete() {
                if (root != null)
                Snackbar.make(root,"Posted Successfully !", BaseTransientBottomBar.LENGTH_LONG)
                .setAction("REFRESH FEED", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      if (postUploadedListener != null)
                          postUploadedListener.OnComplete();
                    }
                }).show();
            }

            @Override
            public void OnError(String error) {
                Log.d("asd","Error : " + error);
                if (root != null)
                    Snackbar.make(root,error,BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_community, container, false);
        final View addPostBtn = root.findViewById(R.id.add_post_btn);

        observer = new Observer<Resource<GyanithUser>>() {
            @Override
            public void onChanged(Resource<GyanithUser> res) {

                if (res.value != null){
                    addPostBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), StartPostActivity.class);
                            startActivity(intent);
                        }
                    });
                }
                else
                {
                    addPostBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(CommunityFragment.this.getContext(), "Sign in to Post", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        };
        super.onCreateView(inflater, container, savedInstanceState);

        final ViewPager viewPager = root.findViewById(R.id.feedViewPager);

        final IconSwitch iconSwitch = root.findViewById(R.id.trendingSwitch);
        syncViewPagerAndIconSwitch(viewPager, iconSwitch);

        viewPager.setOffscreenPageLimit(1);

        viewPager.setAdapter(new PagerAdapter() {

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                final PostsFeed postsFeed = new PostsFeed(getContext());
                Query query;
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                if (position == 0)
                    query = reference.child("posts").orderByChild("time");
                else
                    query = reference.child("posts").orderByChild("likes");

                postsFeed.load(getFragmentManager(),getViewLifecycleOwner(), query, (position == 0));
                container.addView(postsFeed);

                final CompletionListener beforelistener = postUploadedListener;
                postUploadedListener = new CompletionListener() {
                    @Override
                    public void OnComplete() {
                        postsFeed.refresh();
                        if (beforelistener != null)
                            beforelistener.OnComplete();
                    }
                };


                return postsFeed;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                //No Destroy
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }
        });

        return root;
    }


    @Override
    public void onShow() {
        super.onShow();
        if (observer != null)
            GyanithUserManager.getCurrentUser().observeForever(observer);
    }

    @Override
    public void onHide() {
        super.onHide();
        if (observer != null)
            GyanithUserManager.getCurrentUser().removeObserver(observer);
    }

    private void syncViewPagerAndIconSwitch(final ViewPager viewPager, final IconSwitch iconSwitch){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    iconSwitch.setChecked(IconSwitch.Checked.LEFT);
                else
                    iconSwitch.setChecked(IconSwitch.Checked.RIGHT);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        iconSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {

                if (current == IconSwitch.Checked.RIGHT)
                    viewPager.setCurrentItem(1);
                else
                    viewPager.setCurrentItem(0);
            }
        });

    }
}

