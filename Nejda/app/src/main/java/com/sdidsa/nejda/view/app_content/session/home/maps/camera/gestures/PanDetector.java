package com.sdidsa.nejda.view.app_content.session.home.maps.camera.gestures;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

public class PanDetector {
    public interface OnPanGestureListener {
        void onPan(@NonNull PanDetector detector);
        default boolean onPanBegin(@NonNull PanDetector ignoredDetector) {
            return true;
        }
        default void onPanEnd(@NonNull PanDetector ignoredDetector) {}
    }

    private final OnPanGestureListener mListener;

    private float mStartX;
    private float mStartY;
    private float mLastX;
    private float mLastY;
    private float mCurrX;
    private float mCurrY;
    private boolean mInProgress;

    private final int mTouchSlop;
    private final int mMaxPointerCount;

    public PanDetector(@NonNull OnPanGestureListener listener) {
        this(8, 1, listener);
    }

    public PanDetector(int touchSlop, int maxPointerCount,
                       @NonNull OnPanGestureListener listener) {
        mListener = listener;
        mTouchSlop = touchSlop;
        mMaxPointerCount = maxPointerCount;
    }

    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final int action = event.getActionMasked();
        final int count = event.getPointerCount();

        final boolean streamComplete = action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL;

        if (action == MotionEvent.ACTION_DOWN || streamComplete ||
                (count > mMaxPointerCount && mMaxPointerCount > 0)) {
            if (mInProgress) {
                mListener.onPanEnd(this);
                mInProgress = false;
            }

            if (streamComplete) {
                return true;
            }
        }

        final float x = event.getX(0);
        final float y = event.getY(0);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = mLastX = mCurrX = x;
                mStartY = mLastY = mCurrY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                mLastX = mCurrX;
                mLastY = mCurrY;
                mCurrX = x;
                mCurrY = y;

                if (!mInProgress) {
                    final float deltaX = mCurrX - mStartX;
                    final float deltaY = mCurrY - mStartY;
                    final float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                    if (distance > mTouchSlop && count <= mMaxPointerCount) {
                        mInProgress = mListener.onPanBegin(this);
                    }
                } else {
                    if (count <= mMaxPointerCount) {
                        mListener.onPan(this);
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (count > mMaxPointerCount && mInProgress) {
                    mListener.onPanEnd(this);
                    mInProgress = false;
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                break;
        }

        return true;
    }

    public float getDeltaX() {
        return mCurrX - mLastX;
    }

    public float getDeltaY() {
        return mCurrY - mLastY;
    }
}