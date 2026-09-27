package com.sdidsa.nejda.abs.components.controls.button;

import android.content.Context;
import android.graphics.Color;

import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class ColoredButton extends Button implements Styleable {
    private StyleToColor fill;
    private StyleToColor textFill;
    private StyleToColor border;
    private float borderWidth;

    public ColoredButton(Context context) {
        this(context, s -> Color.BLACK, s -> Color.WHITE, "Button Text");
    }

    public ColoredButton(Context owner,
                         StyleToColor fill,
                         StyleToColor textFill,
                         String text) {
        super(owner, text);

        this.fill = fill;
        this.textFill = textFill;

        setElevation(SizeUtils.dipToPx(5, owner));

        applyStyle(StyleUtils.getStyle(owner));
    }

    public void setTextFill(StyleToColor textFill) {
        this.textFill = textFill;
        applyStyle(StyleUtils.getStyle(owner).get());
        for(int i = 0; i < content.getChildCount(); i++) {
            if(content.getChildAt(i) instanceof ColoredIcon icon) {
                icon.setFill(textFill);
            }
        }
    }

    public void setFill(StyleToColor fill) {
        this.fill = fill;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    public void setBorder(StyleToColor border, float width) {
        this.border = border;
        this.borderWidth = width;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    @Override
    public void applyStyle(Style style) {
        setFill(fill.get(style));
        setTextFill(textFill.get(style));
        if(border != null) {
            setBorder(border.get(style), borderWidth);
        }
    }

}
