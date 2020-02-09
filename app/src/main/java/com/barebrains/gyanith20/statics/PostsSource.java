package com.barebrains.gyanith20.statics;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.others.Response;
import com.firebase.ui.database.paging.LoadingState;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.barebrains.gyanith20.others.Response.DATA_EMPTY;

public class PostsSource extends ItemKeyedDataSource<Post, Post> {

    private Query query;
    private boolean timeOrdered;

    private final MutableLiveData<LoadingState> mLoadingState = new MutableLiveData<>();
    private final MutableLiveData<Response> mError = new MutableLiveData<>();

    public static class Factory extends DataSource.Factory<Post,Post> {
        private Query query;
        private boolean timeOrdered;
        private PagedList.Config config;

        public Factory(){ }

        public Factory setQuery(Query query){
            this.query =query;
            return this;
        }

        public Factory setOrder(boolean timeOrdered){
            this.timeOrdered = timeOrdered;
            return this;
        }

        public Factory setConfig(PagedList.Config config){
            this.config = config;
            return this;
        }

        public LiveData<PagedList<Post>> build() {
            return new LivePagedListBuilder<>(this, config).build();
        }

        @Override
        public PostsSource create() {
            return new PostsSource(query, timeOrdered);
        }
    }

    private PostsSource(Query query, boolean timeOrdered){
        this.query = query;
        this.timeOrdered = timeOrdered;
    }

    @Override
    public void loadInitial(@NonNull final LoadInitialParams<Post> params, @NonNull final LoadInitialCallback<Post> callback) {
        mLoadingState.postValue(LoadingState.LOADING_INITIAL);

        Query ultimateQuery = query.limitToFirst(params.requestedLoadSize);

        ultimateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mError.postValue(new Response(DATA_EMPTY));
                    return;
                }

                    //Make List of DataSnapshot
                    List<Post> data = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        data.add(snapshot.getValue(Post.class));
                    }

                    //Update State
                    mLoadingState.postValue(LoadingState.LOADED);

                    callback.onResult(data);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               setError(Response.autoRespond());
            }
        });
    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Post> params, @NonNull final LoadCallback<Post> callback) {
        // Set loading state
        mLoadingState.postValue(LoadingState.LOADING_MORE);

        //Load params.requestedLoadSize+1 because, first data item is getting ignored.
        Query ultimateQuery = query.startAt((timeOrdered) ? params.key.time : params.key.likes, params.key.postId).limitToFirst(params.requestedLoadSize + 1);
        ultimateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mError.postValue(new Response(DATA_EMPTY));
                    return;
                }
                //Make List of DataSnapshot
                List<Post> data = new ArrayList<>();

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                //Skip First Item
                if (iterator.hasNext()) {
                    iterator.next();
                }

                while (iterator.hasNext()) {
                    DataSnapshot snapshot = iterator.next();
                    data.add(snapshot.getValue(Post.class));
                }

                //Update State
                mLoadingState.postValue(LoadingState.LOADED);

                //Detect End of Data
                if (data.isEmpty())
                    mLoadingState.postValue(LoadingState.FINISHED);

                callback.onResult(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                setError(new Response(null,"Could'nt fetch posts"));
            }
        });


    }

    @Override
    public void loadBefore(@NonNull LoadParams<Post> params, @NonNull LoadCallback<Post> callback) {
        // Ignored for now, since we only ever append to the initial load.
    }

    @NonNull
    @Override
    public Post getKey(@NonNull Post item) {
        return item;
    }

    private void setError(Response response){
        mError.postValue(response);
        mLoadingState.postValue(LoadingState.ERROR);
    }

    @NonNull
    public LiveData<LoadingState> getLoadingState() {
        return mLoadingState;
    }

    @NonNull
    public LiveData<Response> getLastError(){
        return mError;
    }
}
