package com.barebrains.gyanith20.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;

import com.barebrains.gyanith20.R;

public class Loader extends FrameLayout {
    public Loader(@NonNull Context context) {
        super(context);
    }

    public Loader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.Loader,0,0);
            init(typedArray);
        }
    }

    public Loader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.Loader,defStyleAttr,0);
            init(typedArray);
        }
    }


    //ATTRIBUTES
    private int loadingIndicatorResId = R.layout.loader_progressbar;
    private boolean isTextError = true;

    private String empty_error_string = "Will be Updated Soon";
    private int empty_error_visual = R.layout.loader_empty_error_visual;

    private View loadingIndicator = null;
    private View errorHolder = null;
    private View content = null;


    private void init(TypedArray attrs){
        loadingIndicatorResId = attrs.getResourceId(R.styleable.Loader_loading_indicator, R.layout.loader_progressbar);
        isTextError = attrs.getBoolean(R.styleable.Loader_isErrorText, true);
        if (isTextError) {
            String s = attrs.getString(R.styleable.Loader_empty_error);
            if (s != null)empty_error_string = s;
        } else {
            empty_error_visual = attrs.getResourceId(R.styleable.Loader_empty_error,R.layout.loader_empty_error_visual);
        }
        attrs.recycle();
    }

    public void setIsTextError(boolean isTextError){
        this.isTextError = isTextError;
    }

    public void setEmpty_error_visual(int error_visual){
        empty_error_visual = error_visual;
    }

    public void set_empty_error(String error){
        this.empty_error_string = error;
    }


    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if(isInEditMode())
            return;
        if (loadingIndicator == null) {
            loadingIndicator = LayoutInflater.from(getContext()).inflate( loadingIndicatorResId,this,false);
            addView(loadingIndicator);
            if (isTextError) {
                errorHolder = new TextView(new ContextThemeWrapper(getContext(),R.style.emptyState));
                addView(errorHolder);
            }

            content = child;

            loading();
        }
    }

    //PUBLIC FUNCITONS
    public View getErrorHolder(){
        return errorHolder;
    }

    public void loading(){
        loadingIndicator.setVisibility(VISIBLE);
        if (content != null)
            content.setVisibility(GONE);

      if (isTextError)
          errorHolder.setVisibility(GONE);
      else
          removeErrorVisual();
    }

    public void loaded(){
        loadingIndicator.setVisibility(GONE);
        if (content != null)
            content.setVisibility(VISIBLE);

        if (isTextError)
            errorHolder.setVisibility(GONE);
        else
            removeErrorVisual();
    }

    public void error(){
        error(0);
    }

    public void error(int index){
        loadingIndicator.setVisibility(GONE);
        if (content != null)
            content.setVisibility(GONE);
        if (isTextError){
            ((TextView)errorHolder).setText(empty_error_string);
            errorHolder.setVisibility(VISIBLE);
        }
        else
            setErrorVisual();
    }

    private void setErrorVisual(){
        if (errorHolder != null)
            removeView(errorHolder);
        Log.d("asd","c 6");
        errorHolder = LayoutInflater.from(getContext()).inflate(empty_error_visual,this,false);
        addView(errorHolder);
    }

    private void removeErrorVisual(){
        if (errorHolder == null)
            return;

        removeView(errorHolder);
    }

}
