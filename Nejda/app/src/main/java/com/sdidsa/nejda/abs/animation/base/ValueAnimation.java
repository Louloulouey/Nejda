package com.sdidsa.nejda.abs.animation.base;

import java.util.function.Supplier;

public abstract class ValueAnimation extends Animation {
    private float from, to;

    protected Supplier<Float> lateTo;
    protected Supplier<Float> lateFrom;

    public ValueAnimation(long duration, float from, float to) {
        super(duration);
        this.from = from;
        this.to = to;
    }

    public ValueAnimation(float from, float to) {
        super();
        this.from = from;
        this.to = to;
    }

    public ValueAnimation setLateTo(Supplier<Float> lateTo) {
        this.lateTo = lateTo;
        return this;
    }

    public ValueAnimation setLateToInt(Supplier<Integer> lateTo) {
        this.lateTo = () -> (float) lateTo.get();
        return this;
    }

    public ValueAnimation setLateFrom(Supplier<Float> lateFrom) {
        this.lateFrom = lateFrom;
        return this;
    }

    public ValueAnimation setLateFromInt(Supplier<Integer> lateFrom) {
        this.lateFrom = () -> (float) lateFrom.get();
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends ValueAnimation> T setFrom(float from) {
        this.from = from;
        return (T) this;
    }

    public void setTo(float to) {
        this.to = to;
    }

    public float getTo() {
        return to;
    }

    public float getFrom() {
        return from;
    }

    @Override
    public void init() {
        super.init();
        if(lateFrom != null) {
            setFrom(lateFrom.get());
        }
        if (lateTo != null) {
            setTo(lateTo.get());
        }
    }

    @Override
    public void update(float v) {
        updateValue(from + (to - from) * v);
    }

    public abstract void updateValue(float v);
}
