package com.sdidsa.nejda.abs.animation.view.padding;

import android.view.View;

import com.sdidsa.nejda.abs.animation.base.ViewAnimation;

public class PaddingTopAnimation extends ViewAnimation {

    private int fromLeft, fromTop, fromRight, fromBottom;
    private final int toTop;

    public PaddingTopAnimation(long duration, View view, int toTop) {
        super(duration, view, 0, 1);

        this.toTop = toTop;
        fromTop = -1;
    }

    public PaddingTopAnimation(View view, int fromTop, int toTop) {
        super(view, 0, 1);

        this.toTop = toTop;
        this.fromTop = fromTop;
    }

    public PaddingTopAnimation(View view, int toTop) {
        super(view, 0, 1);

        this.toTop = toTop;
        fromTop = -1;
    }

    @Override
    public void init() {
        super.init();
        fromLeft = getView().getPaddingLeft();
        if(fromTop == -1) fromTop = getView().getPaddingTop();
        fromRight = getView().getPaddingRight();
        fromBottom = getView().getPaddingBottom();
    }

    @Override
    protected float getFrom(View view) {
        return view.getPaddingTop();
    }

    @Override
    protected void apply(View view, float v) {
        int top = (int) (fromTop + (toTop - fromTop) * v);
        view.setPadding(fromLeft, top, fromRight, fromBottom);
    }
}
