package com.barebrains.gyanith20.adapters;

import android.util.Log;
import android.util.Pair;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.others.Response;
import com.barebrains.gyanith20.others.PostViewHolder;
import com.barebrains.gyanith20.statics.PostsSource;
import com.firebase.ui.database.paging.LoadingState;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

public abstract class postsAdapter extends PagedListAdapter<Post, PostViewHolder> {

    public static Map<String, MutableLiveData<Post>> posts = new HashMap<>();

    private static final PagedList.Config CONFIG = new PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPrefetchDistance(15)
            .setPageSize(15)
            .build();

    private LiveData<PostsSource> DataSource;

    private FragmentManager fragmentManager;

    protected postsAdapter(FragmentManager fragmentManager, @NonNull LifecycleOwner lifecycleOwner, @NonNull Query query, boolean timeOrdered) {
         super(DIFF_CALLBACK);

         this.fragmentManager =  fragmentManager;

        final LiveData<PagedList<Post>> postsList = new PostsSource.Factory()
                .setQuery(query)
                .setOrder(timeOrdered)
                .setConfig(CONFIG)
                .build();


        DataSource = Transformations.map(postsList,
                new Function<PagedList<Post>, PostsSource>() {
                    @Override
                    public PostsSource apply(PagedList<Post> input) {
                        return (PostsSource) input.getDataSource();
                    }
                });

        final LiveData<LoadingState> mLoadingState = Transformations.switchMap(DataSource, new Function<PostsSource, LiveData<LoadingState>>() {
            @Override
            public LiveData<LoadingState> apply(PostsSource input) {
                return input.getLoadingState();
            }
        });


        LiveData<Response> mDatabaseError = Transformations.switchMap(DataSource, new Function<PostsSource, LiveData<Response>>() {
            @Override
            public LiveData<Response> apply(PostsSource input) {
                return input.getLastError();
            }
        });

        postsList.observe(lifecycleOwner, new Observer<PagedList<Post>>() {
            @Override
            public void onChanged(PagedList<Post> posts) {
                submitList(posts);
            }
        });

        mLoadingState.observe(lifecycleOwner, new Observer<LoadingState>() {
            @Override
            public void onChanged(LoadingState loadingState) {
                onLoadingStateChanged(loadingState);
            }
        });

        mDatabaseError.observe(lifecycleOwner, new Observer<Response>() {
            @Override
            public void onChanged(Response e) {
                onError(e);
            }
        });
    }

    public void refresh(){
        PostsSource postsSource = DataSource.getValue();
        if (postsSource != null)
            postsSource.invalidate();
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder viewHolder, int position) {
        final Post post = getItem(position);
        if (post == null){
            viewHolder.clear();//PlaceHolder
            return;
        }
        final MutableLiveData<Post> livePost;
        if (!posts.containsKey(post.postId)) {
            livePost = new MutableLiveData<>();
            posts.put(post.postId,livePost);
            livePost.postValue(post);
        }
        else
            livePost = posts.get(post.postId);
        viewHolder.bindto(livePost);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return PostViewHolder.getHolder(parent,fragmentManager);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.layout_anim);
        recyclerView.setLayoutAnimation(animation);
        super.onAttachedToRecyclerView(recyclerView);
    }

    private static DiffUtil.ItemCallback<Post> DIFF_CALLBACK = new DiffUtil.ItemCallback<Post>() {
        @Override
        public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return oldItem.equals(newItem);
        }
    };

    protected abstract void onError(@NonNull Response error);
    protected abstract void onLoadingStateChanged(@NonNull LoadingState state);
}
