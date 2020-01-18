package com.barebrains.gyanith20.others;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.AnimatedToggle;
import com.barebrains.gyanith20.components.ImageSlider;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.fragments.botSheet;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.statics.Anim;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.LikesSystem;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.barebrains.gyanith20.statics.PostManager;
import com.barebrains.gyanith20.statics.Util;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import static android.view.View.VISIBLE;
import static com.barebrains.gyanith20.gyanith20.appContext;
import static com.barebrains.gyanith20.others.Response.DATA_EMPTY;

public class PostViewHolder extends RecyclerView.ViewHolder{

    private static final long DELETE_RESPONSE_DELAY = 1500;

    //Data UI
    private TextView usernameTxt;
    private TextView likeCountText;
    private TextView captionsText;
    private TextView bottomCaptionsText;
    private TextView timestampText;

    //Interactive UI
    private AnimatedToggle likeBtn;
    private View shareBtn;
    private View tapPanel;

    //FunctionalUI
    private ImageSlider imgSlider;
    private Loader loader;


    //Private Variables
    private Post post;
    private MutableLiveData<Post> livePost;
    private Long likedVal = null;
    private Long unlikedVal = null;

    //Observers
    private Observer<Post> postObserver;
    private Observer<Resource<GyanithUser>> authObserver;
    private Observer<List<String>> likesObserver;

    private View.OnLongClickListener longClickListener;

    private FragmentManager fragmentManager;

