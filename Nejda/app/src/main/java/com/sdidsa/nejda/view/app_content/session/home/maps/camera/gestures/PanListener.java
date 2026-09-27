package com.sdidsa.nejda.view.app_content.session.home.maps.camera.gestures;

import androidx.annotation.NonNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PanListener implements PanDetector.OnPanGestureListener {
    private final float sensitivity;
    private final Supplier<Float> getX, getY, getRotate;
    private final Consumer<Float> setX, setY;

    public PanListener(float sensitivity,
                       Supplier<Float> getX, Supplier<Float> getY, Supplier<Float> getRotate,
                       Consumer<Float> setX, Consumer<Float> setY) {
        this.sensitivity = sensitivity;
        this.getX = getX;
        this.getY = getY;
        this.getRotate = getRotate;
        this.setX = setX;
        this.setY = setY;
    }

    @Override
    public void onPan(@NonNull PanDetector detector) {
        float radians = (float) Math.toRadians(-getRotate.get());

        float rotatedDx = (float)(detector.getDeltaX() * Math.cos(radians)
                - detector.getDeltaY() * Math.sin(radians));
        float rotatedDy = (float)(detector.getDeltaX() * Math.sin(radians)
                + detector.getDeltaY() * Math.cos(radians));

        setX.accept(getX.get() - rotatedDx * sensitivity);
        setY.accept(getY.get() - rotatedDy * sensitivity);
    }
}