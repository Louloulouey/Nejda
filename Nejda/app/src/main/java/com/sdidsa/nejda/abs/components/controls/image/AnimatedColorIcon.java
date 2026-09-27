package com.sdidsa.nejda.abs.components.controls.image;

import android.content.Context;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;

import com.sdidsa.nejda.abs.utils.Platform;

public class AnimatedColorIcon extends ColorIcon {

    public AnimatedColorIcon(Context owner) {
        this(owner, -1);
    }

    public AnimatedColorIcon(Context owner, int id) {
        super(owner, id);
    }

    public void start(int count) {
        Platform.runLater(() -> {
            if(count > 1) {
                ((AnimatedVectorDrawable)getDrawable()).registerAnimationCallback(new Animatable2.AnimationCallback() {
                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        start(count - 1);
                        ((AnimatedVectorDrawable)getDrawable()).unregisterAnimationCallback(this);
                    }
                });
            }
            ((AnimatedVectorDrawable)getDrawable()).reset();
            ((AnimatedVectorDrawable)getDrawable()).start();
        });
    }
}
