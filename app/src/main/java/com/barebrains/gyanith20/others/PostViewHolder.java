package com.barebrains.gyanith20.others;

import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.AnimatedToggle;
import com.barebrains.gyanith20.components.ImageSlider;
import com.barebrains.gyanith20.interfaces.AuthStateListener;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.statics.Anim;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.LikesSystem;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.barebrains.gyanith20.statics.PostManager;
import com.barebrains.gyanith20.statics.Util;
import com.google.firebase.storage.FirebaseStorage;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PostViewHolder extends RecyclerView.ViewHolder{
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
    private ImageSlider imgSlider;
    private View postContent;
    private View deletedView;
    private View deletingProg;


    //Private Variables
    private Post post;
    private AuthStateListener authStateListener;
    private ResultListener<String> likeRefreshListener;
    private long likedVal;
    private long unlikedVal;


    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        init();
    }


    private void init(){
        //Getting View References
        usernameTxt = itemView.findViewById(R.id.post_username);
        likeCountText = itemView.findViewById(R.id.post_likecount);
        captionsText = itemView.findViewById(R.id.post_captions_text);
        bottomCaptionsText = itemView.findViewById(R.id.post_bottom_caption_txt);
        timestampText = itemView.findViewById(R.id.post_timestamp_txt);
        imgSlider = itemView.findViewById(R.id.post_img_slider);
        shareBtn = itemView.findViewById(R.id.share_btn);
        tapPanel = itemView.findViewById(R.id.tap_panel);
        deleteBtn = itemView.findViewById(R.id.post_delete_btn);
        postContent = itemView.findViewById(R.id.post);
        deletedView = itemView.findViewById(R.id.deleted);
        likeBtn = itemView.findViewById(R.id.like_img);
        deletingProg = itemView.findViewById(R.id.delete_prog);
    }

    public void SetPost(Post initalPost) {
        //Reset this view to avoid conflicts with recycling of view_holder
        resetPostView();
        post = initalPost;
        SetupDataUI();
        imgSlider.load(Util.getStorageRefs(post.imgIds, FirebaseStorage.getInstance().getReference().child("PostImages")))
                .start();
        imgSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captionStateToggle();
            }
        });
        SetupInteractiveUI();
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
        likeCountText.setText((post.likes != 0) ? Long.toString(post.likes).substring(1) : "0");
        captionsText.setText(post.caption);
        bottomCaptionsText.setText(post.caption);
        timestampText.setText(Util.BuildTimeAgoString(post.time));
    }


    private void SetupInteractiveUI(){

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Gyanith Community Post");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, buildDeepLink(post.postId));
                view.getContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        //Below are the Auth state aware Statements
        authStateListener = new AuthStateListener(){
            @Override
            public void onChange() {
                deleteBtn.setVisibility(GONE);
                likeCountText.setText((post.likes != 0) ? Long.toString(post.likes).substring(1) : "0");
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
                    final boolean isliked = LikesSystem.isLiked(post.postId);

                    likedVal = isliked ? post.likes : post.likes - 1;
                    unlikedVal = isliked ? post.likes + 1 : post.likes;
                    likeBtn.setChecked(isliked);

                    likeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            LikesSystem.ToggleLikeState(post.postId,b);
                        }
                    });

                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            if (!NetworkManager.getInstance().isNetAvailable()) {
                                Toast.makeText(view.getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            deletingProg.setVisibility(VISIBLE);
                            view.setVisibility(GONE);
                            PostManager.deletePost(post, new CompletionListener() {
                                @Override
                                public void OnComplete() {
                                    deletedView.setVisibility(VISIBLE);
                                    postContent.setVisibility(GONE);
                                }

                                @Override
                                public void OnError(String error) {
                                    Toast.makeText(view.getContext(), "Could'nt Delete Post", Toast.LENGTH_SHORT).show();
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
                            if (postId.equals(post.postId)) {
                                boolean isLiked = LikesSystem.isLiked(postId);
                                likeBtn.setChecked(isliked);
                            }
                        }
                    };
                    LikesSystem.listeners.add(likeRefreshListener);

                }catch (IllegalStateException e){
                   // Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(compoundButton.getContext(),"Sign in to Like Posts", Toast.LENGTH_SHORT).show();
                        compoundButton.setChecked(false);
                    }
                });
            }
        };

        GyanithUserManager.addAuthStateListener(authStateListener);

    }

    private String buildDeepLink(String postId){
        return "http://gyanith.com/post/" + postId;
    }

    private void captionStateToggle(){
        if (tapPanel.getVisibility() == VISIBLE) {
            Anim.crossfade(tapPanel, imgSlider,0f,350);
            Anim.zoom(tapPanel,1f, 0.7f,450);
        } else {
            Anim.crossfade(imgSlider,tapPanel,0.3f,350);
            Anim.zoom(tapPanel,0.7f,1f,400);
        }
    }

    private void setLikeCount(boolean b){
        long likes = (b) ? likedVal : unlikedVal;
        likeCountText.setText((likes != 0) ? Long.toString(likes).substring(1) : "0");
    }
}
