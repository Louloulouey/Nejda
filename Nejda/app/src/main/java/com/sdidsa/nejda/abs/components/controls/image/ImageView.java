package com.sdidsa.nejda.abs.components.controls.image;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

import androidx.appcompat.widget.AppCompatImageView;

public class ImageView extends AppCompatImageView {
    private Path clipPath;
    private RectF rect;
    private float[] cornerRadii;

    public ImageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        clipPath = new Path();
        rect = new RectF();
        cornerRadii = new float[]{0,0,0,0,0,0,0,0};
    }

    public void setCornerRadii(float[] radii) {
        if (radii.length == 8) {
            cornerRadii = radii;
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rect.set(0, 0, w, h);
        updateClipPath();
    }

    private void updateClipPath() {
        clipPath.reset();
        clipPath.addRoundRect(rect, cornerRadii, Path.Direction.CW);
        clipPath.close();
    }

    @Override
    public void draw(Canvas canvas) {
        int save = canvas.save();

        canvas.clipPath(clipPath);

        super.draw(canvas);

        canvas.restoreToCount(save);
    }
}
