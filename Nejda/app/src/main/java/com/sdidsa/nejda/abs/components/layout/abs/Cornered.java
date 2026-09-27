package com.sdidsa.nejda.abs.components.layout.abs;

import android.view.View;

public interface Cornered {
    void setCornerRadius(float[] radius);
    void setCornerRadius(float radius);
    void setCornerRadiusTop(float radius);
    void setCornerRadiusBottom(float radius);
    void setCornerRadiusRight(float radius);
    void setCornerRadiusLeft(float radius);
    void setCornerRadiusTopLeft(float radius);
    void setCornerRadiusTopRight(float radius);
    void setCornerRadiusBottomRight(float radius);
    void setCornerRadiusBottomLeft(float radius);
    float[] getCornerRadius();
    View getView();
}
