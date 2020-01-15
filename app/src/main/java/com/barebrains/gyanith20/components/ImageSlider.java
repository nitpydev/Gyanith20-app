package com.barebrains.gyanith20.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ImageSlider extends Loader {
    public ImageSlider(@NonNull Context context) {
        super(context);
        init();
    }

    public ImageSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private ScrollingPagerIndicator indicator;
    private FrameLayout frameLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private Object[] loadables = new Object[0];
    private RequestOptions requestOptions = (new RequestOptions())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.l2)
            .error(R.drawable.gyanith_error);

    private void init(){

        this.setIsTextError(false);

        frameLayout = new FrameLayout(getContext());
        recyclerView = new RecyclerView(getContext());
        adapter = new recyclerAdapter();
        indicator = new ScrollingPagerIndicator(getContext());

        indicator.setDotColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
        indicator.setSelectedDotColor(ContextCompat.getColor(getContext(),R.color.colorSecondaryAccent));


        FrameLayout.LayoutParams recyclerViewLP = new FrameLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT );
        FrameLayout.LayoutParams indicatorViewLP = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT );

        indicatorViewLP.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
        indicatorViewLP.setMargins(0,0,0,50);

        (new PagerSnapHelper()).attachToRecyclerView(recyclerView);


        frameLayout.addView(recyclerView,recyclerViewLP);
        frameLayout.addView(indicator,indicatorViewLP);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false){
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                final LinearSmoothScroller linearSmoothScroller =
                        new LinearSmoothScroller(recyclerView.getContext()) {
                            private static final float MILLISECONDS_PER_INCH = 200f;

                            @Override
                            public PointF computeScrollVectorForPosition(int targetPosition) {
                                return super.computeScrollVectorForPosition(targetPosition);
                            }

                            @Override
                            protected float calculateSpeedPerPixel
                                    (DisplayMetrics displayMetrics) {
                                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                            }
                        };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        });

        addView(frameLayout);

        handleTouches();

        this.loading();
    }


    private class recyclerAdapter extends RecyclerView.Adapter<viewHolder>{


        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
            final FrameLayout itemView = new FrameLayout(getContext());
            final int width = recyclerView.getLayoutManager().getWidth();
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams((width == 0)?MATCH_PARENT:width,MATCH_PARENT);
            itemView.setLayoutParams(layoutParams);
            post(new Runnable() {
                @Override
                public void run() {
                    itemView.getLayoutParams().width = getWidth();
                }
            });

            return new viewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, int position) {
            holder.loadImage(position);
        }

        @Override
        public int getItemCount() {
            return loadables.length;
        }
    }

    private class viewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = new ImageView(getContext());
            ((FrameLayout)itemView).addView(imageView);
        }

        private void loadImage(int position){
            Glide.with(getContext())
                    .load(loadables[position])
                    .apply(requestOptions)
                    .into(imageView);

        }
    }

    //Properties functions
    public ImageSlider load(@NonNull Object[] loadables){
        this.loadables = loadables;
        return this;
    }



    public ImageSlider apply(@NonNull RequestOptions requestOptions){
        this.requestOptions = this.requestOptions.apply(requestOptions);
        return this;
    }

    public void start(){
        recyclerView.setAdapter(adapter);
        indicator.attachToRecyclerView(recyclerView);
        this.loaded();
    }


    //Touch and Click Behaviour
    private OnClickListener onClickListener;

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        onClickListener = l;
    }

    private void handleTouches(){
        final GestureDetector tapGestureDetector = new GestureDetector(getContext(), new TapGestureListener());

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener(){
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    onTouchChanged(true);
                } else if (e.getAction() != MotionEvent.ACTION_MOVE)
                    onTouchChanged(false);

                tapGestureDetector.onTouchEvent(e);

                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }
    private class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if(onClickListener != null) {
                onClickListener.onClick(recyclerView);
            }

            return true;
        }


    }


    //Scrolling Behaviour
    boolean autoScroll = false;
    int scrollSpeed = 3000;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        int count = 0;
        boolean flag = true;
        @Override
        public void run() {
            if(count < adapter.getItemCount()){
                if(count==adapter.getItemCount()-1){
                    flag = false;
                }else if(count == 0){
                    flag = true;
                }
                if(flag) count++;
                else count--;

                recyclerView.smoothScrollToPosition(count);
                if (autoScroll)
                    handler.postDelayed(this, scrollSpeed);
            }
        }
    };

    private void onTouchChanged(boolean isTouching){
        if (!autoScroll)
            return;
        if (isTouching)
            handler.removeCallbacks(runnable);
        else
            handler.postDelayed(runnable, scrollSpeed);
    }

    public void autoScroll(boolean scroll){
        autoScroll = scroll;
        if (scroll)
            handler.postDelayed(runnable, scrollSpeed);
        else
            handler.removeCallbacks(runnable);
    }
}
