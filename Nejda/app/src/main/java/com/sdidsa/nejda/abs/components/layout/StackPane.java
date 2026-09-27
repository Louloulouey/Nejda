package com.sdidsa.nejda.abs.components.layout;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sdidsa.nejda.abs.components.layout.abs.Bordered;
import com.sdidsa.nejda.abs.components.layout.abs.CornerUtils;
import com.sdidsa.nejda.abs.components.layout.abs.Cornered;
import com.sdidsa.nejda.abs.utils.view.AlignUtils;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;

public class StackPane extends FrameLayout implements Cornered, Bordered {
    protected final Context owner;

    private final GradientDrawable background;
    protected final GradientDrawable foreground;

    public StackPane(Context owner) {
        super(owner);
        this.owner = owner;

        background = new GradientDrawable();
        foreground = new GradientDrawable();
        setBackground(background);
        setForeground(foreground);
    }

    public void setPadding(float padding) {
        PaddingUtils.setPaddingUnified(this, padding, owner);
    }

    public void setBackground(int color) {
        background.setColor(color);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void setCornerRadius(float radius) {
        int px = SizeUtils.dipToPx(radius, owner);
        background.setCornerRadius(px);
        foreground.setCornerRadius(px);
    }

    @Override
    public void setCornerRadius(float[] radius) {
        background.setCornerRadii(radius);
        foreground.setCornerRadii(radius);
    }

    @Override
    public void setCornerRadiusTop(float radius) {
        setCornerRadius(CornerUtils.cornerTopRadius(owner, radius));
    }

    @Override
    public void setCornerRadiusBottom(float radius) {
        setCornerRadius(CornerUtils.cornerBottomRadius(owner, radius));
    }

    @Override
    public void setCornerRadiusRight(float radius) {
        setCornerRadius(CornerUtils.cornerRightRadius(owner, radius));
    }

    @Override
    public void setCornerRadiusLeft(float radius) {
        setCornerRadius(CornerUtils.cornerLeftRadius(owner, radius));
    }

    @Override
    public void setCornerRadiusTopLeft(float radius) {
        setCornerRadius(CornerUtils.cornerTopLeftRadius(owner, radius));
    }

    @Override
    public void setCornerRadiusTopRight(float radius) {
        setCornerRadius(CornerUtils.cornerTopRightRadius(owner, radius));
    }

    @Override
    public void setCornerRadiusBottomRight(float radius) {
        setCornerRadius(CornerUtils.cornerBottomRightRadius(owner, radius));
    }

    @Override
    public void setCornerRadiusBottomLeft(float radius) {
        setCornerRadius(CornerUtils.cornerBottomLeftRadius(owner, radius));
    }

    @Override
    public View getView() {
        return this;
    }

    public float[] getCornerRadius() {
        return background.getCornerRadii();
    }

    public void setBorder(int color, float width) {
        foreground.setStroke(SizeUtils.dipToPx(width, owner), color);
    }

    public Context getOwner() {
        return owner;
    }

    public void addCentered(View child) {
        addAligned(child, Alignment.CENTER);
    }

    public void addCentered(int index, View child) {
        addAligned(index, child, Alignment.CENTER);
    }

    public void addAligned(View child, Alignment alignment) {
        addView(child);
        AlignUtils.alignInFrame(child, alignment);
    }

    public void addAligned(int index, View child, Alignment alignment) {
        addView(child, index);
        AlignUtils.alignInFrame(child, alignment);
    }

    public void addAligned(View child, Alignment alignment, int index) {
        addView(child, index);
        AlignUtils.alignInFrame(child, alignment);
    }

    @Override
    public void addView(View child) {
        if(Thread.currentThread() != Looper.getMainLooper().getThread()
                && isAttachedToWindow())
            ErrorHandler.handle(new RuntimeException("modifying ui from the wrong thread"),
                    "adding view to " + getClass().getSimpleName());
        if(child.getParent() == this) {
            return;
        }
        if(child.getParent() != null) {
            ((ViewGroup)child.getParent()).removeView(child);
        }
        try {
            super.addView(child);
        } catch (Exception x) {
            ErrorHandler.handle(x, "adding view to " + getClass().getSimpleName());
        }
    }

    public void addViews(View... views) {
        for(View view:views) {
            addView(view);
        }
    }

    @Override
    public void removeView(View view) {
        if(Thread.currentThread() != Looper.getMainLooper().getThread() && isAttachedToWindow())
            ErrorHandler.handle(new RuntimeException("modifying ui from the wrong thread"),
                    "removing view from " + getClass().getSimpleName());
        try {
            super.removeView(view);
        } catch (Exception x) {
            ErrorHandler.handle(x, "removing view from " + getClass().getSimpleName());
        }
    }

    @Override
    public void removeAllViews() {
        if(Thread.currentThread() != Looper.getMainLooper().getThread() && isAttachedToWindow())
            ErrorHandler.handle(new RuntimeException("modifying ui from the wrong thread"),
                    "removing views from " + getClass().getSimpleName());
        try {
            super.removeAllViews();
        } catch (Exception x) {
            ErrorHandler.handle(x, "removing views from " + getClass().getSimpleName());
        }
    }

    @Override
    public void addView(View child, int index) {
        if(Thread.currentThread() != Looper.getMainLooper().getThread() && isAttachedToWindow())
            ErrorHandler.handle(new RuntimeException("modifying ui from the wrong thread"),
                    "adding view to " + getClass().getSimpleName());
        try {
            super.addView(child, index);
        } catch (Exception x) {
            ErrorHandler.handle(x, "adding view from " + getClass().getSimpleName());
        }
    }

    public boolean isNotLaidOut() {
        return !isLaidOut();
    }
}
