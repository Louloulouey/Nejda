package com.sdidsa.nejda.abs.utils.view;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class SizeUtils {
    public static float scale = 1;

    public static float pxToDip(int input, Context context) {
        return pxToDip((float) input, context);
    }

    public static float pxToDip(float px, Context context) {
        return px / (context.getResources().getDisplayMetrics().density * scale);
    }

    public static float pxToDipNoScale(float px, Context context) {
        return px / (context.getResources().getDisplayMetrics().density);
    }

    public static int dipToPx(int input, Context context) {
        return (int) (dipToPx((float) input, context) + .5);
    }

    public static int dipToPx(float input, Context context) {
        return (int) (input * context.getResources().getDisplayMetrics().density * scale);
    }

    public static int[] dipToPx(int[] input, Context context) {
        int[] res = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            res[i] = dipToPx(input[i], context);
        }
        return res;
    }

    public static int dipToPx(Double aDouble, Context owner) {
        return dipToPx(aDouble.floatValue(), owner);
    }

    @SuppressWarnings("deprecation")
    public static float pxToSp(float px, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= 34) {
            return TypedValue.convertPixelsToDimension(COMPLEX_UNIT_SP, px, metrics) ;
        } else {
            return px / (context.getResources().getDisplayMetrics().scaledDensity);
        }
    }

    @SuppressWarnings("deprecation")
    public static float spToPx(float sp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= 34) {
            return TypedValue.convertDimensionToPixels(COMPLEX_UNIT_SP, sp, metrics);
        } else {
            return sp * context.getResources().getDisplayMetrics().scaledDensity;
        }
    }

    public static int by(Context owner) {
        return SizeUtils.dipToPx(64, owner);
    }
}
