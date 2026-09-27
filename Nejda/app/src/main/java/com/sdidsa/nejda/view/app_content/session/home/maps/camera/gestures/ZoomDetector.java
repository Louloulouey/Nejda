package com.sdidsa.nejda.view.app_content.session.home.maps.camera.gestures;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

public class ZoomDetector {
    public interface OnZoomGestureListener {
        boolean onZoom(@NonNull ZoomDetector detector);

        default boolean onZoomBegin(@NonNull ZoomDetector ignoredDetector) {
            return true;
        }

        default void onZoomEnd(@NonNull ZoomDetector ignoredDetector) {}
    }

    private final OnZoomGestureListener mListener;

    private float mCurrSpan;
    private float mPrevSpan;
    private float mInitialSpan;
    private boolean mInProgress;

    private final int mSpanSlop;
    private final int mMinSpan;

    public ZoomDetector(@NonNull OnZoomGestureListener listener) {
        this(15, 50, listener);
    }

    public ZoomDetector(int spanSlop, int minSpan,
                         @NonNull OnZoomGestureListener listener) {
        mListener = listener;
        mSpanSlop = spanSlop;
        mMinSpan = minSpan;
    }

    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final int action = event.getActionMasked();
        final int count = event.getPointerCount();

        final boolean streamComplete = action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL;

        if (action == MotionEvent.ACTION_DOWN || streamComplete) {
            if (mInProgress) {
                mListener.onZoomEnd(this);
                mInProgress = false;
                mInitialSpan = 0;
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

        if (count < 2 && !mInProgress) {
            return true;
        }

        float sumX = 0, sumY = 0;
        final int div = pointerUp ? count - 1 : count;

        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            sumX += event.getX(i);
            sumY += event.getY(i);
        }

        final float focusX = sumX / div;
        final float focusY = sumY / div;

        float devSumX = 0, devSumY = 0;
        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;

            devSumX += Math.abs(event.getX(i) - focusX);
            devSumY += Math.abs(event.getY(i) - focusY);
        }
        final float devX = devSumX / div;
        final float devY = devSumY / div;

        final float spanX = devX * 2;
        final float spanY = devY * 2;
        final float span = (float) Math.hypot(spanX, spanY);

        final boolean wasInProgress = mInProgress;

        if (mInProgress && (span < mMinSpan || configChanged)) {
            mListener.onZoomEnd(this);
            mInProgress = false;
            mInitialSpan = span;
        }

        if (configChanged) {
            mInitialSpan = mPrevSpan = mCurrSpan = span;
        }

        if (!mInProgress && span >= mMinSpan &&
                (wasInProgress || Math.abs(span - mInitialSpan) > mSpanSlop)) {
            mPrevSpan = mCurrSpan = span;
            mInProgress = mListener.onZoomBegin(this);
        }

        if (action == MotionEvent.ACTION_MOVE) {
            mCurrSpan = span;

            boolean updatePrev = true;

            if (mInProgress) {
                updatePrev = mListener.onZoom(this);
            }

            if (updatePrev) {
                mPrevSpan = mCurrSpan;
            }
        }

        return true;
    }

    public float getZoomFactor() {
        return mPrevSpan > 0 ? mCurrSpan / mPrevSpan : 1;
    }
}