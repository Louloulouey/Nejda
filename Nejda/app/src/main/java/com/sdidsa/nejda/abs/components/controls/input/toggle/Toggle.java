package com.sdidsa.nejda.abs.components.controls.input.toggle;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.sdidsa.nejda.abs.animation.base.ColorAnimation;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;

import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateXAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateYAnimation;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.data.property.Property;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

import java.util.function.Consumer;

public class Toggle extends StackPane implements Styleable {
    private static final Interpolator interpolator = Interpolator.EASE_OUT;

    private final GradientDrawable trackBack;
    private final GradientDrawable thumbBack;
    private final StackPane thumb;
    private final View track;

    private float sizeDp;
    private int sizePx;

    private Consumer<Boolean> onChange;
    private Consumer<Boolean> postChange;

    public Toggle(Context owner) {
        this(owner, 24);
    }

    public Toggle(Context owner, float sizeDp) {
        super(owner);
        this.sizeDp = sizeDp;

        trackBack = new GradientDrawable();
        track = new View(owner);
        track.setBackground(trackBack);

        thumbBack = new GradientDrawable();
        thumb = new StackPane(owner);
        thumb.setBackground(thumbBack);

        setSize(sizeDp);

        setOnClickListener((v) -> toggle());

        applyStyle(StyleUtils.getStyle(owner));
    }

    public void setSize(float sizeDp) {
        this.sizeDp = sizeDp;
        sizePx = SizeUtils.dipToPx(sizeDp, owner);

        setLayoutParams(new LinearLayout.LayoutParams((int) (sizePx * 2.5f), sizePx));
        trackBack.setCornerRadius(sizePx / 4f);
        track.setLayoutParams(new LayoutParams((int) (sizePx * 1.5f), sizePx / 2));
        MarginUtils.setMarginHorizontal(track, owner, sizeDp / 2);
        thumbBack.setCornerRadius(sizePx / 2f);
        thumb.setElevation(sizeDp / 3);
        thumb.setLayoutParams(new LayoutParams(sizePx, sizePx));

        removeAllViews();

        addAligned(track, Alignment.CENTER);
        addView(thumb);
    }

    private ColoredIcon oldIcon;
    public void setIcon(@DrawableRes int res) {
        setIcon(res, 0);
    }

    public void setIcon(@DrawableRes int res, float padding) {
        int direction = state.get() ? 1 : -1;

        ColoredIcon icon = new ColoredIcon(owner, Style.BACK_PRI, res);
        icon.setSize(sizeDp);
        icon.setAlpha(0f);
        icon.setTranslationY(sizePx * direction);
        icon.setPadding(padding);
        ParallelAnimation set = new ParallelAnimation(400)
                .addAnimation(new TranslateYAnimation(icon, 0))
                .addAnimation(new AlphaAnimation(icon, 1))
                .setInterpolator(interpolator);

        if(oldIcon != null) {
            final ColoredIcon old = oldIcon;
            set.addAnimation(new TranslateYAnimation(old, -sizePx * direction))
                    .addAnimation(new AlphaAnimation(old, 0))
                    .setOnFinished(() -> thumb.removeView(old));
        }
        thumb.addView(icon);

        oldIcon = icon;
        set.start();
    }

    private final Property<Boolean> state = new Property<>(false);
    public void toggle() {
        if(state.get()) {
            disable();
        }else {
            enable();
        }
    }

    public void enable() {
        if(isEnabled()) return;
        state.set(true);

        if(onChange != null) onChange.accept(true);

        int from = StyleUtils.getStyle(owner).get().getTextSecondary();
        int to = StyleUtils.getStyle(owner).get().getTextNormal();

        new ParallelAnimation(400)
                .addAnimation(new TranslateXAnimation(thumb, sizePx * 1.5f))
                .addAnimation(new ColorAnimation(from, to) {
                    @Override
                    public void updateValue(int color) {
                        thumbBack.setColor(color);
                    }
                })
                .setInterpolator(interpolator)
                .setOnFinished(() -> {
                    if(postChange != null) postChange.accept(true);
                }).start();
    }

    public void disable() {
        if(!isEnabled()) return;
        state.set(false);

        if(onChange != null) onChange.accept(false);

        int from = StyleUtils.getStyle(owner).get().getTextNormal();
        int to = StyleUtils.getStyle(owner).get().getTextSecondary();

        new ParallelAnimation(400)
                .addAnimation(new TranslateXAnimation(thumb, 0))
                .addAnimation(new ColorAnimation(from, to) {
                    @Override
                    public void updateValue(int color) {
                        thumbBack.setColor(color);
                    }
                })
                .setInterpolator(interpolator)
                .setOnFinished(() -> {
                    if(postChange != null) postChange.accept(false);
                }).start();
    }

    public boolean isEnabled() {
        return state.get();
    }

    public void setEnabled(boolean enabled) {
        if(enabled) {
            enable();
        }else {
            disable();
        }
    }

    public Property<Boolean> enabledProperty() {
        return state;
    }

    public void setOnChange(Consumer<Boolean> onChange) {
        this.onChange = onChange;
    }

    public void setPostChange(Consumer<Boolean> postChange) {
        this.postChange = postChange;
    }

    @Override
    public void applyStyle(Style style) {
        trackBack.setColor(style.getTextMuted());
        thumbBack.setColor(isEnabled() ? style.getTextNormal() : style.getTextSecondary());
    }

}
