package com.sdidsa.nejda.abs.components.controls.shape;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;

import com.sdidsa.nejda.abs.data.observable.ChangeListener;
import com.sdidsa.nejda.abs.data.property.Property;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;

public class Rectangle extends View {
    private final GradientDrawable background;

    private final Property<Float> width;
    private final Property<Float> height;
    private final Property<Float> radius;
    private final Property<Integer> strokeWidth;

    private final Property<Integer> fill;
    private final Property<Integer> stroke;

    public Rectangle(Context owner) {
        super(owner);
        background = new GradientDrawable();
        setBackground(background);

        width = new Property<>(0f);
        height = new Property<>(0f);
        radius = new Property<>(0f);
        strokeWidth = new Property<>(0);

        fill = new Property<>(Color.TRANSPARENT);
        stroke = new Property<>(Color.TRANSPARENT);

        ChangeListener<Float> sizeListener = (ov, nv) -> {
            ViewGroup.LayoutParams old = getLayoutParams();
            if (getParent() instanceof StackPane) {
                StackPane.LayoutParams params = new StackPane.LayoutParams(SizeUtils.dipToPx(width.get(), owner), SizeUtils.dipToPx(height.get(), owner));
                if(old instanceof StackPane.LayoutParams oldParam)
                    params.gravity = oldParam.gravity;
                setLayoutParams(params);
            } else if(getParent() instanceof LinearLayout) {
                setLayoutParams(new LinearLayout.LayoutParams(SizeUtils.dipToPx(width.get(), owner), SizeUtils.dipToPx(height.get(), owner)));
            } else {
                setLayoutParams(new ViewGroup.LayoutParams(SizeUtils.dipToPx(width.get(), owner), SizeUtils.dipToPx(height.get(), owner)));
            }
        };
        ChangeListener<Integer> strokeListener = (ov, nv) -> background.setStroke(SizeUtils.dipToPx(strokeWidth.get(), owner), stroke.get());

        width.addListener(sizeListener);
        height.addListener(sizeListener);
        radius.addListener((ov, nv) -> background.setCornerRadius(SizeUtils.dipToPx(nv, owner)));
        strokeWidth.addListener(strokeListener);

        fill.addListener((ov, nv) -> background.setColor(nv));
        stroke.addListener((strokeListener));
    }

    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    public void setWidth(float width) {
        this.width.set(width);
    }

    public void setHeight(float height) {
        this.height.set(height);
    }

    public void setRadius(float radius) {
        this.radius.set(radius);
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth.set(strokeWidth);
    }

    public void setStroke(@ColorInt int col) {
        stroke.set(col);
    }

    public void setFill(@ColorInt int fill) {
        this.fill.set(fill);
    }
}
