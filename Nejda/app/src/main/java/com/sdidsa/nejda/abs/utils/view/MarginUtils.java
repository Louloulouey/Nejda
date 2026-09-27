package com.sdidsa.nejda.abs.utils.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sdidsa.nejda.abs.utils.ErrorHandler;

public class MarginUtils {
    public static void setMarginTop(View view, Context context, float val) {
        setMargin(view, context, 0, val, 0, 0);
    }

    public static void setMarginUnified(View view, Context context, float val) {
        setMargin(view, context, val, val, val, val);
    }

    public static void setMarginRight(View view, Context context, float val) {
        setMargin(view, context, 0, 0, val, 0);
    }

    public static void setMarginLeft(View view, Context context, float val) {
        setMargin(view, context, val, 0, 0, 0);
    }

    public static void setMarginHorizontal(View view, Context context, float val) {
        setMargin(view, context, val, 0, val, 0);
    }

    public static void setMarginVertical(View view, Context context, float val) {
        setMargin(view, context, 0, val, 0, val);
    }

    public static void setMarginTopRight(View view, Context context, float top, float right) {
        setMargin(view, context, 0, top, right, 0);
    }

    public static void setMarginTopLeft(View view, Context context, float top, float left) {
        setMargin(view, context, left, top, 0, 0);
    }

    public static void setMarginBottom(View view, Context context, float val) {
        setMargin(view, context, 0, 0, 0, val);
    }

    public static void setMargin(View view, Context context, float left, float top, float right, float bottom) {
        try {
            ViewGroup.LayoutParams old = view.getLayoutParams();
            if (old instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams marginLayoutParams = duplicateLinearLayoutParams((LinearLayout.LayoutParams) old);
                marginLayoutParams.setMargins(SizeUtils.dipToPx(left, context), SizeUtils.dipToPx(top, context), SizeUtils.dipToPx(right, context), SizeUtils.dipToPx(bottom, context));

                marginLayoutParams.setMarginStart(SizeUtils.dipToPx(left, context));
                marginLayoutParams.setMarginEnd(SizeUtils.dipToPx(right, context));

                view.setLayoutParams(marginLayoutParams);
            } else if(old instanceof FrameLayout.LayoutParams flp){
                FrameLayout.LayoutParams marginLayoutParams = duplicateFrameLayoutParams(flp);
                marginLayoutParams.setMargins(SizeUtils.dipToPx(left, context), SizeUtils.dipToPx(top, context), SizeUtils.dipToPx(right, context), SizeUtils.dipToPx(bottom, context));

                marginLayoutParams.setMarginStart(SizeUtils.dipToPx(left, context));
                marginLayoutParams.setMarginEnd(SizeUtils.dipToPx(right, context));

                view.setLayoutParams(marginLayoutParams);
            } else {
                ViewGroup.MarginLayoutParams marginLayoutParams = duplicateViewGroupParams(old);
                marginLayoutParams.setMargins(SizeUtils.dipToPx(left, context), SizeUtils.dipToPx(top, context), SizeUtils.dipToPx(right, context), SizeUtils.dipToPx(bottom, context));

                marginLayoutParams.setMarginStart(SizeUtils.dipToPx(left, context));
                marginLayoutParams.setMarginEnd(SizeUtils.dipToPx(right, context));

                view.setLayoutParams(marginLayoutParams);
            }
        }catch(Exception x) {
            ErrorHandler.handle(x, "setting margin");
        }
    }

    private static ViewGroup.MarginLayoutParams duplicateViewGroupParams(ViewGroup.LayoutParams old) {
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT
        );

        if (old != null) {
            params.height = old.height;
            params.width = old.width;

            if (old instanceof ViewGroup.MarginLayoutParams marginedOld) {
                params.bottomMargin = marginedOld.bottomMargin;
                params.topMargin = marginedOld.topMargin;
                params.rightMargin = marginedOld.rightMargin;
                params.leftMargin = marginedOld.leftMargin;
            }
        }

        return params;
    }

    private static FrameLayout.LayoutParams duplicateFrameLayoutParams(FrameLayout.LayoutParams old) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LinearLayout.MarginLayoutParams.WRAP_CONTENT,
                LinearLayout.MarginLayoutParams.WRAP_CONTENT
        );

        if (old != null) {
            params.height = old.height;
            params.width = old.width;

            params.gravity = old.gravity;
            params.bottomMargin = old.bottomMargin;
            params.topMargin = old.topMargin;
            params.rightMargin = old.rightMargin;
            params.leftMargin = old.leftMargin;
        }

        return params;
    }

    private static LinearLayout.LayoutParams duplicateLinearLayoutParams(LinearLayout.LayoutParams old) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.MarginLayoutParams.WRAP_CONTENT,
                LinearLayout.MarginLayoutParams.WRAP_CONTENT
        );

        if (old != null) {
            params.height = old.height;
            params.width = old.width;

            params.weight = old.weight;
            params.gravity = old.gravity;
            params.bottomMargin = old.bottomMargin;
            params.topMargin = old.topMargin;
            params.rightMargin = old.rightMargin;
            params.leftMargin = old.leftMargin;
        }

        return params;
    }
}
