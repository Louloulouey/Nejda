package com.sdidsa.nejda.view.app_content.session.report;


import android.content.Context;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.view.RotateAnimation;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;

public class SendReport extends ColoredIcon {

    public SendReport(Context owner) {
        super(owner, Style.ONACCENT, Style.ACCENT,
                R.drawable.send, 68);
        setPadding(15);
        setCornerRadius(68);
        MarginUtils.setMarginUnified(this, owner, 5);
        setAutoMirror(true);
    }

    Animation loading;

    public void startLoading() {
        if (loading == null) {
            loading = new RotateAnimation(2000, this, 0, 360);
            loading.setCycleCount(Animation.INDEFINITE);
        }
        setImageResource(R.drawable.loading);
        loading.start();
    }

    public void stopLoading() {
        loading.stop();
        setRotation(0);
        setImageResource(R.drawable.send);
    }

    public boolean isLoading() {
        return loading != null && loading.isRunning();
    }
}
