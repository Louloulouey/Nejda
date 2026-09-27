package com.sdidsa.nejda.view.app_content.session.home.maps.camera.gestures;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

public class TiltDetector {
    public interface OnTiltGestureListener {
        boolean onTilt(@NonNull TiltDetector detector);
        default boolean onTiltBegin(@NonNull TiltDetector ignoredDetector) {
            return true;
        }
        default void onTiltEnd(@NonNull TiltDetector ignoredDetector) {}
    }

    private final OnTiltGestureListener mListener;

    private float mCurrTilt;
    private float mPrevTilt;
    private boolean mInProgress;

    private final int mTiltSlop;
    private final int mMinPointerCount;

    public TiltDetector(@NonNull OnTiltGestureListener listener) {
        this(5, 2, listener);
    }

    public TiltDetector(int tiltSlop, int minPointerCount,
                        @NonNull OnTiltGestureListener listener) {
        mListener = listener;
        mTiltSlop = tiltSlop;
        mMinPointerCount = minPointerCount;
    }

    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final int action = event.getActionMasked();
        final int count = event.getPointerCount();

        final boolean streamComplete = action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL;

        if (action == MotionEvent.ACTION_DOWN || streamComplete) {
            if (mInProgress) {
                mListener.onTiltEnd(this);
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

        float sumY = 0;
        final int div = pointerUp ? count - 1 : count;

        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            sumY += event.getY(i);
        }

        final float focusY = sumY / div;

        final float tiltValue;
        if (count >= mMinPointerCount) {
            tiltValue = focusY;
        } else {
            tiltValue = 0;
        }

        final boolean wasInProgress = mInProgress;

        if (mInProgress && (count < mMinPointerCount || configChanged)) {
            mListener.onTiltEnd(this);
            mInProgress = false;
        }

        if (configChanged) {
            mPrevTilt = mCurrTilt = tiltValue;
        }

        if (!mInProgress && count >= mMinPointerCount &&
                (wasInProgress || Math.abs(tiltValue - mPrevTilt) > mTiltSlop)) {
            mPrevTilt = mCurrTilt = tiltValue;
            mInProgress = mListener.onTiltBegin(this);
        }

        if (action == MotionEvent.ACTION_MOVE) {
            mCurrTilt = tiltValue;

            boolean updatePrev = true;

            if (mInProgress) {
                updatePrev = mListener.onTilt(this);
            }

            if (updatePrev) {
                mPrevTilt = mCurrTilt;
            }
        }

        return true;
    }

    public float getTiltDelta() {
        return mCurrTilt - mPrevTilt;
    }
}