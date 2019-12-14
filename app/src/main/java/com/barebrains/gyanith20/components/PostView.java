package com.barebrains.gyanith20.components;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.barebrains.gyanith20.adapters.postImagesAdaptor;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.statics.PostManager;
import com.barebrains.gyanith20.statics.Util;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

public class PostView extends RelativeLayout {

    //Text Views for Assignment
    private TextView usernameTxt;
    private TextView likeCountText;
    private TextView captionsText;
    private TextView bottomCaptionsText;
    private TextView timestampText;

    //ImageViews for Assignment
    private ImageView profileImg;
    private ClickableViewPager viewPager;
    private ImageView likeIcon;

    //Views for Functions
    private SpringDotsIndicator dotsIndicator;
    private View likeBtn;
    private View shareBtn;
    private View profileBtn;


    //Other Resources
    private Drawable likedDrawable;
    private Drawable unlikedDrawable;


    private boolean likeState;
    private Post post;

    public PostView(Context context) {
        super(context);
        init(context);
    }

    public PostView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PostView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PostView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_post,this,true);

        //Getting View References
        usernameTxt = findViewById(R.id.post_username);
        likeCountText = findViewById(R.id.post_likecount);
        captionsText = findViewById(R.id.post_captions_text);
        bottomCaptionsText = findViewById(R.id.post_bottom_caption_txt);
        timestampText = findViewById(R.id.post_timestamp_txt);
        profileImg = findViewById(R.id.post_profile_img);
        viewPager = findViewById(R.id.post_img_viewpager);
        likeBtn = findViewById(R.id.like_btn);
        shareBtn = findViewById(R.id.share_btn);
        profileBtn = findViewById(R.id.profile_btn);

        dotsIndicator = findViewById(R.id.dots);
        likeIcon = findViewById(R.id.like_img);

        likedDrawable = ContextCompat.getDrawable(context,R.drawable.ic_baseline_favorite_24px);
        unlikedDrawable = ContextCompat.getDrawable(context,R.drawable.ic_favorite_border_black_24dp);
    }

    public void SetPost(final Context context, Post initalPost) {
        post = initalPost;
        usernameTxt.setText(post.username);
        likeCountText.setText((post.likes != 0) ? Long.toString(post.likes).substring(1) : "0");
        captionsText.setText(post.caption);
        bottomCaptionsText.setText(post.caption);
        timestampText.setText(Util.BuildTimeAgoString(post.time));
        setUpViewPager(context, post.imgIds.toArray());
        captionsText.setVisibility(GONE);
        viewPager.setOnViewPagerClickListener(new ClickableViewPager.OnClickListener() {
            @Override
            public void onViewPagerClick(ViewPager viewPager) {
                captionStateToggle();
            }
        });

        if (PostManager.getInstance().isLiked(post.postId)) {
            setLikeIcon(true);
            likeState = true;
        } else{
            setLikeIcon(false);
            likeState = false;
        }


        likeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!likeState)
                    PostManager.getInstance().likePost(post.postId);
                else
                    PostManager.getInstance().dislikePost(post.postId);
            }
        });

        shareBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Gyanith Community Post");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, buildDeepLink(post.postId));
                context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
    }

    public PostManager.OnLikeStateChangedListener getLikeChangedListener(){
        return new PostManager.OnLikeStateChangedListener() {
            @Override
            public void OnChange(boolean state) {
                if (state){
                    setLikeIcon(true);
                    likeState = true;
                    post.likes--;
                    likeCountText.setText(String.valueOf(-post.likes));
                }
                else {
                    setLikeIcon(false);
                    likeState = false;
                    post.likes++;
                    likeCountText.setText(String.valueOf(-post.likes));
                }
            }
        };
    }



    private String buildDeepLink(String postId){
        return "http://gyanith.com/post/" + postId;
    }

    private void setUpViewPager(Context context,Object[] imgIds){
        final Bitmap[] bitmaps = new Bitmap[imgIds.length];
        viewPager.setAdapter(new postImagesAdaptor(context,bitmaps));
        viewPager.setOffscreenPageLimit(bitmaps.length -1);
        dotsIndicator.setViewPager(viewPager);
        for (int i = 0; i < bitmaps.length;i++)
            PostManager.getPostImage(context, i, (String) imgIds[i], new PostManager.Callback2<Bitmap, Integer>() {
                @Override
                public void OnResult(Bitmap bitmap, Integer integer) {
                    bitmaps[integer] = bitmap;
                    ((postImagesAdaptor)viewPager.getAdapter()).UpdatePosition(viewPager,integer);
                }
            });
    }

    private void captionStateToggle(){
        if (captionsText.getVisibility() == View.VISIBLE)
            viewPager.setAlpha(1f);
        else
            viewPager.setAlpha(0.3f);

        captionsText.setVisibility((captionsText.getVisibility() == View.VISIBLE)?GONE:VISIBLE);
    }

    private void setLikeIcon(boolean state)
    {
        if (state)
        {
            likeIcon.setImageDrawable(likedDrawable);
        }
        else
        {
            likeIcon.setImageDrawable(unlikedDrawable);
        }
    }
}
