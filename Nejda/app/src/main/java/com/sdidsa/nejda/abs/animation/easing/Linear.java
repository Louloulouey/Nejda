package com.sdidsa.nejda.abs.animation.easing;

public class Linear implements Interpolator {
    @Override
    public float getInterpolation(float v) {
        return v;
    }
}
