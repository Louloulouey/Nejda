package com.sdidsa.nejda.abs.components.controls.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sdidsa.nejda.abs.utils.ErrorHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DiskImageCache {

    private final Bitmap.CompressFormat mCompressFormat;
    private final int mCompressQuality;
    private final File diskCacheDir;

    public DiskImageCache(Context context, String uniqueName,
                          Bitmap.CompressFormat compressFormat, int quality) {
        diskCacheDir = getDiskCacheDir(context, uniqueName);
        mCompressFormat = compressFormat;
        mCompressQuality = quality;

        if (!diskCacheDir.exists() || !diskCacheDir.isDirectory()) {
            if (!diskCacheDir.mkdir()) {
                ErrorHandler.handle(new RuntimeException("failed to create cache dir"),
                        "init disk cache");
            }
        }
    }

    private File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath = context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }

    public File put(String key, Bitmap data) {
        Bitmap resizedBitmap = data;
        if(data.getWidth() > 2048 || data.getHeight() > 2048) {
            int maxWidth = 2048;
            int maxHeight = 2048;
            if(data.getWidth() > data.getHeight()) {
                maxHeight = (int) ((float) data.getHeight() / data.getWidth() * maxWidth);
            } else {
                maxWidth = (int) ((float) data.getWidth() / data.getHeight() * maxHeight);
            }
            resizedBitmap = ImageProxy.scale(
                    data,
                    maxWidth,
                    maxHeight
            );
        }


        File saveTo = keyToFile(key);
        try (OutputStream os = new FileOutputStream(saveTo)){

            resizedBitmap.compress(mCompressFormat, mCompressQuality, os);

        } catch (IOException e) {
            ErrorHandler.handle(e, "caching image with key [ " + key + " ]");
        }
        return saveTo;
    }

    public Bitmap getBitmap(String key) {
        File readFrom = keyToFile(key);
        if(exists(key)) {
            try (InputStream is = new FileInputStream(readFrom)) {
                return BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                ErrorHandler.handle(e, "get image " + key + " from disk cache");
            }
        }
        return null;
    }

    public boolean exists(String key) {
        File readFrom = keyToFile(key);
        return readFrom.exists() && readFrom.isFile();
    }

    private File keyToFile(String key) {
        return new File(diskCacheDir.getAbsolutePath() + File.separator + key);
    }

    public Bitmap.CompressFormat getmCompressFormat() {
        return mCompressFormat;
    }

    public int getmCompressQuality() {
        return mCompressQuality;
    }
}