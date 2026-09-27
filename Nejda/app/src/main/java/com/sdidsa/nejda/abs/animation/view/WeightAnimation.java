package com.sdidsa.nejda.abs.animation.view;

import android.view.View;
import android.widget.LinearLayout;

import com.sdidsa.nejda.abs.animation.base.ViewAnimation;
import com.sdidsa.nejda.abs.utils.ErrorHandler;

public class WeightAnimation extends ViewAnimation {

    public WeightAnimation(View view, float to) {
        super(view, to);
        check(view);
    }

    public WeightAnimation(View view, float from, float to) {
        super(view, from, to);
        check(view);
    }

    public void check(View view) {
        if(!(view.getLayoutParams() instanceof LinearLayout.LayoutParams)) {
            ErrorHandler.handle(new IllegalArgumentException(
                    "can't use linear height animation on non linear children"),
                    "creating linear weight animation");
        }
    }

    @Override
    protected float getFrom(View view) {
        return ((LinearLayout.LayoutParams) view.getLayoutParams()).weight;
    }

    @Override
    protected void apply(View view, float v) {
        ((LinearLayout.LayoutParams) view.getLayoutParams()).weight = v > 1 ? 1 : v < 0 ? 0 : v;
        view.setLayoutParams(view.getLayoutParams());
    }
}
