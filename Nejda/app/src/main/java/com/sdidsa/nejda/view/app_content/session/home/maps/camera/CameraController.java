package com.sdidsa.nejda.view.app_content.session.home.maps.camera;

import android.view.MotionEvent;

import androidx.annotation.Size;

import com.google.android.filament.Camera;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.base.ValueAnimation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.view.app_content.session.home.maps.camera.gestures.PanDetector;
import com.sdidsa.nejda.view.app_content.session.home.maps.camera.gestures.PanListener;
import com.sdidsa.nejda.view.app_content.session.home.maps.camera.gestures.RotateDetector;
import com.sdidsa.nejda.view.app_content.session.home.maps.camera.gestures.RotateListener;
import com.sdidsa.nejda.view.app_content.session.home.maps.camera.gestures.TiltDetector;
import com.sdidsa.nejda.view.app_content.session.home.maps.camera.gestures.TiltListener;
import com.sdidsa.nejda.view.app_content.session.home.maps.camera.gestures.ZoomDetector;
import com.sdidsa.nejda.view.app_content.session.home.maps.camera.gestures.ZoomListener;

public class CameraController {
    public static final float RADIUS = 4, ROTATE = 0, TILT = 50;

    private final Camera camera;

    private float radius = RADIUS;
    private float rotate = ROTATE;
    private float tilt = TILT;
    private float camX = 0;
    private float camY = 0;

    private final ZoomDetector scaleDetector;
    private final RotateDetector rotateDetector;
    private final PanDetector panDetector;
    private final TiltDetector tiltDetector;

    public CameraController(Camera camera) {
        this.camera = camera;

        scaleDetector = new ZoomDetector(
                new ZoomListener(0.4f,
                        this::getRadius,
                        this::setRadius
                ));
        rotateDetector = new RotateDetector(
                new RotateListener(0.7f,
                        this::getRotate,
                        this::setRotate
                ));
        panDetector = new PanDetector(
                new PanListener(0.001f,
                        this::getCamX,
                        this::getCamY,
                        this::getRotate,
                        this::setCamX,
                        this::setCamY
                ));
        tiltDetector = new TiltDetector(
                new TiltListener(0.1f,
                        this::getTilt,
                        this::setTilt
                ));
    }

    public float getTilt() {
        return tilt;
    }

    public void setTilt(float tilt) {
        this.tilt = Math.max(15, Math.min(85, tilt));
    }

    public float getCamX() {
        return camX;
    }

    public float getCamY() {
        return camY;
    }

    public void setCamX(float camX) {
        this.camX = camX;
    }

    public void setCamY(float camY) {
        this.camY = camY;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = Math.max(0.4f, Math.min(6, radius));
    }

    public float getRotate() {
        return rotate;
    }

    public void setRotate(float rotate) {
        if (rotate > 180) {
            rotate -= 360;
        } else if (rotate < -180) {
            rotate += 360;
        }
        this.rotate = rotate;
    }

    public void onTouch(MotionEvent event) {
        rotateDetector.onTouchEvent(event);
        scaleDetector.onTouchEvent(event);
        panDetector.onTouchEvent(event);
        tiltDetector.onTouchEvent(event);
    }

    private Animation running;

    public void animateCameraTo(CameraPosition zone) {
        float fCamX = this.camX;
        float fCamY = this.camY;
        float fRadius = this.radius;
        float fRotate = this.rotate;
        float fTilt = this.tilt;

        if (running != null && running.isRunning()) running.stop();

        running = new ParallelAnimation(300)
                .addAnimation(new ValueAnimation(fCamX, zone.centerX()) {
                    @Override
                    public void updateValue(float v) {
                        CameraController.this.camX = v;
                    }
                }).addAnimation(new ValueAnimation(fCamY, zone.centerY()) {
                    @Override
                    public void updateValue(float v) {
                        CameraController.this.camY = v;
                    }
                }).addAnimation(new ValueAnimation(fRadius, zone.radius()) {
                    @Override
                    public void updateValue(float v) {
                        CameraController.this.radius = v;
                    }
                }).addAnimation(new ValueAnimation(fRotate, zone.rotate()) {
                    @Override
                    public void updateValue(float v) {
                        CameraController.this.rotate = v;
                    }
                }).addAnimation(new ValueAnimation(fTilt, zone.tilt()) {
                    @Override
                    public void updateValue(float v) {
                        CameraController.this.tilt = v;
                    }
                })
                .setInterpolator(Interpolator.EASE_OUT)
                .start();
    }

    public @Size(min = 9) float[] getCameraTransform() {
        float yaw = (float) Math.toRadians(rotate);
        float pitch = (float) Math.toRadians(tilt);

        float x = (float) (radius * Math.cos(pitch) * Math.sin(yaw));
        float y = (float) (radius * Math.sin(pitch));
        float z = (float) (radius * Math.cos(pitch) * Math.cos(yaw));


        return new float[]{
                camX + x, y, camY + z,
                camX, 0, camY,
                0, 1, 0
        };
    }

    public void lookAt() {
        float[] transform = getCameraTransform();
        camera.lookAt(
                transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5],
                transform[6], transform[7], transform[8]
        );
    }
}
