package com.barebrains.gyanith20.statics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.GyanithUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LikesSystem {

    public static List<String> likedPosts_value;

    public static MutableLiveData<List<String>> likedPosts = new MutableLiveData<>();

    public static void ToggleLikeState(final String postId, final boolean state) throws IllegalStateException {
        Resource<GyanithUser> res = GyanithUserManager.loggedUser_value;
        if (likedPosts_value == null || res.value == null)
            return;

        //DatabaseRefs
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userLikedPostsRef = rootRef.child("users").child(res.value.gyanithId).child("likedPosts");
        DatabaseReference likesRef = rootRef.child("posts").child(postId).child("likes");
        List<String> likedposts = likedPosts_value;

        //State specific
        if (state) {
            likedposts.add(postId);
            userLikedPostsRef.child(postId).setValue(postId);
            likesRef.runTransaction(decrementer);
        } else {
            likedposts.remove(postId);
            userLikedPostsRef.child(postId).removeValue();
            likesRef.runTransaction(incrementer);
        }
        likedPosts.postValue(likedposts);
    }



    public static void fetchLikedPosts(){
        GyanithUserManager.getCurrentUser().observeForever(new Observer<Resource<GyanithUser>>() {
            @Override
            public void onChanged(Resource<GyanithUser> res) {
                if (res.value == null) {
                    likedPosts.postValue(null);
                    return;
                }
                FirebaseDatabase.getInstance().getReference().child("users").child(res.value.gyanithId)
                        .child("likedPosts").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()){
                            likedPosts.postValue(new ArrayList<String>());
                            return;
                        }
                        List<String> posts = new ArrayList<>();
                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                            for (int i = 0;i<dataSnapshot.getChildrenCount();i++)
                                posts.add(iterator.next().getValue(String.class));
                        likedPosts.postValue(posts);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (NetworkManager.internet_value != null && NetworkManager.internet_value)
                            likedPosts.postValue(new ArrayList<String>());
                        else
                            likedPosts.postValue(null);
                    }
                });
            }
        });

        likedPosts.observeForever(new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                likedPosts_value = strings;
            }
        });
    }

    public static Transaction.Handler incrementer = new Transaction.Handler() {
        @NonNull
        @Override
        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
            Long p = mutableData.getValue(Long.class);
            if (p == null) {
                return Transaction.success(mutableData);
            }
            p++;

            if (p > 0)
                p = 0L;
            // Set value and report transaction success
            mutableData.setValue(p);
            return Transaction.success(mutableData);
        }

        @Override
        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

        }
    };

    public static Transaction.Handler decrementer = new Transaction.Handler() {
        @NonNull
        @Override
        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
            Long p = mutableData.getValue(Long.class);
            if (p == null) {
                return Transaction.success(mutableData);
            }
            p--;
            if (p > 0)
                p = 0L;
            // Set value and report transaction success
            mutableData.setValue(p);
            return Transaction.success(mutableData);
        }

        @Override
        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

        }
    };
}
