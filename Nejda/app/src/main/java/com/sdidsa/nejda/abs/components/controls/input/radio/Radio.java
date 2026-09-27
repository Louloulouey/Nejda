package com.sdidsa.nejda.abs.components.controls.input.radio;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.components.controls.shape.Rectangle;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.utils.view.AlignUtils;
import com.sdidsa.nejda.abs.data.property.Property;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class Radio extends StackPane implements Styleable {
    private final GradientDrawable background;

    private final Rectangle checkMark;
    private final Property<Boolean> checked = new Property<>(false);

    private StyleToColor fill = Style.TEXT_SEC;

    private float size;
    public Radio(Context owner) {
        super(owner);

        background = new GradientDrawable();
        background.setColor(Color.TRANSPARENT);
        background.setCornerRadius(SizeUtils.dipToPx(5, owner));

        setBackground(background);

        setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        setClipToOutline(true);

        checkMark = new Rectangle(owner);
        checkMark.setAlpha(0);

        addView(checkMark);

        setSize(20);

        AlignUtils.alignInFrame(checkMark, Alignment.CENTER);

        applyStyle(StyleUtils.getStyle(owner));
    }

    private Animation showCheck = null;
    private Animation hideCheck = null;

    public Radio(Context owner, float size) {
        this(owner);
        setSize(size);
    }

    public void setChecked(boolean checked) {
        if(checked == isChecked()) return;
        this.checked.set(checked);
        if(checked) {
            if(showCheck == null) {
                showCheck = Animation.fadeInUp(owner, .5f, checkMark)
                        .setInterpolator(Interpolator.OVERSHOOT);
            }
            if(hideCheck != null) hideCheck.stop();
            showCheck.stop();
            showCheck.start();
        }else {
            if(hideCheck == null) {
                hideCheck = Animation.fadeOutDown(owner, .1f, checkMark)
                        .setInterpolator(Interpolator.EASE_OUT);
            }
            if(showCheck != null) showCheck.stop();
            hideCheck.stop();
            hideCheck.start();
        }
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    public boolean isChecked() {
        return checked.get();
    }

    public Property<Boolean> checkedProperty() {
        return checked;
    }

    public void setSize(float size) {
        this.size = size;
        int sizePx = SizeUtils.dipToPx(size, owner);

        ViewGroup.LayoutParams old = getLayoutParams();
        if(old == null) {
            setLayoutParams(new ViewGroup.LayoutParams(sizePx, sizePx));
        }else {
            old.width = sizePx;
            old.height = sizePx;
            setLayoutParams(old);
        }

        background.setCornerRadius(size * 2);
        float s = size / 2;
        checkMark.setSize(s, s);
        checkMark.setRadius(s);

        applyStyle(StyleUtils.getStyle(owner).get());
    }

    public void setFill(StyleToColor fill) {
        this.fill = fill;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    @Override
    public void applyStyle(Style style) {
        background.setStroke(SizeUtils.dipToPx(size / 10, owner), fill.get(style));
        background.setColor(Color.TRANSPARENT);
        checkMark.setFill(fill.get(style));
    }

}
