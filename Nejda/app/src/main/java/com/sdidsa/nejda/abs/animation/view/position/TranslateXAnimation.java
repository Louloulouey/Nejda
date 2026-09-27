package com.sdidsa.nejda.abs.animation.view.position;

import android.view.View;

import com.sdidsa.nejda.abs.animation.base.ValueAnimation;
import com.sdidsa.nejda.abs.animation.base.ViewAnimation;
import com.sdidsa.nejda.abs.utils.view.LocaleUtils;

public class TranslateXAnimation extends ViewAnimation {

    public TranslateXAnimation(View view, float to) {
        super(view, to);
    }

    public TranslateXAnimation(View view,float from, float to) {
        super(view, from, to);
    }

    public TranslateXAnimation(long duration, View view, float to) {
        super(duration, view, to);
    }

    private int direction = 1;
    @Override
    public void init() {
        super.init();

        int newDirection = LocaleUtils.getLocaleDirection(getView());

        if(newDirection != direction) {
            if(!initialFrom) {
                setFrom(getFrom() * -1);
            }
            setTo(getTo() * -1);
        }
        direction = newDirection;
    }

    public void reset() {
        this.direction = 1;
    }

    @Override
    public void setTo(float to) {
        super.setTo(to);
        reset();
    }

    @Override
    public <T extends ValueAnimation> T setFrom(float from) {
        reset();
        return super.setFrom(from);
    }

    @Override
    protected float getFrom(View view) {
        return view.getTranslationX();
    }

    @Override
    protected void apply(View view, float v) {
        view.setTranslationX(v);
    }
}
