package com.barebrains.gyanith20.statics;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.barebrains.gyanith20.interfaces.AuthStateListener;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.barebrains.gyanith20.models.GyanithUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.barebrains.gyanith20.statics.Util.decrementer;
import static com.barebrains.gyanith20.statics.Util.incrementer;

@Deprecated//TODO: THIS SYSTEM NEEDS LIVE DATA DESPERATELY
public class LikesSystem {

    private static Set<String> likedPost_cache;

    public static void Initialize(){

        GyanithUserManager.getCurrentUser().observeForever(new Observer<Resource<GyanithUser>>() {
            @Override
            public void onChanged(Resource<GyanithUser> res) {
                if (res.value != null){
                    fetchLikedPosts(new ResultListener<String[]>(){
                        @Override
                        public void OnResult(String[] strings) {
                            likedPost_cache = new HashSet<>(Arrays.asList(strings));
                        }

                        @Override
                        public void OnError(String error) {
                            Log.d("asd","LikesSystem : " + error);
                        }
                    });
                }
            }
        });
        GyanithUserManager.addAuthStateListener(new AuthStateListener(){
            @Override
            public void VerifiedUser() {

            }

            @Override
            public void NullUser() {
                likedPost_cache = null;
            }
        });
    }

    public static boolean isLiked(String postId)throws IllegalStateException{
        if (likedPost_cache == null)
            throw new IllegalStateException("No user Signed In");

        return likedPost_cache.contains(postId);
    }

    public static void ToggleLikeState(final String postId, final boolean state) throws IllegalStateException
    {
        GyanithUserManager.getCurrentUser().observeForever(new Observer<Resource<GyanithUser>>() {
            @Override
            public void onChanged(Resource<GyanithUser> res) {
                if (res.value == null)
                    return;
                //DatabaseRefs
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference userLikedPostsRef = rootRef.child("users").child(res.value.gyanithId).child("likedPosts");
                DatabaseReference likesRef = rootRef.child("posts").child(postId).child("likes");

                //State specific
                if (state) {
                    likedPost_cache.add(postId);
                    userLikedPostsRef.child(postId).setValue(postId);
                    likesRef.runTransaction(decrementer);
                } else {
                    likedPost_cache.remove(postId);
                    userLikedPostsRef.child(postId).removeValue();
                    likesRef.runTransaction(incrementer);
                }
                respondListeners(postId);
            }
        });
    }



    private static void fetchLikedPosts(final ResultListener<String[]> callback){
        GyanithUserManager.getCurrentUser().observeForever(new Observer<Resource<GyanithUser>>() {
            @Override
            public void onChanged(Resource<GyanithUser> res) {
                if (res == null) {
                    return;
                }
                FirebaseDatabase.getInstance().getReference().child("users").child(res.value.gyanithId)
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
                        if (NetworkManager.internet.getValue())
                            callback.OnResult(new String[0]);
                        else
                            callback.OnError(null);
                    }
                });
                GyanithUserManager.getCurrentUser().removeObserver(this);
            }
        });
    }



    //LISTENERS HANDLING
    public static List<ResultListener<String>> listeners = new ArrayList<>();

    private static void respondListeners(String postId){
        for (ResultListener<String> listener : listeners)
            listener.OnResult(postId);
    }
}
