package com.sdidsa.nejda.abs.components.controls.scratches;

public enum Orientation {
    HORIZONTAL, VERTICAL;

    public boolean isHorizontal() {
        return this == HORIZONTAL;
    }

    public boolean isVertical() {
        return this == VERTICAL;
    }

}