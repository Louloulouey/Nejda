package com.sdidsa.nejda.view.app_content.auth.login;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.components.Page;
import com.sdidsa.nejda.abs.components.controls.camera.Camera2Preview;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.layout.ColoredStackPane;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.overlay.PartialSlideOverlay;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;

public class QrScanOverlay extends PartialSlideOverlay {

    public QrScanOverlay(Context owner) {
        super(owner, SQUARE_HEIGHT);

        StackPane previewContainer = new ColoredStackPane(owner, Style.BACK_TER);
        previewContainer.setCornerRadius(15);
        previewContainer.setClipToOutline(true);
        SpacerUtils.spacer(previewContainer, Orientation.VERTICAL);

        Camera2Preview cameraFeed = new Camera2Preview(owner);
        cameraFeed.setZoomLevel(1.5f);
        previewContainer.addView(cameraFeed);


        ColoredIcon back = new ColoredIcon(owner, Style.BACK_TER,
                R.drawable.qr_scan_back, -1);
        ColoredIcon front = new ColoredIcon(owner, Style.TEXT_NORM,
                R.drawable.qr_scan_front, -1);
        back.setAlpha(.4f);
        back.matchParent();
        front.matchParent();

        cameraFeed.addCentered(back);
        cameraFeed.addCentered(front);

        cameraFeed.setOnImage(qrImage -> {
            String qrData = decodeQRCode(crop(qrImage));
            if(qrData != null) {
                String matricule = extractMatricule(qrData);

                hide();

                Login login = Page.getInstance(owner, Login.class);
                Platform.runLater(() -> login.loginWithMatricule(matricule));
            }
        });

        list.setPadding(20);
        list.addView(previewContainer);
    }

    private String extractMatricule(String s) {
        String url = s.trim();
        return url.substring(url.lastIndexOf('/') + 1);
    }

    public String decodeQRCode(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        RGBLuminanceSource source = new RGBLuminanceSource(width, height, getRGB(bitmap));
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();

        try {
            Result result = reader.decode(binaryBitmap);
            return result.getText();
        } catch (NotFoundException | ChecksumException | FormatException e) {
            return null;
        }
    }

    private Bitmap crop(Bitmap full) {
        assert full != null;
        int width = full.getWidth();
        int height = full.getHeight();
        int size = (int) (Math.min(width, height) * 2f / 3f);

        int x = (width - size) / 2;
        int y = (height - size) / 2;

        return Bitmap.createBitmap(full, x, y, size, size);
    }

    private int[] getRGB(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

}
