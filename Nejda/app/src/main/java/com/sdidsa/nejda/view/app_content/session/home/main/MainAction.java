package com.sdidsa.nejda.view.app_content.session.home.main;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.MotionEvent;

import androidx.annotation.DrawableRes;

import com.sdidsa.nejda.abs.App;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.abs.CornerUtils;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredVBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class MainAction extends ColoredVBox {
    private final ColoredIcon icon;
    private final ColoredLabel label;

    private GradientDrawable background;
    private final RippleDrawable ripple;

    private Runnable onClick;

    public MainAction(Context owner, String text, @DrawableRes int iconRes) {
        super(owner, Style.BACK_TER);
        background = (GradientDrawable) getBackground();
        ripple = new RippleDrawable(ColorStateList.valueOf(Color.TRANSPARENT),
                background, background);
        setBackground(ripple);

        setPadding(20);
        setCornerRadius(CornerUtils.DEFAULT);
        setSpacing(15);
        setAlignment(Alignment.CENTER);

        icon = new ColoredIcon(owner, Style.TEXT_NORM, iconRes, 30);
        label = new ColoredLabel(owner, Style.TEXT_NORM, text);
        label.setFont(new Font(16));

        SpacerUtils.spacer(this, -1,-2);

        setOnClickListener(e -> {
            if (onClick != null) {
                onClick.run();
            }
        });

        addViews(icon, label);
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    public void setBackground(int color) {
        if(background == null) return;
        background.setColor(color);
        setRippleColor(App.adjustAlpha(getComplementaryColor(color), 0.1f));
    }

    public static int getComplementaryColor(int color) {
        double y = (299f * Color.red(color) + 587f * Color.green(color) + 114f * Color.blue(color)) / 1000f;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }

    private void setRippleColor(int touchColor) {
        if(ripple == null) return;
        ripple.setColor(ColorStateList.valueOf(touchColor));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isClickable()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ripple.setHotspot(event.getX(), event.getY());
                setPressed(true);
                return true;

            case MotionEvent.ACTION_UP:
                Rect rect = new Rect();
                getHitRect(rect);
                if (event.getX() > 0 && event.getX() < rect.width() &&
                        event.getY() > 0 && event.getY() < rect.height()) {
                    performClick();
                }
                setPressed(false);
                return true;

            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                return true;

            default:
                return false;
        }
    }

    /** @noinspection EmptyMethod*/
    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
