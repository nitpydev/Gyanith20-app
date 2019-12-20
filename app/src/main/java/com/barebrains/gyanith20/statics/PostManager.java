package com.barebrains.gyanith20.statics;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.Post;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class  PostManager{

    private static PostManager instance;
    public static PostManager getInstance(){
        if (instance == null)
            instance = new PostManager();
        if(instance.listeners == null)
            instance.listeners = new HashMap<>();
        return instance;
    }

    public static ArrayList<Pair<String,UploadTask>>  uploadImages(Context context, String[] imgPaths){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("PostImages");
        ArrayList<Pair<String,UploadTask>> pics = new ArrayList<>();
        for (int i =0;i<imgPaths.length;i++)
        {
            String id = generateUniqueId();
            try {
                SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.package_name),Context.MODE_PRIVATE);
                File existingFile = new File(imgPaths[i]);
                File imgFile = new File(context.getCacheDir(),id);
                Util.putBitmaptoFile(Util.decodeFile(existingFile),imgFile);
                sp.edit().putString(id,imgFile.getAbsolutePath()).apply();
                InputStream stream = new FileInputStream(new File(imgPaths[i]));
                UploadTask uploadTask = storageRef.child(id).putStream(stream);
                pics.add(new Pair<>(id, uploadTask));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pics;
    }



    public static void CommitPostToDB(Post post, final CompletionListener result){
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference postRef = rootRef.child("posts").push();
        post.time = -post.time;
        post.postId = postRef.getKey();
        postRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getInstance().showPostUploadedSnackbar();
                result.OnComplete();
            }
        });
        rootRef.child("postCount").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Long p = mutableData.getValue(Long.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }
                p++;
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });

        rootRef.child("users").child(GyanithUserManager.getCurrentUser().gyanithId)
                .child("posts").child(post.postId).setValue(post);
    }

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
                if (dataSnapshot.exists())
                    callback.OnResult(dataSnapshot.getValue(Post.class));
                else
                    callback.OnResult(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static void deletePost(Post post, final CompletionListener callback){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference postRef = rootRef.child("posts").child(post.postId);
        postRef.removeValue();
        DatabaseReference userPostRef = rootRef.child("users").child(post.gyanithId).child("posts").child(post.postId);
        userPostRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.OnComplete();
            }
        });

        rootRef.child("postCount").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Long p = mutableData.getValue(Long.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }
                p--;
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
        for (String imgId : post.imgIds)
            FirebaseStorage.getInstance().getReference().child("PostImages").child(imgId).delete();
    }


    private static String generateUniqueId(){
        return FirebaseDatabase.getInstance().getReference().push().getKey();
    }

    //LIKES MANAGEMENT
    private Set<String> likedPosts;

    public void Initialize()
    {
        getRemoteLikedPosts(new ResultListener<String[]>()  {
            @Override
            public void OnResult(String[] strings) {
                likedPosts = new HashSet<>(Arrays.asList(strings));
            }
        });
    }

    public boolean isLiked(String postId)
    {
        if (likedPosts == null) {
            Log.d("asd","isLiked : not initialized");
            return false;
        }

        return likedPosts.contains(postId);
    }

    public void likePost(String postId)
    {
        for (OnLikeStateChangedListener listener : listeners.get(postId))
            listener.OnChange(true);
        likedPosts.add(postId);
        FirebaseDatabase.getInstance().getReference().child("users").child(GyanithUserManager.getCurrentUser().gyanithId)
                .child("likedPosts").child(postId).setValue(postId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(postId)
                .child("likes");
        reference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Long p = mutableData.getValue(Long.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }
                p--;
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }
    public void dislikePost(String postId){
        for (OnLikeStateChangedListener listener : listeners.get(postId))
            listener.OnChange(false);
        likedPosts.remove(postId);
        FirebaseDatabase.getInstance().getReference().child("users").child(GyanithUserManager.getCurrentUser().gyanithId)
                .child("likedPosts").child(postId).removeValue();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(postId)
                .child("likes");
        reference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Long p = mutableData.getValue(Long.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }
                p++;
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }

    private void getRemoteLikedPosts(final ResultListener<String[]> callback){
        FirebaseDatabase.getInstance().getReference().child("users").child(GyanithUserManager.getCurrentUser().gyanithId)
                .child("likedPosts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String[] posts = new String[(int) dataSnapshot.getChildrenCount()];
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                for (int i = 0;i<posts.length;i++)
                    posts[i] = iterator.next().getValue(String.class);

                callback.OnResult(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //Like Listeners
    private Map<String,List<OnLikeStateChangedListener>> listeners;

    public void AddLikeStateChangedLister(String id,OnLikeStateChangedListener listener){
        if (listeners.containsKey(id))
            listeners.get(id).add(listener);
        else {
            List<OnLikeStateChangedListener> t = new ArrayList<>();
            t.add(listener);
            listeners.put(id,t);
        }
    }


    public interface OnLikeStateChangedListener{
        void OnChange(boolean state);
    }

    //USER POSTS MANAGEMENT
    //private Set<String> userPostIds;
    private View snackbarParent;
    private FirebaseRecyclerPagingAdapter[] refresh = new FirebaseRecyclerPagingAdapter[2];
    public void getRemoteUserPosts(final ResultListener<Post[]> callback){
        FirebaseDatabase.getInstance().getReference().child("users").child(GyanithUserManager.getCurrentUser().gyanithId)
                .child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post[] posts = new Post[(int) dataSnapshot.getChildrenCount()];
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                for (int i = 0;i<posts.length;i++)
                    posts[i] = iterator.next().getValue(Post.class);

                callback.OnResult(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setSnackbarParent(View view){
        snackbarParent = view;
    }

    public void setRefreshs(FirebaseRecyclerPagingAdapter adapter){
        if (refresh[0] == null)
            refresh[0] = adapter;
        else if (refresh[1] == null)
            refresh[1] = adapter;
    }


    public void showPostUploadedSnackbar(){
        final Snackbar snackbar = Snackbar.make(snackbarParent,"Posted Successfully",10000);
        snackbar.setAction("Refresh Feed", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (FirebaseRecyclerPagingAdapter refreshAdapter : refresh)
                    refreshAdapter.refresh();
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}


