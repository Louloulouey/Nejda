package com.sdidsa.nejda.abs.components.layout.linear;

import android.content.Context;
import android.widget.LinearLayout;

public class VBox extends LinearBox {
    public VBox(Context owner) {
        super(owner);
        setOrientation(LinearLayout.VERTICAL);
    }
}
