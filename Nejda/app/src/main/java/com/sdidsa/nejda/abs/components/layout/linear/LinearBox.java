package com.sdidsa.nejda.abs.components.layout.linear;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.core.content.res.ResourcesCompat;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.Gradient;
import com.sdidsa.nejda.abs.components.layout.GradientStop;
import com.sdidsa.nejda.abs.components.layout.abs.CornerUtils;
import com.sdidsa.nejda.abs.components.layout.abs.Cornered;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.data.property.Property;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;

import java.util.List;

public class LinearBox extends LinearLayout implements Cornered {
    private final Property<Float> spacing;
    protected final Context owner;

    private final GradientDrawable background;

    private final GradientDrawable foreground;

    public LinearBox(Context owner) {
        super(owner);
        this.owner = owner;

        background = new GradientDrawable();
        foreground = new GradientDrawable();
        setBackground(background);
        setForeground(foreground);

        setOutlineProvider(ViewOutlineProvider.BACKGROUND);

        spacing = new Property<>(0f);
        spacing.addListener((ov, nv) -> applySpacing(nv));

        setClipChildren(false);
        setClipToPadding(false);
        setClipToOutline(false);
    }

    private void applySpacing(double spacing) {
        int rs = SizeUtils.dipToPx(spacing, owner);
        GradientDrawable divider = (GradientDrawable) ResourcesCompat.getDrawable(
                owner.getResources(), R.drawable.divider_shape,null);
        if(divider==null) {
            return;
        }
        if(this instanceof HBox) {
            divider.setSize(rs, 1);
        } else {
            divider.setSize(1, rs);
        }
        setDividerPadding(0);
        setDividerDrawable(divider);
        setShowDividers(SHOW_DIVIDER_MIDDLE);
    }

    public void setSpacing(float spacing) {
        this.spacing.set(spacing);

        if(this instanceof HBox) {
            ErrorHandler.handle(new RuntimeException("you shouldn't set the spacing on " + getClass().getSimpleName() +
                    " (it won't work in RTL)"), "setting spacing of linear box");
        }
    }

    public void setPadding(float padding) {
        PaddingUtils.setPaddingUnified(this, padding, owner);
    }

    public void setBackground(int color) {
        background.setColor(color);
    }

    public void setBackgroundGradient(Gradient gradient) {
        List<GradientStop> stops = gradient.getStops();
        int[] colors = new int[stops.size()];
        float[] positions = new float[stops.size()];

        for (int i = 0; i < stops.size(); i++) {
            GradientStop stop = stops.get(i);
            colors[i] = stop.color();
            positions[i] = stop.position();
        }

        background.setOrientation(gradient.getOrientation());
        background.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        background.setColors(colors, positions);
    }

    public void setBorder(@ColorInt int color, float width) {
        background.setStroke(SizeUtils.dipToPx(width, owner), color);
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

    public void setAlignment(Alignment alignment) {
        setGravity(alignment.getGravity());
    }

    public Context getOwner() {
        return owner;
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
}
