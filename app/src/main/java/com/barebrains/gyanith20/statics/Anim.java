package com.barebrains.gyanith20.statics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.ScaleAnimation;

public class Anim {

    public static void crossfade(final View from, final View to, final float from_final, long duration){
        from.setVisibility(View.VISIBLE);

        from.animate().alpha(from_final)
                .setDuration(duration)
                .setListener(null);
        to.setVisibility(View.VISIBLE);
        to.animate().alpha(1)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (from_final == 0)
                from.setVisibility(View.GONE);
            }
        });
    }

    public static void zoom(View view,float from,float to,long duration){
        ScaleAnimation scaleAnimation = new ScaleAnimation(from, to, from, to, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(duration);
        view.startAnimation(scaleAnimation);
    }
}
