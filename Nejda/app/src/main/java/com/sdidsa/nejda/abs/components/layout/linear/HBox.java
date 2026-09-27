package com.sdidsa.nejda.abs.components.layout.linear;

import android.content.Context;
import android.widget.LinearLayout;

public class HBox extends LinearBox {
    public HBox(Context owner) {
        super(owner);
        setOrientation(LinearLayout.HORIZONTAL);
    }

    @Override
    public void setSpacing(float spacing) {
        super.setSpacing(spacing);
    }
}
