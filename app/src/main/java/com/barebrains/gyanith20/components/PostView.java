package com.barebrains.gyanith20.components;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Network;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import com.barebrains.gyanith20.adapters.postImagesAdaptor;
import com.barebrains.gyanith20.interfaces.AuthStateListener;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.NetworkStateListener;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.statics.Anim;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.LikesSystem;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.barebrains.gyanith20.statics.PostManager;
import com.barebrains.gyanith20.statics.Util;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

public class PostView extends RelativeLayout {

    //Data UI
    private TextView usernameTxt;
    private TextView likeCountText;
    private TextView captionsText;
    private TextView bottomCaptionsText;
    private TextView timestampText;

    //Interactive UI
    private AnimatedToggle likeBtn;
    private View shareBtn;
    private View deleteBtn;
    private View tapPanel;

    //FunctionalUI
    private ClickableViewPager viewPager;
    private SpringDotsIndicator dotsIndicator;
    private View postContent;
    private View deletedView;
    private View deletingProg;

    //Private Variables
    private Post post;
    private AuthStateListener authStateListener;
    private ResultListener<String> likeRefreshListener;

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

    private void init(final Context context){
        LayoutInflater.from(context).inflate(R.layout.view_post,this,true);

        //Getting View References
        usernameTxt = findViewById(R.id.post_username);
        likeCountText = findViewById(R.id.post_likecount);
        captionsText = findViewById(R.id.post_captions_text);
        bottomCaptionsText = findViewById(R.id.post_bottom_caption_txt);
        timestampText = findViewById(R.id.post_timestamp_txt);
        viewPager = findViewById(R.id.post_img_viewpager);
        shareBtn = findViewById(R.id.share_btn);
        tapPanel = findViewById(R.id.tap_panel);
        deleteBtn = findViewById(R.id.post_delete_btn);
        postContent = findViewById(R.id.post);
        deletedView = findViewById(R.id.deleted);
        dotsIndicator = findViewById(R.id.dots);
        likeBtn = findViewById(R.id.like_img);
        deletingProg = findViewById(R.id.delete_prog);

    }

    public void SetPost(Context context, Post initalPost) {
        //Reset this view to avoid conflicts with recycling of view_holder
        resetPostView();
        post = initalPost;
        SetupDataUI();
        setUpViewPager(context, post.imgIds.toArray(new String[0]));
        SetupInteractiveUI(context);
    }

    private void resetPostView(){
        postContent.setVisibility(VISIBLE);
        deletedView.setVisibility(GONE);
        if (authStateListener != null) {
            GyanithUserManager.removeAuthStateListener(authStateListener);
            authStateListener = null;
        }

        if(likeRefreshListener != null) {
            LikesSystem.listeners.remove(likeRefreshListener);
            likeRefreshListener = null;
        }
        if (tapPanel.getVisibility() == VISIBLE)
            captionStateToggle();
        deletingProg.setVisibility(GONE);
    }


    private void SetupDataUI(){
        usernameTxt.setText(post.username);
        setLikeCount(post.likes);
        captionsText.setText(post.caption);
        bottomCaptionsText.setText(post.caption);
        timestampText.setText(Util.BuildTimeAgoString(post.time));
    }


    private void SetupInteractiveUI(final Context context){
        viewPager.setOnViewPagerClickListener(new ClickableViewPager.OnClickListener() {
            @Override
            public void onViewPagerClick(ViewPager viewPager) {
                captionStateToggle();
            }
        });

        shareBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("body/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Gyanith Community Post");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, buildDeepLink(post.postId));
                context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        //Below are the Auth state aware Statements
        authStateListener = new AuthStateListener(){
            @Override
            public void onChange() {
                deleteBtn.setVisibility(GONE);
                setLikeCount(post.likes);
                likeBtn.setOnCheckedChangeListener(null);
                deleteBtn.setOnClickListener(null);
            }

            @Override
            public void VerifiedUser() {
                try {

                    //If we are owner of post show delete btn
                    if (post.gyanithId.equals(GyanithUserManager.getCurrentUser().gyanithId))
                        deleteBtn.setVisibility(VISIBLE);

                    //Initial Likes Setup
                    boolean isliked = LikesSystem.isLiked(post.postId);
                    final long likedVal = isliked ? post.likes : post.likes - 1;
                    final long unlikedVal = isliked ? post.likes + 1 : post.likes;
                    likeBtn.setChecked(isliked);

                    likeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            long likes = (b) ? likedVal : unlikedVal;
                            setLikeCount(likes);
                        }
                    });

                    deleteBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            if (!NetworkManager.getInstance().isNetAvailable()) {
                                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            findViewById(R.id.delete_prog).setVisibility(VISIBLE);
                            view.setVisibility(GONE);
                            PostManager.deletePost(post, new CompletionListener() {
                                @Override
                                public void OnComplete() {
                                    deletedView.setVisibility(VISIBLE);
                                    postContent.setVisibility(GONE);
                                }

                                @Override
                                public void OnError(String error) {
                                    Toast.makeText(context, "Could'nt Delete Post", Toast.LENGTH_SHORT).show();
                                    deletingProg.setVisibility(GONE);
                                    view.setVisibility(VISIBLE);
                                }
                            });
                        }
                    });

                    //Refreshing Likes Listener
                    likeRefreshListener = new ResultListener<String>(){
                        @Override
                        public void OnResult(String postId) {
                            if (postId.equals(post.postId))
                                likeBtn.setChecked(LikesSystem.isLiked(postId));
                        }
                    };
                    LikesSystem.listeners.add(likeRefreshListener);

                }catch (IllegalStateException e){
                    Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void NullUser() {
                if (likeRefreshListener != null) {
                    LikesSystem.listeners.remove(likeRefreshListener);
                    likeRefreshListener = null;
                }
                likeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Toast.makeText(getContext(),"Sign in to Like Posts", Toast.LENGTH_SHORT).show();
                        compoundButton.setChecked(false);
                    }
                });
            }
        };

        GyanithUserManager.addAuthStateListener(authStateListener);

    }

    private void setUpViewPager(Context context,String[] imgIds){
        final Bitmap[] bitmaps = new Bitmap[imgIds.length];
        viewPager.setAdapter(new postImagesAdaptor(context,bitmaps));
        viewPager.setOffscreenPageLimit(bitmaps.length -1);
        dotsIndicator.setVisibility((bitmaps.length > 1)?VISIBLE:GONE);
        dotsIndicator.setViewPager(viewPager);
        for (int i = 0; i < bitmaps.length;i++)
            PostManager.getPostImage(context, i, imgIds[i], new ResultListener<Pair<Bitmap, Integer>>() {
                @Override
                public void OnResult(Pair<Bitmap,Integer> result) {
                    bitmaps[result.second] = result.first;
                    ((postImagesAdaptor)viewPager.getAdapter()).UpdatePosition(viewPager,result.second);
                }
            });
    }

    private String buildDeepLink(String postId){
        return "http://gyanith.com/post/" + postId;
    }

    private void captionStateToggle(){
        if (tapPanel.getVisibility() == View.VISIBLE) {
            Anim.crossfade(tapPanel,viewPager,0f,350);
            Anim.zoom(tapPanel,1f, 0.7f,450);
        } else {
            Anim.crossfade(viewPager,tapPanel,0.3f,350);
            Anim.zoom(tapPanel,0.7f,1f,400);
        }
    }

    private void setLikeCount(Long likes){
        likeCountText.setText((likes != 0) ? Long.toString(likes).substring(1) : "0");
    }
}
