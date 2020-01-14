package com.barebrains.gyanith20.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.others.PostViewHolder;
import com.barebrains.gyanith20.statics.PostManager;

public class SpecificPostActivity extends AppCompatActivity {

    SwipeRefreshLayout refreshPost;
    Loader loader;
    PostViewHolder postViewHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_post);
        postViewHolder = new PostViewHolder(LayoutInflater.from(this).inflate(R.layout.view_post,loader));
        loader = findViewById(R.id.postLoader);
        loader.addView(postViewHolder.itemView,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
            PostManager.getSpecificPost(postId,new ResultListener<Post>() {
                @Override
                public void OnResult(Post post) {
                    if (post == null) {
                        loader.error();
                        refreshPost.setRefreshing(false);
                        return;
                    }

                    postViewHolder.SetPost(post);
                    loader.loaded();
                    refreshPost.setRefreshing(false);
                }

                @Override
                public void OnError(String error) {
                    loader.error();
                    if (error != null)
                        Toast.makeText(SpecificPostActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
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
