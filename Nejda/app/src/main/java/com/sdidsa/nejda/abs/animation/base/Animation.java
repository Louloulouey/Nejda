package com.sdidsa.nejda.abs.animation.base;

import android.content.Context;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.sdidsa.nejda.abs.animation.combine.CombineAnimation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.combine.SequenceAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.easing.Linear;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateXAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateYAnimation;
import com.sdidsa.nejda.abs.animation.view.scale.ScaleXYAnimation;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;

public abstract class Animation {
    public static final String SLOW = "animation_slow";
    public static final String DEFAULT = "animation_default";
    public static final String FAST = "animation_fast";
    public static final String OFF = "animation_off";

    public static void applySpeed(String speed) {
        if (speed == null) {
            timeScale = 1f;
            return;
        }

        switch (speed.toLowerCase()) {
            case SLOW:
                timeScale = 2f;
                break;
            case FAST:
                timeScale = 0.65f;
                break;
            case OFF:
                timeScale = 0f;
                break;
            case DEFAULT:
            default:
                timeScale = 1f;
                break;
        }
    }

    public static final int INDEFINITE = -1;

    public static float timeScale = 1f;

    protected Interpolator interpolator = new Linear();
    private long duration;

    private Runnable before;
    private Runnable onFinished;

    private boolean disableTimeScale = false;

    private int cycleCount = 1;

    protected Animation(long duration) {
        this.duration = duration;
    }

    protected Animation() {
        this(0);
    }

    public void init() {
        if (before != null) {
            Platform.runLater(before);
        }
    }

    private long start;

