package com.sdidsa.nejda.abs.animation.view;

import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.components.layout.abs.Cornered;
import com.sdidsa.nejda.abs.utils.ErrorHandler;

public class CornerRadiusAnimation extends Animation {
    private final Cornered view;
    private float[] from;
    private final float[] to;

    public CornerRadiusAnimation(Cornered view, float[] from, float[] to) {
        this.view = view;
        this.from = from;
        this.to = to;
    }

    @Override
    public void init() {
        super.init();
        if(from == null) {
            from = view.getCornerRadius();
        }
        if (from.length != to.length) {
            ErrorHandler.handle(
                    new IllegalArgumentException("from and to must have the same length"),
                    "initializing corner radius animation");
        }
    }

    @Override
    public void update(float v) {
        float[] current = new float[from.length];
        for (int i = 0; i < from.length; i++) {
            current[i] = from[i] + v * (to[i] - from[i]);
        }
        view.setCornerRadius(current);
    }
}
