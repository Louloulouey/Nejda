package com.sdidsa.nejda.view.app_content.session.home.maps.camera.gestures;

import androidx.annotation.NonNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ZoomListener implements ZoomDetector.OnZoomGestureListener {
    private final Supplier<Float> getRadius;
    private final Consumer<Float> setRadius;

    private final float sensitivity;

    public ZoomListener(float sensitivity, Supplier<Float> getRadius, Consumer<Float> setRadius) {
        this.sensitivity = sensitivity;
        this.getRadius = getRadius;
        this.setRadius = setRadius;
    }

    @Override
    public boolean onZoom(@NonNull ZoomDetector detector) {
        float zoomFactor = detector.getZoomFactor() - 1;
        float radius = getRadius.get();
        setRadius.accept(radius / (1 + zoomFactor * sensitivity));
        return true;
    }
}