package com.sdidsa.nejda.abs.components.controls.scratches.loading;

import android.content.Context;
import android.view.View;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.RotateAnimation;
import com.sdidsa.nejda.abs.components.controls.image.ColorIcon;
import com.sdidsa.nejda.abs.components.controls.image.ImageView;

public class SpinLoading extends ColorIcon implements Loading {
    private final Animation loader;
    private boolean running = false;

    public SpinLoading(Context owner) {
        super(owner, R.drawable.loading);

        loader = new RotateAnimation(2000, this, 0, 360)
                .setInterpolator(Interpolator.LINEAR)
                .setDisableTimeScale(true)
                .setCycleCount(Animation.INDEFINITE);
    }

    @Override
    protected void onAttachedToWindow() {
        if(running)
            loader.start();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        if(running)
            loader.stop();
        super.onDetachedFromWindow();
    }

    public void startLoading() {
        running = true;
        setRotation(0);
        loader.start();
    }

    public void stopLoading() {
        running = false;
        setRotation(0);
        loader.stop();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public View getLoadingView() {
        return this;
    }
}
