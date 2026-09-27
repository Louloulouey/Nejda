package com.sdidsa.nejda.abs.animation.view;

import android.view.View;
import android.widget.LinearLayout;

import com.sdidsa.nejda.abs.animation.base.ViewAnimation;
import com.sdidsa.nejda.abs.utils.ErrorHandler;

public class LinearHeightAnimation extends ViewAnimation {

    public LinearHeightAnimation(View view, float to) {
        super(view, to);
        //check(view);
    }

    public LinearHeightAnimation(long duration, View view, float to) {
        super(duration, view, to);
        check(view);
    }

    public LinearHeightAnimation( View view, float from, float to) {
        super(view, from, to);
        check(view);
    }

    public LinearHeightAnimation(long duration, View view, float from, float to) {
        super(duration, view, from, to);
        check(view);
    }

    public void check(View view) {
        if(!(view.getLayoutParams() instanceof LinearLayout.LayoutParams)) {
            ErrorHandler.handle(new IllegalArgumentException("can't use linear height animation on non linear children"), "creating linear height animation");
        }
    }

    @Override
    protected float getFrom(View view) {
        return view.getLayoutParams().height;
    }

    @Override
    protected void apply(View view, float v) {
        view.getLayoutParams().height = Math.max((int) v, 0);
        view.setLayoutParams(view.getLayoutParams());
    }
}
