package com.sdidsa.nejda.abs.components.controls.scratches.loading;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import com.sdidsa.nejda.abs.animation.view.scale.ScaleXYAnimation;
import com.sdidsa.nejda.abs.components.layout.StackPane;

import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateXAnimation;
import com.sdidsa.nejda.abs.components.controls.shape.Rectangle;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.utils.view.LocaleUtils;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;

public class LinearLoading extends HBox implements Loading{
    private static final int count = 4;
    private final Rectangle[] rectangles;
    private final ParallelAnimation loader;
    private final float size;

    public LinearLoading(Context owner) {
        this(owner, 16);
    }

    public LinearLoading(Context owner, float size) {
        super(owner);
        setAlpha(.6f);
        this.size = size;

        rectangles = new Rectangle[count];

        StackPane.LayoutParams params = new StackPane.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        setLayoutParams(params);

        setHorizontalGravity(Gravity.CENTER);

        for(int i = 0; i < count; i++) {
            rectangles[i] = new Rectangle(owner);
            rectangles[i].setSize(size, size);
            rectangles[i].setRadius(size);
            addView(rectangles[i]);
        }

        float shift = SizeUtils.dipToPx(-(size * 2), owner);

        loader = new ParallelAnimation(500)
                .addAnimation(new AlphaAnimation(rectangles[0],0, 1))
                .addAnimation(new ScaleXYAnimation(rectangles[0],.5f, 1))
                .addAnimation(new AlphaAnimation(rectangles[count -1], 1, 0))
                .addAnimation(new ScaleXYAnimation(rectangles[count - 1], 1, 0.5f))
                .setDisableTimeScale(true)
                .setInterpolator(Interpolator.EASE_OUT).setCycleCount(Animation.INDEFINITE);
        for(int i = 0; i < count; i++) {
            loader.addAnimation(new TranslateXAnimation(rectangles[i], shift, 0));
        }

        for(int i = 1; i < count; i++) {
            MarginUtils.setMarginLeft(rectangles[i], owner, size);
        }

        setTranslationX((-shift / 2) * LocaleUtils.getLocaleDirection(owner));

        setSize(size);
    }

    public void setSize(float size) {
        float shift = SizeUtils.dipToPx(-(size * 2), owner);

        for(Rectangle rect : rectangles) {
            rect.setTranslationX(shift * LocaleUtils.getLocaleDirection(owner));
        }
        rectangles[0].setAlpha(0);
        rectangles[count - 1].setAlpha(1);

        setTranslationX((-shift / 2) * LocaleUtils.getLocaleDirection(owner));

        if(loader != null && loader.isRunning()) {
            loader.stop();
        }
    }

    private boolean running = false;

    @Override
    protected void onAttachedToWindow() {
        if(running)
            loader.start();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        if(running)
            loader.stop();
        super.onDetachedFromWindow();
    }

    public void startLoading() {
        setSize(size);
        running = true;
        loader.start();
    }

    public void stopLoading() {
        running = false;
        loader.stop();
    }

    @Override
    public View getLoadingView() {
        return this;
    }

    public void setColor(int c) {
        for(Rectangle r : rectangles) {
            r.setFill(c);
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    public void setFill(int fill) {
        setColor(fill);
    }
}