    @SuppressWarnings("unchecked")
    public <T extends Animation> T start() {
        stop();
        init();
        start = System.nanoTime();
        Platform.runBack(this::preUpdate);
        return (T) this;
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public long getDuration() {
        return duration;
    }

    @SuppressWarnings("unchecked")
    public <T extends Animation> T setDuration(long duration) {
        this.duration = duration;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Animation> T setDisableTimeScale(boolean disableTimeScale) {
        this.disableTimeScale = disableTimeScale;
        return (T) this;
    }

    public Runnable getOnFinished() {
        return onFinished;
    }

    public Animation reverse() {
        Animation t = this;
        return new Animation(t.duration) {
            @Override
            public void update(float v) {
                t.update(1 - v);
            }

            @Override
            public void init() {
                t.init();
                t.update(1);
            }
        }
                .setOnFinished(t.onFinished)
                .setInterpolator(t.interpolator)
                .setBefore(t.before);
    }

    @SuppressWarnings("unchecked")
    public <T extends Animation> T setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Animation> T setBefore(Runnable before) {
        this.before = before;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Animation> T setCycleCount(int cycleCount) {
        this.cycleCount = cycleCount;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Animation> T setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return (T) this;
    }

    private long dt = 0;
    private float p = 0;
    private boolean running = false;
    int rep;

    private void preUpdate() {
        rep = 1;
        if (running) {
            start = System.nanoTime();
            return;
        }
        running = true;
        long d = disableTimeScale ? duration : (long) (duration * timeScale);
        if (d == 0) {
            running = false;
            Platform.runLater(() -> {
                update(1);
                if (onFinished != null) onFinished.run();
            });
            return;
        }
        Platform.runLater(() -> {
            Choreographer inst = Choreographer.getInstance();
            inst.postFrameCallback(new Choreographer.FrameCallback() {
                @Override
                public void doFrame(long now) {
                    if (!running) return;
                    dt = (now - start) / 1000000;
                    p = (float) (dt) / d;
                    p = p < 0 ? 0 : p > 1 ? 1 : p;
                    if (p >= 1) {
                        update(1f);
                        running = false;
                        if (onFinished != null) {
                            onFinished.run();
                        }
                        if (rep < cycleCount || cycleCount == INDEFINITE) {
                            running = true;
                            rep++;
                            start = System.nanoTime();
                        }
                    } else {
                        update(interpolator.getInterpolation(p));
                    }
                    inst.postFrameCallback(this);
                }
            });
        });
    }

    public abstract void update(float v);

    private String toString(int indent) {
        StringBuilder sb = new StringBuilder();

        sb.append('\n');
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
        sb.append(getClass().getSimpleName());

        if (this instanceof ViewAnimation va && va.getView() != null) {
            sb.append(" for ");
            sb.append(va.getView().getClass().getSimpleName());
        }

        if (this instanceof CombineAnimation pa) {
            for (Animation sa : pa.getAnimations()) {
                sb.append(sa.toString(indent + 1));
            }
        }
        return sb.toString();
    }

    @NonNull
    @Override
    public String toString() {
        return toString(0);
    }

    public static Animation fadeInUp(Context owner, View... views) {
        return fadeInUp(owner, 0, 1, views);
    }

    public static Animation fadeInUp(Context owner, int offset, int direction, View... views) {
        ParallelAnimation res = new ParallelAnimation(300);
        for (View view : views) {
            res.addAnimation(new AlphaAnimation(view, 0, 1));
            res.addAnimation(new TranslateYAnimation(view,
                    SizeUtils.by(owner) * direction + offset,
                    offset));
        }
        return res;
    }

    public static Animation fadeInUp(Context owner, float factor, View... views) {
        ParallelAnimation res = new ParallelAnimation(300);
        for (View view : views) {
            res.addAnimation(new AlphaAnimation(view, 0, 1));
            res.addAnimation(new TranslateYAnimation(view, SizeUtils.by(owner) * factor, 0));
        }
        return res;
    }

    public static Animation fadeInRight(Context owner, View view) {
        return new ParallelAnimation(300)
                .addAnimation(new AlphaAnimation(view, 1))
                .addAnimation(new TranslateXAnimation(view, -SizeUtils.by(owner), 0));
    }

    public static Animation fadeInLeft(Context owner, View view) {
        return fadeInLeft(owner, 1, view);
    }

    public static Animation fadeInLeft(Context owner, float factor, View...views) {
        ParallelAnimation anim = new ParallelAnimation(300);

        for(View view: views) {
            anim.addAnimation(new AlphaAnimation(view, 1))
                    .addAnimation(new TranslateXAnimation(view,
                            SizeUtils.by(owner) * factor, 0));
        }

        return anim;
    }

    public static Animation fadeOutUp(Context owner, View... views) {
        return fadeOutUp(owner, 1, views);
    }

    public static Animation fadeOutUp(Context owner, int direction, View... views) {
        ParallelAnimation res = new ParallelAnimation(300);
        for (View view : views) {
            res.addAnimation(new AlphaAnimation(view, 1, 0));
            res.addAnimation(new TranslateYAnimation(view, 0, -SizeUtils.by(owner) * direction));
        }
        return res;
    }

    public static Animation fadeInDown(Context owner, View...views) {
        ParallelAnimation res = new ParallelAnimation(300);
        for (View view : views) {
            res.addAnimation(new AlphaAnimation(view, 0, 1))
                    .addAnimation(new TranslateYAnimation(view, -SizeUtils.by(owner), 0));
        }
        return res;
    }

    public static Animation fadeOutDown(Context owner, View... views) {
        return fadeOutDown(owner, 0, views);
    }

    public static Animation fadeOutDown(Context owner, int offset, View... views) {
        ParallelAnimation res = new ParallelAnimation(300);
        for (View view : views) {
            res.addAnimation(new AlphaAnimation(view, 1, 0));
            res.addAnimation(new TranslateYAnimation(view, offset, SizeUtils.by(owner) + offset));
        }
        return res;
    }

    public static Animation fadeOutDown(Context owner, float factor, View... views) {
        ParallelAnimation res = new ParallelAnimation(300);
        for (View view : views) {
            res.addAnimation(new AlphaAnimation(view, 1, 0));
            res.addAnimation(new TranslateYAnimation(view, 0, SizeUtils.by(owner) * factor));
        }
        return res;
    }

    public static Animation fadeOutRight(Context owner, View view) {
        return fadeOutRight(owner, 1f, view);
    }

    public static Animation fadeOutRight(Context owner, float factor, View...views) {
        ParallelAnimation res = new ParallelAnimation(300);

        for (View view : views) {
            res.addAnimation(new AlphaAnimation(view, 1, 0))
                    .addAnimation(new TranslateXAnimation(view, 0,
                            SizeUtils.by(owner) * factor));
        }

        return res;
    }

    public static Animation fadeOutLeft(Context owner, View view) {
        return new ParallelAnimation(300)
                .addAnimation(new AlphaAnimation(view, 1, 0))
                .addAnimation(new TranslateXAnimation(view, 0, -SizeUtils.by(owner)));
    }

    public static Animation fadeInScaleUp(View... views) {
        ParallelAnimation res = new ParallelAnimation(300);
        for (View view : views) {
            res.addAnimation(new AlphaAnimation(view, 0, 1));
            res.addAnimation(new ScaleXYAnimation(view, downscale, 1));
        }
        return res;
    }

    public static Animation fadeInScaleUp(float targetAlpha, View... views) {
        ParallelAnimation res = new ParallelAnimation(300);
        for (View view : views) {
            res.addAnimation(new AlphaAnimation(view, targetAlpha).setLateFrom(view::getAlpha));
            res.addAnimation(new ScaleXYAnimation(view, downscale, 1));
        }
        return res;
    }

    public static Animation scaleUpIn(View... views) {
        ParallelAnimation res = new ParallelAnimation(300);
        for (View view : views) {
            view.setScaleX(downscale);
            view.setScaleY(downscale);
            res.addAnimation(new ScaleXYAnimation(view, 1));
        }
        return res;
    }

    public static Animation fadeOutScaleUp(View view) {
        view.setScaleX(1f);
        view.setScaleY(1f);
        view.setAlpha(1f);
        return new ParallelAnimation(300)
                .addAnimation(new AlphaAnimation(view, 0))
                .addAnimation(new ScaleXYAnimation(view, upscale));
    }

    public static Animation fadeInScaleDown(View view) {
        view.setScaleX(upscale);
        view.setScaleY(upscale);
        view.setAlpha(0f);
        return new ParallelAnimation(300)
                .addAnimation(new AlphaAnimation(view, 1))
                .addAnimation(new ScaleXYAnimation(view, 1));
    }

    public static Animation fadeOutScaleDown(View view) {
        view.setScaleX(1f);
        view.setScaleY(1f);
        view.setAlpha(1f);
        return new ParallelAnimation(300)
                .addAnimation(new AlphaAnimation(view, 0))
                .addAnimation(new ScaleXYAnimation(view, downscale));
    }

    public static Animation scaleDownOut(View... views) {
        ParallelAnimation res = new ParallelAnimation(300);
        for (View view : views) {
            view.setScaleX(1f);
            view.setScaleY(1f);
            res.addAnimation(new ScaleXYAnimation(view, downscale));
        }
        return res;
    }

    public static SequenceAnimation sequenceFadeInUpParent(Context owner, ViewGroup parent) {
        View[] vs = new View[parent.getChildCount()];

        for(int i = 0; i < vs.length; i++) {
            vs[i] = parent.getChildAt(i);
        }

        return sequenceFadeInUp(owner, vs);
    }

    public static SequenceAnimation sequenceFadeInUp(Context owner, View... views) {
        SequenceAnimation anim = new SequenceAnimation(300)
                .setInterpolator(Interpolator.OVERSHOOT)
                .setDelay(-250);
        for (View view : views) {
            view.setAlpha(0);
            anim.addAnimation(Animation.fadeInUp(owner, view));
        }
        return anim;
    }

    public static SequenceAnimation sequenceFadeOutRightParent(Context owner, ViewGroup parent) {
        View[] vs = new View[parent.getChildCount()];

        for(int i = 0; i < vs.length; i++) {
            vs[i] = parent.getChildAt(i);
        }

        return sequenceFadeOutRight(owner, vs);
    }

    public static SequenceAnimation sequenceFadeOutRight(Context owner, View... views) {
        SequenceAnimation anim = new SequenceAnimation(300)
                .setInterpolator(Interpolator.EASE_OUT)
                .setDelay(-250);
        for (View view : views) {
            view.setAlpha(1);
            anim.addAnimation(Animation.fadeOutRight(owner, view));
        }
        return anim;
    }

    public static Animation fadeIn(View view) {
        view.setAlpha(0f);
        return new AlphaAnimation(300, view, 1);
    }

    public static Animation fadeOut(View... views) {
        ParallelAnimation res = new ParallelAnimation(300);
        for (View view : views) {
            view.setAlpha(1f);
            res.addAnimation(new AlphaAnimation(300, view, 0));
        }
        return res;
    }

    private static final float upscale = 1.2f;
    private static final float downscale = .8f;
}
