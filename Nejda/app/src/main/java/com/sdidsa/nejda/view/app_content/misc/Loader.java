package com.sdidsa.nejda.view.app_content.misc;

import android.content.Context;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.components.Page;
import com.sdidsa.nejda.abs.components.controls.scratches.loading.ColoredLinearLoading;
import com.sdidsa.nejda.abs.components.controls.scratches.loading.LinearLoading;
import com.sdidsa.nejda.abs.style.Style;

public class Loader extends Page {
    private final LinearLoading loading;

    public Loader(Context owner) {
        super(owner);

        loading = new ColoredLinearLoading(owner, Style.TEXT_SEC, 18);
        addCentered(loading);
    }

    @Override
    public Animation setup(int direction) {
        if(setup == null) {
            setup = new ParallelAnimation(400)
                    .addAnimation(Animation.fadeInScaleUp(this))
                    .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
        }
        setAlpha(0);
        loading.startLoading();
        return setup;
    }

    @Override
    public Animation destroy(int direction) {
        loading.stopLoading();
        if(destroy == null) {
            destroy = new ParallelAnimation(400)
                    .addAnimation(Animation.fadeOutScaleDown(this))
                    .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
        }
        return destroy;
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
    }

    @Override
    public boolean onBack() {
        return true;
    }

    @Override
    public void applyInsets(Insets insets) {
        //ignore
    }
}
