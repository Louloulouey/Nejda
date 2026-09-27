package com.sdidsa.nejda.abs.components.controls.button;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.text.method.TransformationMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.sdidsa.nejda.abs.components.controls.scratches.loading.LinearLoading;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import android.widget.LinearLayout;

import com.sdidsa.nejda.abs.App;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.utils.view.AlignUtils;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;

public class Button extends StackPane {
    private final GradientDrawable background;
    private final RippleDrawable ripple;
    private final Label label;
    private Runnable onClick;

    protected final HBox content;

    private final LinearLoading loading;

    public Button(Context owner) {
        this(owner, "Button Text");
    }

    public Button(Context owner, String text) {
        super(owner);
        background = new GradientDrawable();
        setRadius(12);
        ripple = new RippleDrawable(ColorStateList.valueOf(Color.TRANSPARENT), background, background);
        setBackground(ripple);
        PaddingUtils.setPaddingUnified(this, 15, owner);

        setContentDescription(text);

        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        label = new Label(owner, text);
        label.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        label.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        label.setMaxLines(1);

        content = new HBox(owner);
        content.setGravity(Gravity.CENTER);
        AlignUtils.alignInFrame(content, Alignment.CENTER);

        content.addView(label);

        loading = new LinearLoading(owner, 8);
        addView(content);

        setFont(new Font(16));

        setOnClickListener(e -> {
            if (onClick != null) {
                onClick.run();
            }
        });

        setClickable(true);
        setFocusable(true);
    }

    public void setTextAlignment(int alignment) {
        label.setTextAlignment(alignment);
    }

    public void extendLabel() {
        content.setLayoutParams(new LayoutParams(-1,-1));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        label.setLayoutParams(params);
        setTextAlignment(TEXT_ALIGNMENT_TEXT_START);
    }

    @SuppressWarnings("unchecked")
    public <T extends Button> T setWidth(float width) {
        int pxWidth = SizeUtils.dipToPx(width, owner);
        if(getLayoutParams() != null) {
            getLayoutParams().width = pxWidth;
            setLayoutParams(getLayoutParams());
        }else {
            setLayoutParams(new LayoutParams(pxWidth, LayoutParams.WRAP_CONTENT));
        }
        return (T) this;
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

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setClickable(enabled);
        ContextUtils.applyEnabled(this, enabled);
    }

    public void startLoading() {
        getLayoutParams().height = getHeight();
        setClickable(false);
        loading.startLoading();
        removeAllViews();
        addView(loading);
    }

    public void stopLoading() {
        setClickable(true);
        removeAllViews();
        addView(content);
        loading.stopLoading();
    }

    public HBox getContent() {
        return content;
    }

    public void addPostLabel(View view) {
        content.addView(view);
    }

    public void addPreLabel(View view) {
        content.addView(view, 0);
    }

    private void setRippleColor(int touchColor) {
        ripple.setColor(ColorStateList.valueOf(touchColor));
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
        setFocusableInTouchMode(onClick != null);
        setClickable(onClick != null);
    }

    public void setRadius(float radius) {
        background.setCornerRadius(SizeUtils.dipToPx(radius, owner));
        foreground.setCornerRadius(SizeUtils.dipToPx(radius, owner));
    }

    public void setBackgroundColor(int color) {
        background.setColor(color);
        setRippleColor(App.adjustAlpha(getComplementaryColor(color), .2f));
    }

    public void setTransformationMethod(TransformationMethod method) {
        label.setTransformationMethod(method);
    }

    public void setTextFill(int color) {
        label.setTextColor(color);
        loading.setFill(color);
    }

    @SuppressWarnings("unchecked")
    public <T extends Button> T setFont(Font font) {
        label.setFont(font);
        return (T) this;
    }

    public static int getComplementaryColor(int color) {
        double y = (299f * Color.red(color) + 587f * Color.green(color) + 114f * Color.blue(color)) / 1000f;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }


    public void setFill(int fill) {
        setBackgroundColor(fill);
    }

    public void fire() {
        if(onClick != null)
            onClick.run();
    }

    public String getKey() {
        return label.getKey();
    }

    public void setKey(String name) {
        label.setKey(name);
    }

    public void setDisabled(boolean b) {
        setEnabled(!b);
    }
}