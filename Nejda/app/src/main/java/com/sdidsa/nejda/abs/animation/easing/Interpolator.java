package com.sdidsa.nejda.abs.animation.easing;

import android.animation.TimeInterpolator;

public interface Interpolator extends TimeInterpolator {
    Interpolator LINEAR = new Linear();
    Interpolator EASE_OUT = new EaseOut();

    Interpolator ANTICIPATE_OVERSHOOT = new AnticipateOvershoot();
    Interpolator ANTICIPATE = new EaseOut();
    Interpolator OVERSHOOT = new Overshoot();

    float getInterpolation(float v);
}
