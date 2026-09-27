package com.sdidsa.nejda.abs.components.layout.overlay;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.base.ColorAnimation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.Platform;

import java.util.ArrayList;

public abstract class Overlay extends StackPane {
    private final ParallelAnimation show, hide;
    private final ArrayList<Runnable> onHidden;
    private final ArrayList<Runnable> onHiding;
    private final ArrayList<Runnable> onShowing;
    private final ArrayList<Runnable> onShown;

    private final boolean autoHide = true;

    private boolean tint = true;

    public Overlay(Context owner) {
        super(owner);

        int shown = Color.argb(192, 0, 0, 0);
        int hidden = Color.argb(0, 0, 0, 0);
        setBackgroundColor(hidden);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        setFocusable(false);

        onHiding = new ArrayList<>();
        onHidden = new ArrayList<>();
        onShowing = new ArrayList<>();
        onShown = new ArrayList<>();

        show = new ParallelAnimation(300)
                .setOnFinished(() -> {
                    for(Runnable act : onShown) {
                        act.run();
                    }
                })
                .setInterpolator(Interpolator.OVERSHOOT);

        show.addAnimation(new ColorAnimation(hidden, shown) {
            @Override
            public void updateValue(int color) {
                if(tint)
                    setBackgroundColor(color);
            }
        });

        hide = new ParallelAnimation(300)
                .setInterpolator(Interpolator.EASE_OUT)
                .setOnFinished(() -> {
                    for(Runnable act : onHidden) {
                        act.run();
                    }
                    ContextUtils.removeOverlay(owner, this);
                });

        hide.addAnimation(new ColorAnimation(shown, hidden) {
            @Override
            public void updateValue(int color) {
                if(tint)
                    setBackgroundColor(color);
            }
        });

        setOnClickListener(e -> {if(autoHide) hide();});
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return getChildAt(0).requestFocus();
    }

    public void setInterpolator(Interpolator interpolator) {
        show.setInterpolator(interpolator);
        hide.setInterpolator(interpolator);
    }

    public void setDuration(long duration) {
        show.setDuration(duration);
        hide.setDuration(duration);
    }

    public void setHideInterpolator(Interpolator interpolator) {
        hide.setInterpolator(interpolator);
    }

    public void setHideDuration(long duration) {
        hide.setDuration(duration);
    }

    public void setTint(boolean tint) {
        this.tint = tint;
    }

    private boolean shown = false;
    public boolean isShown() {
        return shown;
    }

    public void show() {
        if(shown) return;
        ContextUtils.hideKeyboard(owner);
        shown = true;
        hide.stop();
        show.stop();
        for(Runnable act : onShowing) {
            act.run();
        }
        ContextUtils.addOverlay(owner, this);
        post(show::start);
    }

    public void hide() {
        if(!shown) return;
        ContextUtils.hideKeyboard(owner);
        shown = false;
        show.stop();
        hide.stop();
        for(Runnable act : onHiding) {
            act.run();
        }
        hide.start();
    }

    public void addToShow(Animation animation) {
        show.addAnimation(animation);
    }

    public void addToHide(Animation animation) {
        hide.addAnimation(animation);
    }

    public void addOnHiding(Runnable onHiding) {
        this.onHiding.add(onHiding);
    }

    public void addOnHidden(Runnable onHidden) {
        this.onHidden.add(onHidden);
    }

    public abstract void applySystemInsets(Insets insets);

    public void addOnHiddenOnce(Runnable onHidden) {
        addOnHidden(new Runnable() {
            @Override
            public void run() {
                onHidden.run();
                Platform.runLater(() -> Overlay.this.onHidden.remove(this));
            }
        });
    }

    public void addOnShownOnce(Runnable onShown) {
        addOnShown(new Runnable() {
            @Override
            public void run() {
                onShown.run();
                Platform.runLater(() -> Overlay.this.onShown.remove(this));
            }
        });
    }

    public void addOnHidingOnce(Runnable onHiding) {
        addOnHiding(new Runnable() {
            @Override
            public void run() {
                onHiding.run();
                Platform.runLater(() -> Overlay.this.onHiding.remove(this));
            }
        });
    }

    public void addOnShowing(Runnable onShown) {
        this.onShowing.add(onShown);
    }

    public void addOnShown(Runnable onShown) {
        this.onShown.add(onShown);
    }

    public void back() {
        if(autoHide)
            hide();
    }

    public boolean isShowing() {
        return show.isRunning();
    }

    public boolean isHiding() {
        return hide.isRunning();
    }

    public boolean isAnimating() {
        return isShowing() || isHiding();
    }
}
