package com.sdidsa.nejda.abs.components.layout.overlay;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;

import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateYAnimation;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredVBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;

public abstract class SlideOverlay extends Overlay {
    protected final ColoredVBox list;
    private float initTY, initPY, lastY, velocity;
    private long lastTime;

    public SlideOverlay(Context owner) {
        super(owner);

        list = new ColoredVBox(owner, Style.BACK_PRI);
        list.setTranslationY(ContextUtils.getScreenHeight(owner));
        list.setFocusable(false);
        list.setOnClickListener(e -> {
            //consume
        });

        addView(list);

        list.setElevation(SizeUtils.by(owner));

        addToShow(new TranslateYAnimation(list, 0)
                .setLateFrom(() -> (float) (list.getHeight() / 2)));
        addToShow(new AlphaAnimation(list, 1).setFrom(0));

        addToHide(new TranslateYAnimation(list, 0)
                .setLateTo(() -> list.getTranslationY() + (float) (list.getHeight() / 2)));
        addToHide(new AlphaAnimation(list, 0));

        Animation releaseShow = new TranslateYAnimation(300, list, 0)
                .setInterpolator(Interpolator.EASE_OUT);

        setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN -> {
                    initTY = event.getRawY();
                    initPY = list.getTranslationY();
                    lastY = initTY;
                }
                case MotionEvent.ACTION_UP -> {
                    float fdy = event.getRawY() - initTY;
                    if (Math.abs(fdy) < 10) {
                        v.performClick();
                    } else {
                        long dy = System.currentTimeMillis() - lastTime;
                        if (dy > 300) {
                            velocity = 0;
                        }
                        if (velocity > 10) {
                            hide();
                        } else if (velocity < -10) {
                            releaseShow.start();
                        } else {
                            int min = 0;
                            int max = list.getHeight();
                            int mid = (max + min) / 2;
                            if (list.getTranslationY() > mid) {
                                hide();
                            } else {
                                releaseShow.start();
                            }
                        }
                    }
                }
                case MotionEvent.ACTION_MOVE -> {
                    float nty = event.getRawY();
                    velocity = nty - lastY;
                    float dy = nty - initTY;
                    float ny = initPY + dy;
                    lastY = nty;
                    list.setTranslationY(Math.max(ny, 0));
                    lastTime = System.currentTimeMillis();
                }
            }
            return true;
        });
    }

    public ColoredVBox getRoot() {
        return list;
    }

    protected void setHeight(int height) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        params.gravity = Gravity.BOTTOM;
        list.setLayoutParams(params);
    }

    protected void setHeightFactor(double factor) {
        setHeight((int) (ContextUtils.getScreenHeight(owner) * factor));
    }
}
