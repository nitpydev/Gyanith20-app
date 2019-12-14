package com.barebrains.gyanith20.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.barebrains.gyanith20.components.PostView;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.statics.PostManager;

public class SpecificPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_post);
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent){
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            final String postId = data.substring(data.lastIndexOf("/") + 1);
            GetPost(postId);
            findViewById(R.id.refresh_post).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GetPost(postId);
                }
            });
        }
    }

    private void GetPost(String postId){
        PostManager.getSpecificPost(postId, new PostManager.Callback<Post>() {
            @Override
            public void OnResult(Post post) {
                PostView postView = findViewById(R.id.specific_post);
                postView.SetPost(SpecificPostActivity.this,post);
            }
        });
    }


}
