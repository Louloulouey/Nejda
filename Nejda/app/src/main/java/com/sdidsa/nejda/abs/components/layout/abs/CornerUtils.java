package com.sdidsa.nejda.abs.components.layout.abs;

import android.content.Context;

import com.sdidsa.nejda.abs.utils.view.SizeUtils;

public class CornerUtils {
    public static final float DEFAULT = 15;

    public static float[] cornerRadius(Context owner,
                                       float topLeft,
                                       float topRight,
                                       float bottomRight,
                                       float bottomLeft) {
        int topLeftPx = SizeUtils.dipToPx(topLeft, owner);
        int topRightPx = SizeUtils.dipToPx(topRight, owner);
        int bottomRightPx = SizeUtils.dipToPx(bottomRight, owner);
        int bottomLeftPx = SizeUtils.dipToPx(bottomLeft, owner);
        return new float[]{
                topLeftPx, topLeftPx,
                topRightPx, topRightPx,
                bottomRightPx, bottomRightPx,
                bottomLeftPx, bottomLeftPx
        };
    }

    public static float[] noRadius() {
        return new float[]{
                0,0,
                0,0,
                0,0,
                0,0
        };
    }

    public static float[] cornerRadius(Context owner, float radius) {
        return cornerRadius(owner, radius, radius, radius, radius);
    }

    public static float[] cornerTopRadius(Context owner, float radius) {
        return cornerRadius(owner, radius, radius, 0, 0);
    }

    public static float[] cornerBottomRadius(Context owner, float radius) {
        return cornerRadius(owner, 0, 0, radius, radius);
    }

    public static float[] cornerRightRadius(Context owner, float radius) {
        return cornerRadius(owner, 0, radius, radius, 0);
    }

    public static float[] cornerLeftRadius(Context owner, float radius) {
        return cornerRadius(owner, radius, 0, 0, radius);
    }

    public static float[] cornerTopLeftRadius(Context owner, float radius) {
        return cornerRadius(owner, radius, 0, 0, 0);
    }

    public static float[] cornerTopRightRadius(Context owner, float radius) {
        return cornerRadius(owner, 0, radius, 0, 0);
    }

    public static float[] cornerBottomRightRadius(Context owner, float radius) {
        return cornerRadius(owner, 0, 0, radius, 0);
    }

    public static float[] cornerBottomLeftRadius(Context owner, float radius) {
        return cornerRadius(owner, 0, 0, 0, radius);
    }

    public static float[] cornerTopBottomRadius(Context owner, float topRadius, float bottomRadius) {
        return cornerRadius(owner, topRadius, topRadius, bottomRadius, bottomRadius);
    }
}
