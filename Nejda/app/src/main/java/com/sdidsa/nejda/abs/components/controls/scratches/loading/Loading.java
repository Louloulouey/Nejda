package com.sdidsa.nejda.abs.components.controls.scratches.loading;

import android.view.View;

public interface Loading {
    void startLoading();
    void stopLoading();
    View getLoadingView();

    void setSize(float size);

    boolean isRunning();
}
