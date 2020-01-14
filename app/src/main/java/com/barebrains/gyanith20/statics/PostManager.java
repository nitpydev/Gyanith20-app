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
    public static void getPostImage(Context context,final int requestId, final String imgId, final ResultListener<Pair<Bitmap,Integer>> callback){
        final SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.package_name),Context.MODE_PRIVATE);
        String imgPath = sp.getString(imgId,"");
       if (!imgPath.equals("")){
           Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
           if (bitmap != null) {
               callback.OnResult(new Pair<>(bitmap,requestId));
               return;
           }
       }

       StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("PostImages").child(imgId);
       final File imgFile = new File(context.getCacheDir(),imgId);
       storageReference.getFile(imgFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
               if (!task.isSuccessful()) {
                   Log.d("asd","e : " + task.getException().getLocalizedMessage());
                   callback.OnResult(new Pair<Bitmap, Integer>(null,requestId));
                   return;
               }
               sp.edit().putString(imgId,imgFile.getAbsolutePath()).apply();
               callback.OnResult(new Pair<>(BitmapFactory.decodeFile(imgFile.getAbsolutePath()),requestId));
           }
       });
    }

    public static void getSpecificPost(String postId, final ResultListener<Post> callback){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference postRef = root.child("posts").child(postId);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (NetworkManager.getInstance().isNetAvailable())
                callback.OnResult(dataSnapshot.getValue(Post.class));
                else
                    callback.OnComplete(dataSnapshot.getValue(Post.class),"No Internet");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.OnError(null);
            }
        });
    }

    private static ArrayList<String> deletedPosts = new ArrayList<>();

    public static void deletePost(Post post, final CompletionListener callback){
        if (deletedPosts.contains(post.postId))
            return;

        deletedPosts.add(post.postId);
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
                    callback.OnError("Error");
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


