package com.barebrains.gyanith20.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.barebrains.gyanith20.adapters.postsAdapter;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.others.PostViewHolder;
import com.barebrains.gyanith20.statics.NetworkManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static com.barebrains.gyanith20.others.Response.NO_DATA_AND_NET;

public class SpecificPostActivity extends AppCompatActivity {

    SwipeRefreshLayout refreshPost;
    Loader loader;
    PostViewHolder postViewHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_post);
        loader = findViewById(R.id.postLoader);
        postViewHolder = PostViewHolder.getHolder(loader,getSupportFragmentManager());
        loader.addView(postViewHolder.itemView);
        refreshPost = findViewById(R.id.refresh_post);
        findViewById(R.id.specific_post_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        handleIntent(getIntent());
    }

    private void handleIntent(final Intent intent){
        String action = intent.getAction();
        String data = intent.getDataString();
        loader.loading();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            final String postId = data.substring(data.lastIndexOf("/") + 1);

            DatabaseReference root = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference postRef = root.child("posts").child(postId);
            final ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!NetworkManager.internet_value) {
                        loader.error(NO_DATA_AND_NET);
                        return;
                    }

                    Post post = dataSnapshot.getValue(Post.class);
                    MutableLiveData<Post> livepost;
                    if (post == null) {
                        loader.error();
                        return;
                    }

                    if (postsAdapter.posts == null)
                        postsAdapter.posts = new HashMap<>();

                    if (postsAdapter.posts.containsKey(post.postId)) {
                        livepost = postsAdapter.posts.get(post.postId);
                        livepost.postValue(post);
                    } else {
                        livepost = new MutableLiveData<>();
                        postsAdapter.posts.put(post.postId, livepost);
                        livepost.postValue(post);
                    }

                    postViewHolder.bindto(livepost);
                    loader.loaded();
                    refreshPost.setRefreshing(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if (NetworkManager.internet_value == null || !NetworkManager.internet_value)
                        Toast.makeText(SpecificPostActivity.this, "No Internet", Toast.LENGTH_SHORT).show();

                    loader.error();
                }
            };
            postRef.addListenerForSingleValueEvent(listener);
            refreshPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleIntent(intent);
                }
            });
        }
        else {
            loader.error();
            refreshPost.setRefreshing(false);
        }
    }

}
