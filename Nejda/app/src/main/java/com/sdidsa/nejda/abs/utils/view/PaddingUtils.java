package com.sdidsa.nejda.abs.utils.view;

import android.content.Context;
import android.view.View;

public class PaddingUtils {
    public static void setPaddingUnified(View view, float padding, Context context) {
        setPadding(view, padding, padding, padding, padding, context);
    }

    public static void setPaddingTop(View view, float padding, Context context) {
        setPadding(view, 0, padding, 0, 0, context);
    }

    public static void setPaddingVertical(View view, float padding, Context context) {
        setPadding(view, 0, padding, 0, padding, context);
    }

    public static void setPaddingHorizontalVertical(View view, float horizontal, float vertical, Context context) {
        setPadding(view, horizontal, vertical, horizontal, vertical, context);
    }

    public static void setPadding(View view, float left, float top, float right, float bottom, Context context) {
        int dil = left == -1 ? view.getPaddingLeft() : SizeUtils.dipToPx(left, context);
        int dit = top == -1 ? view.getPaddingTop() : SizeUtils.dipToPx(top, context);
        int dir = right == -1 ? view.getPaddingRight() : SizeUtils.dipToPx(right, context);
        int dib = bottom == -1 ? view.getPaddingBottom() : SizeUtils.dipToPx(bottom, context);
        view.setPaddingRelative(dil, dit, dir, dib);
        //view.setPadding(dil, dit, dir, dib);
    }

    public static int[] getPadding(View view) {
        return new int[]{view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()};
    }
}
