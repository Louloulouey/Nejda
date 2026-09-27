package com.sdidsa.nejda.view.app_content.session.home.maps.camera.gestures;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

public class RotateDetector {
    public interface OnRotateGestureListener {
        boolean onRotate(@NonNull RotateDetector detector);
        default boolean onRotateBegin(@NonNull RotateDetector ignoredDetector) {
            return true;
        }
        default void onRotateEnd(@NonNull RotateDetector ignoredDetector) {}
    }

    private final OnRotateGestureListener mListener;

    private float mCurrAngle;
    private float mPrevAngle;
    private boolean mInProgress;

    private final int mAngleSlop;
    private final int mMinPointerCount;

    public RotateDetector(@NonNull OnRotateGestureListener listener) {
        this(1, 2, listener);
    }

    public RotateDetector(int angleSlop, int minPointerCount,
                          @NonNull OnRotateGestureListener listener) {
        mListener = listener;
        mAngleSlop = angleSlop;
        mMinPointerCount = minPointerCount;
    }

    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final int action = event.getActionMasked();
        final int count = event.getPointerCount();

        final boolean streamComplete = action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL;

        if (action == MotionEvent.ACTION_DOWN || streamComplete) {
            if (mInProgress) {
                mListener.onRotateEnd(this);
                mInProgress = false;
            }

            if (streamComplete) {
                return true;
            }
        }

        final boolean configChanged = action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_POINTER_UP
                || action == MotionEvent.ACTION_POINTER_DOWN;

        final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
        final int skipIndex = pointerUp ? event.getActionIndex() : -1;

        // Calculate focus point (center of rotation)
        float sumX = 0, sumY = 0;
        final int div = pointerUp ? count - 1 : count;

        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            sumX += event.getX(i);
            sumY += event.getY(i);
        }

        final float focusX = sumX / div;
        final float focusY = sumY / div;

        // Calculate rotation angle
        final float angle;
        if (count >= mMinPointerCount) {
            // We need at least two pointers for rotation
            float x1 = event.getX(0) - focusX;
            float y1 = event.getY(0) - focusY;

            // Use the second pointer that isn't being lifted
            int index = 1;
            if (skipIndex == 1) {
                index = skipIndex + 1 < count ? skipIndex + 1 : 0;
            }

            float x2 = event.getX(index) - focusX;
            float y2 = event.getY(index) - focusY;

            angle = calculateAngle(x1, y1, x2, y2);
        } else {
            angle = 0;
        }

        final boolean wasInProgress = mInProgress;

        if (mInProgress && (count < mMinPointerCount || configChanged)) {
            mListener.onRotateEnd(this);
            mInProgress = false;
        }

        if (configChanged) {
            mPrevAngle = mCurrAngle = angle;
        }

        if (!mInProgress && count >= mMinPointerCount &&
                (wasInProgress || Math.abs(angleDelta(angle, mPrevAngle)) > mAngleSlop)) {
            mPrevAngle = mCurrAngle = angle;
            mInProgress = mListener.onRotateBegin(this);
        }

        if (action == MotionEvent.ACTION_MOVE) {
            mCurrAngle = angle;

            boolean updatePrev = true;

            if (mInProgress && count >= mMinPointerCount) {
                updatePrev = mListener.onRotate(this);
            }

            if (updatePrev) {
                mPrevAngle = mCurrAngle;
            }
        }

        return true;
    }

    private float calculateAngle(float x1, float y1, float x2, float y2) {
        double angleRad = Math.atan2(y2 - y1, x2 - x1);
        float angleDeg = (float) Math.toDegrees(angleRad);
        if (angleDeg < 0) {
            angleDeg += 360;
        }
        return angleDeg;
    }

    private float angleDelta(float angle1, float angle2) {
        float delta = (angle1 - angle2) % 360;
        if (delta > 180) {
            delta -= 360;
        } else if (delta < -180) {
            delta += 360;
        }
        return delta;
    }

    public float getRotationDelta() {
        return angleDelta(mCurrAngle, mPrevAngle);
    }
}