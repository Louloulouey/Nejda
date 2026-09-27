package com.sdidsa.nejda.abs.components.controls.scratches;

import android.content.Context;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.RotateAnimation;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.StyleToColor;

public class Refresh extends ColoredIcon {
    private static final float ALPHA = .5f;
    public Refresh(Context owner) {
        this(owner, Style.TEXT_NORM, 48);
    }

    public Refresh(Context owner, StyleToColor color, float size) {
        super(owner, color, R.drawable.refresh, size);
        setPadding(12);
        setAlpha(.7f);
    }

    private Animation show;
    public Animation show() {
        setClickable(true);
        if(show == null) {
            show = new ParallelAnimation(300)
                    .addAnimation(Animation.fadeInScaleUp(ALPHA, this))
                    .addAnimation(new RotateAnimation(this, -90, 0))
                    .setInterpolator(Interpolator.OVERSHOOT);
        }
        return show;
    }

    private Animation hide;
    public Animation hide() {
        setClickable(false);
        if(hide == null) {
            hide = new ParallelAnimation(300)
                    .addAnimation(Animation.fadeOutScaleDown(this))
                    .addAnimation(new RotateAnimation(this, 90)
                            .setLateFrom(this::getRotation)
                            .setLateTo(() -> this.getRotation() + 90))
                    .setInterpolator(Interpolator.OVERSHOOT);
        }
        return hide;
    }

    public void applyRefresh(float dist) {
        setRotation(dist * -90);
        float scale = 1f + dist / 4.5f;
        setScaleX(scale);
        setScaleY(scale);
        setAlpha(ALPHA + (1-ALPHA) * dist);

    }
}
