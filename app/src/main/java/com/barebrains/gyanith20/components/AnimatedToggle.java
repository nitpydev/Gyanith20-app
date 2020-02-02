package com.barebrains.gyanith20.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;

public class AnimatedToggle extends ToggleButton {

    public OnCheckedChangeListener listener;

    public AnimatedToggle(Context context) {
        super(context);
        init();
    }

    public AnimatedToggle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimatedToggle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        final ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f,Animation.RELATIVE_TO_SELF, 0.7f,Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                compoundButton.startAnimation(scaleAnimation);

                if (listener != null)
                    listener.onCheckedChanged(compoundButton, b);
            }
        });
    }

    public void setCheckListener(OnCheckedChangeListener listener){
        this.listener = listener;
    }

}
