package com.sdidsa.nejda.view.app_content.session.attachments;


import android.content.Context;

import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.WidthAnimation;
import com.sdidsa.nejda.abs.animation.view.margin.MarginLeftAnimation;
import com.sdidsa.nejda.abs.components.layout.ColoredStackPane;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;

import java.io.File;

public abstract class BasicAttachment extends ColoredStackPane {
    public BasicAttachment(Context owner) {
        super(owner);
    }

    public void hide(Runnable post) {
        boolean last = ((HBox) getParent()).getChildCount() == 1;
        new ParallelAnimation(300)
                .addAnimation(!last ?
                        new WidthAnimation(this, getWidth(), 0) :
                        new MarginLeftAnimation(this, 0))
                .addAnimation(new AlphaAnimation(this, 1, 0))
                .addAnimation(new MarginLeftAnimation(this, 0))
                .setOnFinished(post)
                .setInterpolator(Interpolator.EASE_OUT)
                .start();
    }

    public abstract File toFile(String name);
}
