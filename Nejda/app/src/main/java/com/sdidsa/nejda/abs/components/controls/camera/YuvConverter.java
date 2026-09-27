package com.sdidsa.nejda.abs.components.controls.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class YuvConverter {

    private static final String TAG = "YuvConverter"; // Tag for logging

    /**
     * Converts an android.media.Image in YUV_420_888 format to a Bitmap.
     *
     * @param image The Image in YUV_420_888 format.
     * @return The converted Bitmap, or null if conversion fails.
     */
    public static Bitmap imageYuv420ToBitmap(Image image) {
        if (image == null) {
            Log.e(TAG, "Input image is null.");
            return null;
        }
        if (image.getFormat() != ImageFormat.YUV_420_888) {
            Log.e(TAG, "Image format is not YUV_420_888. Got: " + image.getFormat());
            return null;
        }

        final int width = image.getWidth();
        final int height = image.getHeight();

        // --- Convert YUV_420_888 to NV21 byte array ---
        byte[] nv21Bytes = imageToNv21(image);
        if (nv21Bytes == null) {
            Log.e(TAG, "Failed to convert Image to NV21 byte array.");
            return null; // imageToNv21 failed
        }

        // --- Convert NV21 byte array to Bitmap using YuvImage ---
        Bitmap full = nv21ToBitmap(nv21Bytes, width, height);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        assert full != null;
        return Bitmap.createBitmap(full, 0, 0, full.getWidth(), full.getHeight(), matrix, true);
    }

    /**
     * Helper function to convert YUV_420_888 Image planes to NV21 byte array.
     * NV21 format is: YYYYYYYY... UVUVUV...
     * IMPORTANT: This assumes the UV pixel stride is 2. Handle other cases as needed.
     * It also assumes the image is valid and planes are accessible.
     */
    private static byte[] imageToNv21(Image image) {
        if (image.getFormat() != ImageFormat.YUV_420_888) {
            Log.e(TAG, "Invalid image format for imageToNv21");
            return null;
        }

        final int width = image.getWidth();
        final int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();

        if (planes == null || planes.length < 3 || planes[0] == null || planes[1] == null || planes[2] == null) {
            Log.e(TAG, "Invalid image planes.");
            return null;
        }

        Image.Plane yPlane = planes[0];
        Image.Plane uPlane = planes[1];
        Image.Plane vPlane = planes[2];

        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();

        // Ensure buffers are accessible
        if (yBuffer == null || uBuffer == null || vBuffer == null) {
            Log.e(TAG, "Invalid image buffers.");
            return null;
        }

        final int ySize = yBuffer.remaining();
        final int uSize = uBuffer.remaining();
        final int vSize = vBuffer.remaining();

        // NV21 requires V plane comes first in the interleaved UV section
        // Size calculation assumes packed YUV420 format (width*height + width*height/2)
        int nv21Size = width * height + (width * height / 2);
        byte[] nv21 = new byte[nv21Size];

        int yRowStride = yPlane.getRowStride();
        int uvPixelStride = uPlane.getPixelStride(); // Assume U and V planes have the same pixel stride
        int uvRowStride = uPlane.getRowStride();     // Assume U and V planes have the same row stride

        int outputOffset = 0;

        // --- Copy Y Plane ---
        if (yRowStride == width) { // Optimization for contiguous Y plane
            yBuffer.get(nv21, outputOffset, ySize);
            outputOffset += ySize;
        } else { // Handle potential row padding
            for (int row = 0; row < height; ++row) {
                yBuffer.position(row * yRowStride);
                yBuffer.get(nv21, outputOffset + row * width, width);
            }
            outputOffset += width * height; // Move offset past the entire Y plane
            yBuffer.position(0); // Reset buffer position just in case
        }


        // --- Copy VU Plane (Interleaved) ---
        // NV21 format requires V followed by U (VU VU VU ...)
        // We need to interleave bytes from the V and U planes.
        final int uvHeight = height / 2;
        final int uvWidth = width / 2;

        // This is the most common case for YUV_420_888. If uvPixelStride is 1, the layout
        // is different (planar), and this logic needs to be adjusted.
        if (uvPixelStride == 2) {
            // Allocate temporary buffers for U and V data if needed (safer in case of direct buffers)
            byte[] vData = new byte[vSize];
            vBuffer.get(vData, 0, vSize);
            byte[] uData = new byte[uSize];
            uBuffer.get(uData, 0, uSize);

            for (int row = 0; row < uvHeight; ++row) {
                for (int col = 0; col < uvWidth; ++col) {
                    int vIndex = row * uvRowStride + col * uvPixelStride;
                    int uIndex = row * uvRowStride + col * uvPixelStride;

                    // Check bounds carefully
                    if (vIndex >= vData.length || (outputOffset + 1) >= nv21.length) {
                        Log.e(TAG, "Error converting YUV_420_888 to NV21: V index out of bounds or output overflow.");
                        return null; // Prevent crash
                    }
                    nv21[outputOffset++] = vData[vIndex]; // V byte

                    // Check bounds carefully
                    if (uIndex >= uData.length || (outputOffset) >= nv21.length) { // Note: +0 as V was just written
                        Log.e(TAG, "Error converting YUV_420_888 to NV21: U index out of bounds or output overflow.");
                        return null; // Prevent crash
                    }
                    nv21[outputOffset++] = uData[uIndex]; // U byte
                }
            }
        } else if (uvPixelStride == 1) {
            // Handle planar layout (YYYY... UU... VV...) conversion to NV21 (YYYY... VUVUVU...)
            // This is less common for direct Image input but possible.
            // NV21 expects interleaved V and U.
            byte[] vData = new byte[vSize];
            vBuffer.get(vData, 0, vSize);
            byte[] uData = new byte[uSize];
            uBuffer.get(uData, 0, uSize);

            for(int i=0; i < uvWidth * uvHeight; ++i) {
                if (i * 2 + 1 >= (width * height / 2) || i >= vData.length || i >= uData.length ) {
                    Log.e(TAG, "Error converting planar YUV_420 to NV21: index out of bounds.");
                    return null;
                }
                nv21[outputOffset + i*2] = vData[i]; // V
                nv21[outputOffset + i*2+1] = uData[i]; // U
            }
        } else {
            Log.e(TAG, "Unsupported UV pixel stride: " + uvPixelStride);
            return null;
        }

        return nv21;
    }

    /**
     * Helper function (reusable) to convert an NV21 byte array to a Bitmap.
     */
    private static Bitmap nv21ToBitmap(byte[] nv21, int width, int height) {
        if (nv21 == null || nv21.length == 0) {
            Log.e(TAG, "NV21 data is null or empty");
            return null;
        }

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            // Compress YuvImage to JPEG into the ByteArrayOutputStream
            // Quality of 90 is a good balance. Adjust if needed.
            if (!yuvImage.compressToJpeg(new Rect(0, 0, width, height), 90, out)) {
                Log.e(TAG, "compressToJpeg failed");
                return null;
            }
            byte[] imageBytes = out.toByteArray();

            // Decode the JPEG bytes into a Bitmap
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        } catch (Exception e) {
            Log.e(TAG, "Error converting NV21 to Bitmap: " + e.getMessage());
            return null;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing output stream: " + e.getMessage());
            }
        }
    }
}