package com.barebrains.gyanith20.statics;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.activities.MainActivity;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import static com.barebrains.gyanith20.statics.Util.decrementer;

public class  PostManager{
    public static void deletePost(Post post, final CompletionListener callback){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference postRef = rootRef.child("posts").child(post.postId);
        postRef.removeValue();
        DatabaseReference userPostRef = rootRef.child("users").child(post.gyanithId).child("posts").child(post.postId);
        userPostRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    callback.OnComplete();
                else
                    callback.OnError("Could'nt Delete Post");
            }
        });

        rootRef.child("postCount").runTransaction(decrementer);
        rootRef.child("users").child(post.gyanithId).child("postCount").runTransaction(decrementer);
        for (String imgId : post.imgIds)
            FirebaseStorage.getInstance().getReference().child("PostImages").child(imgId).delete();
    }



    public static Integer postCount = 0;

    public static void StartListeningPostCount(){
        FirebaseDatabase.getInstance().getReference().child("postCount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postCount =  dataSnapshot.getValue(Integer.class);
                if (MainActivity.botNav != null)
                    MainActivity.botNav.updateCount(4,postCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


