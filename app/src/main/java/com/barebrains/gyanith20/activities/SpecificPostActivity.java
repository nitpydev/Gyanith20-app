package com.barebrains.gyanith20.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.components.PostView;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.statics.PostManager;

public class SpecificPostActivity extends AppCompatActivity {

    Loader loader;
    PostView postView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_post);
        loader = findViewById(R.id.postLoader);
        postView = findViewById(R.id.specific_post);
        handleIntent(getIntent());

    }

    private void handleIntent(final Intent intent){
        String action = intent.getAction();
        String data = intent.getDataString();
        loader.loading();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            final String postId = data.substring(data.lastIndexOf("/") + 1);
            GetPost(postId,new ResultListener<Post>() {
                @Override
                public void OnResult(Post post) {
                    if (post == null) {
                        loader.error(0);
                        return;
                    }

                    postView.SetPost(SpecificPostActivity.this,post);
                    loader.loaded();
                }
            });
            findViewById(R.id.refresh_post).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleIntent(intent);
                }
            });
        }
    }

    private void GetPost(String postId,ResultListener<Post> listener){
        PostManager.getSpecificPost(postId, listener);
    }


}
