package com.barebrains.gyanith20.CustomComponents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.telecom.Call;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.barebrains.gyanith20.Adapters.postImagesAdaptor;
import com.barebrains.gyanith20.Models.Post;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.Statics.PostManager;
import com.barebrains.gyanith20.Statics.Util;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

public class PostView extends RelativeLayout {

    //Text Views for Assignment
    private TextView usernameTxt;
    private TextView likeCountText;
    private TextView captionsText;
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
    private View userProfile;


    //Other Resources
    private Drawable likedDrawable;
    private Drawable unlikedDrawable;


    private boolean likeState;

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
        timestampText = findViewById(R.id.post_timestamp_txt);
        profileImg = findViewById(R.id.post_profile_img);
        viewPager = findViewById(R.id.post_img_viewpager);
        likeBtn = findViewById(R.id.like_btn);
        shareBtn = findViewById(R.id.share_btn);
        profileBtn = findViewById(R.id.profile_btn);

        dotsIndicator = findViewById(R.id.dots);
        likeIcon = findViewById(R.id.like_img);
        userProfile = findViewById(R.id.user_profile);

        likedDrawable = ContextCompat.getDrawable(context,R.drawable.ic_baseline_favorite_24px);
        unlikedDrawable = ContextCompat.getDrawable(context,R.drawable.ic_favorite_border_black_24dp);
    }

    public void SetPost(Context context, final Post post){
        usernameTxt.setText(post.username);
        likeCountText.setText((post.likes != 0)?Long.toString(post.likes).substring(1):"0");
        captionsText.setText(post.caption);
        timestampText.setText(Util.BuildTimeAgoString(post.time));
        setUpViewPager(context,post.imgIds.toArray());
        captionsText.setVisibility(GONE);
        viewPager.setOnViewPagerClickListener(new ClickableViewPager.OnClickListener() {
            @Override
            public void onViewPagerClick(ViewPager viewPager) {
                captionStateToggle();
            }
        });

        if (PostManager.getInstance().isLiked(post.postId)){
            setLikeIcon(true);
            likeState = true;
        }


        likeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                likeState = !likeState;
                if (likeState){
                    setLikeIcon(true);
                    post.likes--;
                    likeCountText.setText(String.valueOf(-post.likes));
                    PostManager.getInstance().likePost(post.postId);
                }
                else {
                    setLikeIcon(false);
                    post.likes++;
                    likeCountText.setText(String.valueOf(-post.likes));
                    PostManager.getInstance().dislikePost(post.postId);
                }
            }
        });
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
            viewPager.setAlpha(0.5f);

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
