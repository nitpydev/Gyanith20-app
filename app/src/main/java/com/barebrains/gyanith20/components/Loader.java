package com.barebrains.gyanith20.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barebrains.gyanith20.R;

public class Loader extends FrameLayout {
    public Loader(@NonNull Context context) {
        super(context);
        if(!isInEditMode())
        init(null);
    }

    public Loader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode())
            init(context.obtainStyledAttributes(attrs,R.styleable.Loader,0,0));
    }

    public Loader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode())
            init(context.obtainStyledAttributes(attrs,R.styleable.Loader,defStyleAttr,0));
    }



    //INITIATIONS
    private View progress;
    private TextView emptyStateText;
    private View content;

    private String[] errors = new String[]{"Will be Updated Soon"};

    private void init(TypedArray attrs){
        if (attrs == null)
            return;
        if (attrs.getIndexCount() == 0){
            attrs.recycle();
            return;
        }
        errors = new String[attrs.getIndexCount()];
        for (int i = 0;i< errors.length;i++) {
            errors[i] = attrs.getString(i);
        }
        attrs.recycle();
    }

    public void setErrorTexts(String[] errors){
        this.errors = errors;
    }

    @Override
    public void onViewAdded(View child) {
        if (progress == null) {
            progress = new ProgressBar(getContext());
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            setLayoutParams(lp);
            addView(progress,lp);
            emptyStateText = new TextView(new ContextThemeWrapper(getContext(),R.style.emptyState));
            addView(emptyStateText);
            content = child;
            loading();
        }
    }

    public void loading(){
        if (progress == null)
            return;
        progress.setVisibility(VISIBLE);
        emptyStateText.setVisibility(GONE);
        if (content != null)
            content.setVisibility(GONE);
    }

    public void loaded(){
        if (progress == null)
            return;
        progress.setVisibility(GONE);
        emptyStateText.setVisibility(GONE);
        if (content != null)
            content.setVisibility(VISIBLE);
    }

    public void error(){
        error(0);
    }

    public void error(int index){
        if (progress == null)
            return;
        progress.setVisibility(GONE);
        emptyStateText.setText(errors[index]);
        emptyStateText.setVisibility(VISIBLE);
        if (content != null)
        content.setVisibility(GONE);
    }

}
