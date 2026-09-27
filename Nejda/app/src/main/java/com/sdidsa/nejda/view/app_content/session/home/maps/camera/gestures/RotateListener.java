package com.sdidsa.nejda.view.app_content.session.home.maps.camera.gestures;

import androidx.annotation.NonNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RotateListener implements RotateDetector.OnRotateGestureListener {
    private final Supplier<Float> getAngle;
    private final Consumer<Float> setAngle;

    private final float sensitivity;

    public RotateListener(float sensitivity, Supplier<Float> getAngle, Consumer<Float> setAngle) {
        this.sensitivity = sensitivity;
        this.getAngle = getAngle;
        this.setAngle = setAngle;
    }

    @Override
    public boolean onRotate(@NonNull RotateDetector detector) {
        float angleDelta = detector.getRotationDelta();
        float angle = getAngle.get();
        setAngle.accept(angle + angleDelta * sensitivity);
        return true;
    }
}