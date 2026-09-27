package com.sdidsa.nejda.abs.utils.view;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.GravityInt;

import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.utils.ErrorHandler;

public class AlignUtils {
    public static void alignInFrame(View view, Alignment alignment) {
        alignInFrame(view, alignment.getGravity());
    }

    private static void alignInFrame(View view, @GravityInt int gravity) {
        try {
            ViewGroup.LayoutParams old = view.getLayoutParams();
            StackPane.LayoutParams n = new StackPane.LayoutParams(
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT
            );

            if(old != null) {
                n.width = old.width;
                n.height = old.height;
                if(old instanceof ViewGroup.MarginLayoutParams mold) {
                    n.setMargins(mold.leftMargin, mold.topMargin, mold.rightMargin, mold.bottomMargin);
                }
            }
            n.gravity = gravity;
            view.setLayoutParams(n);
        }catch(Exception x) {
            ErrorHandler.handle(x, "aligning child in frame");
        }
    }
}
