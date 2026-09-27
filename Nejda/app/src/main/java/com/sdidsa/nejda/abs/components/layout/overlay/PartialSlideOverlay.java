package com.sdidsa.nejda.abs.components.layout.overlay;

import android.content.Context;
import android.view.Gravity;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;

public abstract class PartialSlideOverlay extends SlideOverlay {
    public static final int SQUARE_HEIGHT = -70;

    private final float margin = 15;

    public PartialSlideOverlay(Context owner, double heightFactor) {
        super(owner);
        list.setCornerRadius(20);
        setHeightFactor(heightFactor);
    }

    public PartialSlideOverlay(Context owner, int height) {
        super(owner);
        list.setCornerRadius(20);
        setHeight(height);
    }

    @Override
    public void setHeightFactor(double heightFactor) {
        super.setHeightFactor(heightFactor);
    }

    private Insets insets;
    @Override
    public void applySystemInsets(Insets insets) {
        this.insets = insets;
        LayoutParams params = (LayoutParams) list.getLayoutParams();
        int marg = SizeUtils.dipToPx(margin, owner);

        params.gravity = Gravity.BOTTOM;
        params.bottomMargin = insets.bottom + marg;
        params.rightMargin = marg;
        params.leftMargin = marg;
        list.setLayoutParams(params);
    }

    @Override
    protected void setHeight(int height) {
        if(height == SQUARE_HEIGHT) {
            height = calcHeight();
        }
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        int marg = SizeUtils.dipToPx(margin, owner);
        params.gravity = Gravity.BOTTOM;
        params.bottomMargin = (insets == null ? 0 : insets.bottom) + marg;
        params.rightMargin = marg;
        params.leftMargin = marg;
        list.setLayoutParams(params);
    }

    private int calcHeight() {
        int margins = SizeUtils.dipToPx(margin * 2, owner);
        return ContextUtils.getScreenWidth(owner) - margins;
    }
}
