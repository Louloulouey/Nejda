package com.sdidsa.nejda.view.app_content.session.attachments;

import android.content.Context;
import android.graphics.Bitmap;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.App;
import com.sdidsa.nejda.abs.components.controls.camera.Camera2Preview;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.ColoredStackPane;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.overlay.PartialSlideOverlay;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;

import java.util.function.Consumer;

public class CaptureImage extends PartialSlideOverlay {

    private Consumer<Bitmap> onCapture;

    public CaptureImage(Context owner) {
        super(owner, .6f);

        StackPane previewContainer = new ColoredStackPane(owner, Style.BACK_TER);
        previewContainer.setCornerRadius(15);
        previewContainer.setClipToOutline(true);
        SpacerUtils.spacer(previewContainer, Orientation.VERTICAL);

        Camera2Preview camera2Preview = new Camera2Preview(owner);
        previewContainer.addView(camera2Preview);

        StackPane bottom = new ColoredStackPane(owner, s ->
                App.adjustAlpha(s.getBackgroundPrimary(), 0.6f));
        bottom.setPadding(20);
        bottom.setLayoutParams(new LayoutParams(-1, -2));

        ColoredIcon capture = new ColoredIcon(owner, Style.BACK_SEC, R.drawable.camera, 64);
        capture.setPadding(15);
        capture.setRadiusNoClip(64);

        capture.setBack(Style.TEXT_NORM);

        capture.setOnClick(() -> {
            if(onCapture != null) onCapture.accept(camera2Preview.getLastImage());
        });

        bottom.addCentered(capture);

        camera2Preview.addAligned(bottom, Alignment.BOTTOM_CENTER);

        list.setPadding(20);
        list.addView(previewContainer);
    }

    public void setOnCapture(Consumer<Bitmap> onCapture) {
        this.onCapture = onCapture;
    }
}
