package com.sdidsa.nejda.abs.components.controls.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.sdidsa.nejda.abs.components.controls.scratches.loading.ColoredSpinLoading;
import com.sdidsa.nejda.abs.components.controls.scratches.loading.SpinLoading;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.Platform;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Camera2Preview extends StackPane {

    private TextureView textureView;
    private ImageReader imageReader;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private CaptureRequest.Builder previewRequestBuilder;

    private SpinLoading loader;

    private Handler backgroundHandler;
    private HandlerThread backgroundThread;

    public ImageReader.OnImageAvailableListener imageHandler;

    private Bitmap lastImage;

    private Consumer<Bitmap> onImage;

    private long lastImageTimestamp = 0;

    private CameraManager manager;
    private String cameraId;

    private float zoomLevel = 1.0f;

    public Camera2Preview(Context context) {
        super(context);
        init();
    }

    private void init() {
        textureView = new TextureView(getContext());
        textureView.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        loader = new ColoredSpinLoading(getContext(), Style.TEXT_SEC, 64);

        addCentered(loader);
        addCentered(textureView);

        imageHandler = reader -> {
            long now = System.currentTimeMillis();
            if (now - lastImageTimestamp < 500) {
                Image skippedImage = reader.acquireLatestImage();
                if (skippedImage != null) skippedImage.close(); // Always close!
                return;
            }
            lastImageTimestamp = now;

            Image image = reader.acquireLatestImage();
            if (image != null) {
                lastImage = YuvConverter.imageYuv420ToBitmap(image);
                if (onImage != null) onImage.accept(lastImage);
                image.close();
            }
        };

        textureView.setSurfaceTextureListener(surfaceTextureListener);
    }

    public Bitmap getLastImage() {
        return lastImage;
    }

    private final TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            loader.startLoading();
            Platform.runAfter(() -> {
                startBackgroundThread();
                openCamera();
            }, 500);
        }

        @Override public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {}
        @Override public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            stopCamera();
            return true;
        }
        @Override public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {}
    };

    @SuppressLint("MissingPermission")
    private void openCamera() {
        try {
            manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
            cameraId = manager.getCameraIdList()[0]; // 0 for rear | 1 for front
            manager.openCamera(cameraId, cameraStateCallback, backgroundHandler);
        } catch (Exception e) {
            ErrorHandler.handle(e, "open camera");
        }
    }

    private final CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            startPreview();
        }

        @Override public void onDisconnected(CameraDevice camera) {
            camera.close();
        }

        @Override public void onError(CameraDevice camera, int error) {
            camera.close();
        }
    };

    private void startPreview() {
        try {
            int width = 1024;
            int height = 720;

            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(width, height);

            Surface surface = new Surface(texture);

            imageReader = ImageReader.newInstance(width, height,
                    ImageFormat.YUV_420_888, 2);
            imageReader.setOnImageAvailableListener(imageHandler, backgroundHandler);

            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surface);
            previewRequestBuilder.addTarget(imageReader.getSurface());

            List<Surface> outputSurfaces = Arrays.asList(
                    surface,
                    imageReader.getSurface()
            );

            Rect zoomRect = getZoomRect();

            cameraDevice.createCaptureSession(outputSurfaces,
                    new CameraCaptureSession.StateCallback() {
                        @Override public void onConfigured(@NonNull CameraCaptureSession session) {
                            if (cameraDevice == null) return;
                            captureSession = session;
                            try {
                                previewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoomRect);
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                session.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler);
                            } catch (CameraAccessException e) {
                                ErrorHandler.handle(e, "create capture session");
                            }
                        }

                        @Override public void onConfigureFailed(@NonNull CameraCaptureSession session) {}
                    }, backgroundHandler
            );
        } catch (CameraAccessException e) {
            ErrorHandler.handle(e, "start camera preview");
        }
    }

    public void setZoomLevel(float zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    @NonNull
    private Rect getZoomRect() throws CameraAccessException {
        CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
        Rect sensorArraySize = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);

        assert sensorArraySize != null;
        int cropWidth = (int) (sensorArraySize.width() / zoomLevel);
        int cropHeight = (int) (sensorArraySize.height() / zoomLevel);

        int cropLeft = (sensorArraySize.width() - cropWidth) / 2;
        int cropTop = (sensorArraySize.height() - cropHeight) / 2;

        return new Rect(
                cropLeft,
                cropTop,
                cropLeft + cropWidth,
                cropTop + cropHeight
        );
    }

    public void setOnImage(Consumer<Bitmap> onImage) {
        this.onImage = onImage;
    }

    public void stopCamera() {
        if (captureSession != null) {
            captureSession.close();
            captureSession = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if(imageReader != null) {
            imageReader.close();
        }
        stopBackgroundThread();
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
                backgroundThread = null;
                backgroundHandler = null;
            } catch (InterruptedException e) {
                ErrorHandler.handle(e, "stop background thread");
            }
        }
    }
}
