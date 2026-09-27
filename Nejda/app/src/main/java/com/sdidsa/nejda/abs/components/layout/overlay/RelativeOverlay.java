package com.sdidsa.nejda.abs.components.layout.overlay;

import android.content.Context;
import android.view.View;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.base.ValueAnimation;
import com.sdidsa.nejda.abs.animation.base.ViewAnimation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.LinearHeightAnimation;
import com.sdidsa.nejda.abs.animation.view.WidthAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateXAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateYAnimation;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.ColoredStackPane;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;


public class RelativeOverlay extends Overlay {
    private final ValueAnimation translateX;
    private final ViewAnimation viewTranslateX;
    private final ValueAnimation translateY;
    private final ViewAnimation viewTranslateY;
    private final ValueAnimation linearHeight;
    private final ValueAnimation linearWidth;

    protected final ColoredStackPane root;

    public RelativeOverlay(Context owner) {
        super(owner);

        root = new ColoredStackPane(owner, Style.BACK_TER);
        root.setCornerRadius(20);

        translateX = new TranslateXAnimation(root, 0, 0);
        viewTranslateX = new TranslateXAnimation(root, 0, 0);
        translateY = new TranslateYAnimation(root, 0);
        viewTranslateY = new TranslateYAnimation(root, 0);
        linearWidth = new WidthAnimation(root, 0);
        linearHeight = new LinearHeightAnimation(root, 0);

        Animation extraShow = new ParallelAnimation(300)
                .addAnimation(translateX)
                .addAnimation(translateY)
                .addAnimation(viewTranslateX)
                .addAnimation(viewTranslateY)
                .addAnimation(linearWidth)
                .addAnimation(new AlphaAnimation(root, 0, 1))
                .addAnimation(linearHeight);

        Animation extraHide = extraShow.reverse();

        addToShow(extraShow);
        addToHide(extraHide);

        setInterpolator(Interpolator.EASE_OUT);

        root.setAlpha(0);
        root.setOnClickListener(e -> {
            //IGNORE
        });
        addAligned(root, Alignment.TOP_LEFT);
    }

    public void show(View view) {
        int[] loc = new int[2];
        ContextUtils.getLocationOnScreen(view, loc);
        int x = loc[0];
        int y = loc[1];
        int sh = ContextUtils.getScreenHeight(owner);
        int sw = ContextUtils.getScreenWidth(owner);

        int pad = SizeUtils.dipToPx(15, owner);

        float ty = sh / 4f;
        float tw = sw - pad * 2;

        viewTranslateX.setView(view);
        viewTranslateY.setView(view);
        viewTranslateX.setFrom(0);
        viewTranslateY.setFrom(0);
        viewTranslateX.setTo((tw - view.getWidth()) / 2f + pad - x);
        viewTranslateY.setTo(ty - y);

        translateX.setFrom(x);
        translateX.setTo((float) pad);
        translateY.setFrom(y);
        translateY.setTo(ty);
        linearWidth.setFrom(view.getWidth());
        linearWidth.setTo(tw);
        linearHeight.setFrom(view.getHeight());
        linearHeight.setTo(sh / 2f);

        root.setTranslationX(-1);
        view.setTranslationX(-1);

        //extraShow.update(0);

        super.show();
    }

    @Override
    public void show() {
        ErrorHandler.handle(new UnsupportedOperationException("you shouldn't call show() on " +
                "RelativeOverlay without passing the View responsible of the event, " +
                        "as it is needed for the fadeIn animation"),
                "showing RelativeOverlay");
    }

    private Animation anim;
    @Override
    public void applySystemInsets(Insets insets) {
        if(isAnimating()) return;
        int sh = ContextUtils.getScreenHeight(owner);

        int pad = SizeUtils.dipToPx(15, owner);

        float ty = sh / 4f;
        float th = sh / 2f;

        float maxHeight = sh - (pad * 2) - (insets.top + insets.bottom);
        th = Math.min(th, maxHeight);
        float maxy = sh - pad - insets.bottom - th;
        ty = Math.min(ty, maxy);

        translateY.setTo(ty);

        if(anim != null && anim.isRunning()) anim.stop();


        anim = new ParallelAnimation(300)
                .addAnimation(new TranslateYAnimation(root, ty))
                .addAnimation(new LinearHeightAnimation(root, th))
                .setInterpolator(Interpolator.EASE_OUT)
                .start();
    }
}
