package com.sdidsa.nejda.abs.components.layout;

import android.view.Gravity;

/** @noinspection unused*/
public enum Alignment {
    CENTER(Gravity.CENTER),
    TOP_RIGHT(Gravity.TOP | Gravity.END),
    TOP_CENTER(Gravity.TOP | Gravity.CENTER_HORIZONTAL),
    TOP_LEFT(Gravity.TOP | Gravity.START),
    BOTTOM_RIGHT(Gravity.BOTTOM | Gravity.END),
    BOTTOM_CENTER(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL),
    BOTTOM_LEFT(Gravity.BOTTOM | Gravity.START),
    CENTER_RIGHT(Gravity.CENTER_VERTICAL | Gravity.END),
    CENTER_LEFT(Gravity.CENTER_VERTICAL | Gravity.START);

    private final int gravity;
    Alignment(int gravity) {
        this.gravity = gravity;
    }

    public int getGravity() {
        return gravity;
    }
}
