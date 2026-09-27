package com.sdidsa.nejda.abs.utils.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.utils.ErrorHandler;

public class SpacerUtils {


    public static LinearLayout.LayoutParams spacer(View view, Orientation orientation) {
        return spacer(view, orientation, 1f);
    }

    public static LinearLayout.LayoutParams spacer(View view, Orientation orientation, float weight) {
        try {
            boolean hor = orientation == Orientation.HORIZONTAL;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    hor ? 0 : -1,
                    hor ? -1 : 0);
            params.weight = weight;
            view.setLayoutParams(params);
            return params;
        }catch (Exception x) {
            ErrorHandler.handle(x, "creating spacer");
        }
        return null;
    }

    public static LinearLayout.LayoutParams spacer(View view) {
        return spacer(view, -2, -2);
    }

    public static LinearLayout.LayoutParams spacer(View view, int w, int h) {
        int width = view.getLayoutParams() != null ? view.getLayoutParams().width :
                w;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, h);
        params.weight = 1;
        view.setLayoutParams(params);
        return params;
    }

    public static void spacerWidth(View view, float weight) {
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.weight = weight;
        view.setLayoutParams(params);
    }

    public static View spacer(Context context, Orientation orientation) {
        try {
            boolean hor = orientation == Orientation.HORIZONTAL;
            View view = new View(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    hor ? 0 : -1,
                    hor ? -1 : 0);
            params.weight = 1;
            view.setLayoutParams(params);
            return view;
        }catch (Exception x) {
            ErrorHandler.handle(x, "creating spacer");
            return null;
        }
    }
}