    public static PostViewHolder getHolder(ViewGroup parent,FragmentManager fragmentManager){
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post,parent,false);
        return new PostViewHolder(item,fragmentManager);
    }


    private PostViewHolder(@NonNull final View itemView, FragmentManager fragmentManager) {
        super(itemView);

        this.fragmentManager = fragmentManager;

        //Getting View References
        usernameTxt = itemView.findViewById(R.id.post_username);
        likeCountText = itemView.findViewById(R.id.post_likecount);
        captionsText = itemView.findViewById(R.id.post_captions_text);
        bottomCaptionsText = itemView.findViewById(R.id.post_bottom_caption_txt);
        timestampText = itemView.findViewById(R.id.post_timestamp_txt);
        imgSlider = itemView.findViewById(R.id.post_img_slider);
        shareBtn = itemView.findViewById(R.id.share_btn);
        tapPanel = itemView.findViewById(R.id.tap_panel);
        likeBtn = itemView.findViewById(R.id.like_img);
        loader = itemView.findViewById(R.id.post_loader);
        //Statics
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post == null)
                    return;
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Gyanith Community Post");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, buildDeepLink(post.postId));
                view.getContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
        clear();

        loader.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (longClickListener != null)
                    longClickListener.onLongClick(v);
                return true;
            }
        });

        imgSlider.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibe = (Vibrator) itemView.getContext().getSystemService(Context.VIBRATOR_SERVICE) ;
                vibe.vibrate(50); // 50 is time in ms

                if (longClickListener != null)
                    longClickListener.onLongClick(v);

                return true;
            }
        });
    }


    public void bindto(MutableLiveData<Post> postRecent) {
        clear();

        livePost = postRecent;

        postObserver = new Observer<Post>() {
            @Override
            public void onChanged(final Post post) {
                loader.loaded();
                if (post == null)//POST DELETED
                {
                    clear();
                    loader.error(DATA_EMPTY);
                    return;
                }

                PostViewHolder.this.post = post;
                ConsumeData(post);

                authObserver = new Observer<Resource<GyanithUser>>() {
                    @Override
                    public void onChanged(final Resource<GyanithUser> res) {

                        if (likesObserver != null) {
                            LikesSystem.likedPosts.removeObserver(likesObserver);
                            likesObserver = null;
                        }

                        if (res.value == null){//NO USER STATE
                            likedVal = null;
                            unlikedVal = null;
                            likeBtn.setSafeChecked(false);
                            likeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    Toast.makeText(buttonView.getContext(),"Sign in to Like Posts", Toast.LENGTH_SHORT).show();
                                    if (likeBtn.isChecked())
                                    likeBtn.setSafeChecked(false);
                                }
                            });
                            longClickListener = new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    Toast.makeText(appContext, "Sign in to Delete Post!", Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                            };
                        }
                        else {
                            try {

                               likesObserver = new Observer<List<String>>() {
                                   @Override
                                   public void onChanged(List<String> postIds) {
                                       boolean isLiked;
                                       if (postIds != null) {
                                           isLiked = postIds.contains(post.postId);

                                           if (likedVal == null || unlikedVal == null){
                                               likedVal = isLiked ? post.likes : post.likes - 1;
                                               unlikedVal = isLiked ? post.likes + 1 : post.likes;
                                           }
                                       } else {
                                           likedVal = null;
                                           unlikedVal = null;
                                           isLiked = false;
                                       }
                                       setLikeState(isLiked);
                                   }
                               };

                               LikesSystem.likedPosts.observeForever(likesObserver);

                                likeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                        if (NetworkManager.internet_value != null && NetworkManager.internet_value)
                                            LikesSystem.ToggleLikeState(post.postId,b);
                                        else
                                            Toast.makeText(compoundButton.getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                longClickListener = new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        if (res.value.gyanithId.equals(post.gyanithId))
                                            deletePost();
                                        else
                                            Toast.makeText(appContext, "Only @" + post.username + " can delete this post !", Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                };
                            }catch (IllegalStateException e){
                                // Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                            }
                        }

                        imgSlider.load(Util.getStorageRefs(post.imgIds, FirebaseStorage.getInstance().getReference().child("PostImages")))
                                .start();
                        if (post.caption != null && !post.caption.isEmpty()) {
                            imgSlider.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    captionStateToggle();
                                }
                            });
                        }
                    }
                };

                GyanithUserManager.getCurrentUser().observeForever(authObserver);
            }
        };

        livePost.observeForever(postObserver);
    }

    public void clear(){
        loader.loading();

        if (authObserver != null) {
            GyanithUserManager.getCurrentUser().removeObserver(authObserver);
            authObserver = null;
        }

        if (livePost != null && postObserver != null) {
            livePost.removeObserver(postObserver);
            livePost = null;
            postObserver = null;
        }

        if (likesObserver != null){
            LikesSystem.likedPosts.removeObserver(likesObserver);
            likesObserver = null;
        }

        if (longClickListener != null)
            longClickListener = null;


        imgSlider.setOnClickListener(null);

        if (tapPanel.getVisibility() == VISIBLE)
            captionStateToggle();

        likeBtn.setOnCheckedChangeListener(null);
        likeBtn.setChecked(false);

        post = null;
    }


    private void ConsumeData(Post post){
        usernameTxt.setText("@" + post.username);
        likeCountText.setText((post.likes != 0) ? Long.toString(post.likes).substring(1) : "0");
        captionsText.setText(post.caption);
        bottomCaptionsText.setText(post.caption);
        timestampText.setText(Util.BuildTimeAgoString(post.time));
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

    private void setLikeState(boolean isliked){
        long likes;

        if (likedVal != null && unlikedVal != null) {
            if (isliked)
                likes = likedVal;
            else
                likes = unlikedVal;
        }
        else
            likes = post.likes;
        likeCountText.setText((likes != 0) ? Long.toString(likes).substring(1) : "0");
        likeBtn.setSafeChecked(isliked);
    }

    private void deletePost(){
        final botSheet deletePrompt = botSheet.makeBotSheet(fragmentManager);
        deletePrompt.setTitle("Delete Post")
                .setBody("Are you sure want to delete this post ?")
                .setAction("DELETE")
                .setActionListener(new CompletionListener(){
                    @Override
                    public void OnComplete() {
                        deletePrompt.dismiss();
                        final botSheet deletingPrompt = botSheet.makeBotSheet(fragmentManager)
                                .setTitle("Delete Post")
                                .setBody("Deleting Post ...")
                                .show();

                        PostManager.deletePost(post,new CompletionListener(){
                            @Override
                            public void OnComplete() {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        deletingPrompt.dismiss();
                                        Toast.makeText(appContext, "Post Deleted !", Toast.LENGTH_SHORT).show();
                                        if (livePost != null)
                                            livePost.postValue(null);
                                    }
                                },DELETE_RESPONSE_DELAY);
                            }

                            @Override
                            public void OnError(String error) {
                                deletingPrompt.dismiss();
                                botSheet.makeBotSheet(fragmentManager)
                                        .setTitle("Delete Post")
                                        .setBody("Could'nt Delete Post !")
                                        .show();
                            }
                        });
                    }
                }).show();
    }
}
