package com.sdidsa.nejda.abs.animation.view;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

import android.view.View;
import android.widget.TextView;

import com.sdidsa.nejda.abs.animation.base.ViewAnimation;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;

public class FontSizeAnimation extends ViewAnimation {

    private final TextView view;

    public FontSizeAnimation(TextView view, float to) {
        super(view, to);
        this.view = view;
    }

    @Override
    protected void apply(View view, float v) {
        this.view.setTextSize(COMPLEX_UNIT_PX , SizeUtils.spToPx(v, view.getContext()));
    }

    @Override
    protected float getFrom(View view) {
        return SizeUtils.pxToSp(this.view.getTextSize(), view.getContext());
    }
}
