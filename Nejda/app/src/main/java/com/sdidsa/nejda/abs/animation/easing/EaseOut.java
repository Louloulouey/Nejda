package com.sdidsa.nejda.abs.animation.easing;

public class EaseOut implements Interpolator {
    @Override
    public float getInterpolation(float v) {
        return (float) (1 - Math.pow(1 - v, 4));
    }
}
