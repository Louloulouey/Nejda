package com.sdidsa.nejda.abs.animation.base;

import android.view.View;

public abstract class ViewAnimation extends ValueAnimation {
    private View view;

    public ViewAnimation(long duration, View view, float from, float to) {
        super(duration, from, to);
        this.view = view;
    }

    public ViewAnimation(View view, float from, float to) {
        this(300, view, from, to);
    }

    public ViewAnimation(View view, float to) {
        this(view, Float.MIN_VALUE, to);
    }

    public ViewAnimation(long duration, View view, float to) {
        this(duration, view, Float.MIN_VALUE, to);
    }

    public void setView(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    protected boolean initialFrom = false;
    @Override
    public void init() {
        super.init();
        if(lateFrom == null && (getFrom() == Float.MIN_VALUE || initialFrom)) {
            initialFrom = true;
            setFrom(getFrom(view));
        }
    }

    @Override
    public void updateValue(float v) {

        apply(view, v);
    }

    protected abstract float getFrom(View view);

    protected abstract void apply(View view, float v);
}
