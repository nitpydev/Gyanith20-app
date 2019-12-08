package com.barebrains.gyanith20.Others;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.barebrains.gyanith20.Adapters.postImagesAdaptor;
import com.barebrains.gyanith20.Models.Post;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.Statics.PostManager;
import com.barebrains.gyanith20.Statics.Util;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

public class PostViewHolder extends RecyclerView.ViewHolder {

    private TextView userName;
    private TextView captions;
    private TextView likeCount;
    private View likeBtn;
    private View shareBtn;
    private ViewPager viewPager;
    private SpringDotsIndicator dotsIndicator;
    private postImagesAdaptor adaptor;
    private Bitmap[] bitmaps;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        userName = itemView.findViewById(R.id.post_username);
        captions = itemView.findViewById(R.id.post_captions_text);
        likeCount = itemView.findViewById(R.id.post_likecount);
        likeBtn = itemView.findViewById(R.id.like_btn);
        shareBtn = itemView.findViewById(R.id.share_btn);
        viewPager = itemView.findViewById(R.id.post_img_viewpager);
        dotsIndicator = itemView.findViewById(R.id.dots);
    }

    public void FillPost(Context context, Post post){
        userName.setText(post.username);
        captions.setText(post.caption);
        likeCount.setText(Long.toString(post.likes));
        bitmaps = new Bitmap[post.imgIds.size()];
        adaptor = new postImagesAdaptor(context,bitmaps);
        viewPager.setAdapter(adaptor);
        dotsIndicator.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(bitmaps.length -1);
        Pair<Integer,String>[] imgIdsMap = Util.arrayToPair(post.imgIds);
        for (final Pair<Integer,String> imgId : imgIdsMap)
        {
            PostManager.getPostImage(context, imgId.second, new PostManager.Callback<Bitmap>() {
                @Override
                public void OnResult(Bitmap bitmap) {
                    if (bitmap == null)
                    Log.d("asd","bitmap is null");
                    bitmaps[imgId.first] = bitmap;
                    adaptor.UpdatePosition(viewPager,imgId.first);
                }
            });
        }


    }

}
