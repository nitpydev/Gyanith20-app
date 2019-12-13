package com.barebrains.gyanith20.Others;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.barebrains.gyanith20.Activities.MainActivity;
import com.barebrains.gyanith20.Adapters.postImagesAdaptor;
import com.barebrains.gyanith20.Models.Post;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.Statics.PostManager;
import com.barebrains.gyanith20.Statics.Util;
import com.google.android.material.card.MaterialCardView;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

public class PostViewHolder extends RecyclerView.ViewHolder {

    private Context context;
    private TextView userName;
    private TextView captions;
    private TextView likeCount;
    private ImageView likeIcon;
    private MaterialCardView likeBtn;
    private MaterialCardView shareBtn;
    private MaterialCardView userProfileBtn;
    private ViewPager viewPager;
    private SpringDotsIndicator dotsIndicator;
    private postImagesAdaptor adaptor;
    private Bitmap[] bitmaps;
    private View postTopBar;
    private View postBottomBar;

    private boolean likeState;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        userName = itemView.findViewById(R.id.post_username);
        captions = itemView.findViewById(R.id.post_captions_text);
        likeCount = itemView.findViewById(R.id.post_likecount);
        likeBtn = itemView.findViewById(R.id.like_btn);
        shareBtn = itemView.findViewById(R.id.share_btn);
        viewPager = itemView.findViewById(R.id.post_img_viewpager);
        dotsIndicator = itemView.findViewById(R.id.dots);
        likeIcon = itemView.findViewById(R.id.like);
        userProfileBtn = itemView.findViewById(R.id.user_profile_btn);
        postTopBar = itemView.findViewById(R.id.post_top_bar);
        postBottomBar = itemView.findViewById(R.id.bottomBar);
    }

    public void FillPost(final MainActivity mainActivity, final Post post){
        this.context = mainActivity;
        postBottomBar.setVisibility(View.VISIBLE);
        postTopBar.setVisibility(View.VISIBLE);
        userName.setText(post.username);
        captions.setText(post.caption);
        //Changing Variables
        likeCount.setText((post.likes != 0)?Long.toString(post.likes).substring(1):"0");
        bitmaps = new Bitmap[post.imgIds.size()];
        adaptor = new postImagesAdaptor(mainActivity,bitmaps);
        viewPager.setAdapter(adaptor);
        dotsIndicator.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(bitmaps.length -1);
        Pair<Integer,String>[] imgIdsMap = Util.arrayToPair(post.imgIds);
        for (final Pair<Integer,String> imgId : imgIdsMap)
        {
            PostManager.getPostImage(mainActivity, imgId.second, new PostManager.Callback<Bitmap>() {
                @Override
                public void OnResult(Bitmap bitmap) {
                    if (bitmap == null)
                    Log.d("asd","bitmap is null");
                    bitmaps[imgId.first] = bitmap;
                    adaptor.UpdatePosition(viewPager,imgId.first);
                }
            });
        }
        ToggleLikeIcon(false);

        if (PostManager.getInstance().isLiked(post.postId)){
            ToggleLikeIcon(true);
            likeState = true;
        }

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeState = !likeState;
             if (likeState){
                 ToggleLikeIcon(true);
                 post.likes--;
                 likeCount.setText(String.valueOf(-post.likes));
                 PostManager.getInstance().likePost(post.postId);
             }
             else {
                 ToggleLikeIcon(false);
                 post.likes++;
                 likeCount.setText(String.valueOf(-post.likes));
                 PostManager.getInstance().dislikePost(post.postId);
             }
            }
        });

        userProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show User Profile
            }
        });

    }

    private void ToggleLikeIcon(boolean state)
    {
        if (!state)
        {
            likeIcon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_favorite_border_black_24dp));
        }
        else
        {
            likeIcon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_favorite_24px));
        }
    }

}
