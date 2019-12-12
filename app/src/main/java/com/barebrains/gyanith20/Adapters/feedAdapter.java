package com.barebrains.gyanith20.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.barebrains.gyanith20.Models.Post;
import com.barebrains.gyanith20.Others.PostViewHolder;
import com.barebrains.gyanith20.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class feedAdapter extends RecyclerView.Adapter<PostViewHolder> implements ChildEventListener {

    private Context context;
    private int lastPost;
    private int postsPerQuery;
    private List<Post> posts;
    private List<Query> queries;
    private Query fullQuery;
    private PageLoadListener pageLoadListener;
    private boolean fullfill = false;
    private String id;
    private String lastPostStart;
    private boolean hot;

    public feedAdapter(boolean hot,String id,Context context, int postsPerQuery, Query query,PageLoadListener listener ){
        this.hot = hot;
        this.id = id;
        this.context = context;
        lastPost = 0;
        this.postsPerQuery = postsPerQuery;
        queries = new ArrayList<>();
        posts = new ArrayList<>();
        fullQuery = query;
        pageLoadListener = listener;
        lastPostStart = "-Zzzzzzzzzzzzzzzzzzzzzzzz";
        getNextPageItems();
    }
    public void getNextPageItems(){

        if (fullfill)
            return;

        pageLoadListener.OnStartLoad();
        fullfill = true;//posts.get(lastPost - 1).postId
       // String start = (lastPostStart != null)?lastPostStart:"-Zzzzzzzzzzzzzzzzzzzzzzzz";
        final Query newQuery = fullQuery.endAt(lastPostStart).limitToLast(postsPerQuery);
        queries.add(newQuery);

        newQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                Iterator<DataSnapshot> postsSnaps = dataSnapshots.getChildren().iterator();
                Post[] tempPosts = new Post[((int) dataSnapshots.getChildrenCount())];
                for (int i = 0;i<dataSnapshots.getChildrenCount();i++)
                    tempPosts[i] = postsSnaps.next().getValue(Post.class);
                int x = (posts.size() ==0)?1:2;
                for (int i = tempPosts.length - x;i >= 0;i--)
                    posts.add(tempPosts[i]);
                pageLoadListener.OnLoaded();
                notifyItemRangeInserted(lastPost,tempPosts.length - x + 1);
                lastPost = posts.size();
                newQuery.addChildEventListener(feedAdapter.this);
                fullfill = false;
                Log.d("asd","end " + id + " : " + dataSnapshots.getChildrenCount());
                if(dataSnapshots.getChildrenCount() == 1)
                    Toast.makeText(context,"Reached end of the posts!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removeListeners(){
        for (Query query : queries)
            query.removeEventListener(this);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedpost,parent,false);
        return new PostViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.FillPost(context,posts.get(position));
    }

    @Override
    public int getItemCount() {
        return lastPost;
    }

    //LISTENER CALLBACKS
    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    public interface PageLoadListener {
        void OnStartLoad();
        void OnLoaded();
    }
}
