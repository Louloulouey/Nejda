package com.sdidsa.nejda.abs.animation.combine;

import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.utils.Platform;

import java.util.ArrayList;

public class SequenceAnimation extends Animation implements CombineAnimation{
    private final ArrayList<Animation> animations;
    private Animation running = null;
    private long delay = 0;
    private boolean isRunning;

    public SequenceAnimation(long duration) {
        super(duration);
        animations = new ArrayList<>();
    }

    public SequenceAnimation setDelay(long delay) {
        this.delay = delay;
        return this;
    }

    public SequenceAnimation addAnimation(Animation animation) {
        animation.setDuration(getDuration());
        animation.setInterpolator(interpolator);
        this.animations.add(animation);
        return this;
    }

    public ArrayList<Animation> getAnimations() {
        return animations;
    }

    @Override
    public void init() {
        for(Animation animation : animations) {
            animation.init();
        }
        super.init();
    }

    @Override
    public boolean isRunning() {
        for(Animation a : animations) {
            if(a.isRunning()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T extends Animation> T start() {
        isRunning = true;
        Platform.runBack(() -> {
            for (int i = 0; i < animations.size() && !Thread.currentThread().isInterrupted(); i++) {
                if(!isRunning) break;
                Animation current = animations.get(i);
                current.start();
                running = current;
                Platform.sleep((getDuration() + delay));
            }

            if(animations.isEmpty()) {
                if(getOnFinished() != null) {
                    Platform.runLater(getOnFinished());
                }
            } else {
                Animation last = animations.get(animations.size() - 1);
                last.setOnFinished(getOnFinished());
            }
        });
        return null;
    }

    @Override
    public void stop() {
        isRunning = false;

        if (running != null)
            running.stop();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SequenceAnimation setInterpolator(Interpolator interpolator) {
        for (Animation animation : animations) {
            animation.setInterpolator(interpolator);
        }
        this.interpolator = interpolator;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SequenceAnimation setCycleCount(int cycleCount) {
        for (Animation animation : animations) {
            animation.setCycleCount(cycleCount);
        }
        return this;
    }

    @Override
    public void update(float v) {
        //IGNORE
    }

}
