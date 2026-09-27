package com.sdidsa.nejda.abs.animation.view.padding;

import android.view.View;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.abs.animation.base.ViewAnimation;

public class PaddingAnimation extends ViewAnimation {

    private int fromLeft, fromTop, fromRight, fromBottom;
    private final int toLeft, toTop, toRight, toBottom;

    public PaddingAnimation(long duration, View view, int toLeft, int toTop, int toRight, int toBottom) {
        super(duration, view, 0, 1);

        this.toLeft = toLeft;
        this.toTop = toTop;
        this.toRight = toRight;
        this.toBottom = toBottom;
    }

    public PaddingAnimation(long duration, View view, int[] pads) {
        super(duration, view, 0, 1);

        this.toLeft = pads[0];
        this.toTop = pads[1];
        this.toRight = pads[2];
        this.toBottom = pads[3];
    }

    public PaddingAnimation(long duration, View view, int[] from, Insets to) {
        super(duration, view, 0, 1);

        this.toLeft = to.left;
        this.toTop = to.top;
        this.toRight = to.right;
        this.toBottom = to.bottom;

        this.fromLeft = from[0];
        this.fromTop = from[1];
        this.fromRight = from[2];
        this.fromBottom = from[3];
    }

    public PaddingAnimation(View view, int[] pads) {
        super(view, 0, 1);

        this.toLeft = pads[0];
        this.toTop = pads[1];
        this.toRight = pads[2];
        this.toBottom = pads[3];

        this.fromLeft = -1;
        this.fromTop = -1;
        this.fromRight = -1;
        this.fromBottom = -1;
    }

    public PaddingAnimation(View view, int[] from, int[] to) {
        super(view, 0, 1);

        this.toLeft = to[0];
        this.toTop = to[1];
        this.toRight = to[2];
        this.toBottom = to[3];

        this.fromLeft = from[0];
        this.fromTop = from[1];
        this.fromRight = from[2];
        this.fromBottom = from[3];
    }

    @Override
    public void init() {
        super.init();
        if(fromLeft == -1) fromLeft = getView().getPaddingLeft();
        if(fromTop == -1) fromTop = getView().getPaddingLeft();
        if(fromRight == -1) fromRight = getView().getPaddingLeft();
        if(fromBottom == -1) fromBottom = getView().getPaddingLeft();
    }

    @Override
    protected float getFrom(View view) {
        return 0;
    }

    @Override
    protected void apply(View view, float v) {
        int left = (int) (fromLeft + (toLeft - fromLeft) * v);
        int top = (int) (fromTop + (toTop - fromTop) * v);
        int right = (int) (fromRight + (toRight - fromRight) * v);
        int bottom = (int) (fromBottom + (toBottom - fromBottom) * v);
        view.setPadding(left, top, right, bottom);
    }
}
