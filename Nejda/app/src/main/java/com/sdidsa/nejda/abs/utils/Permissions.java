package com.sdidsa.nejda.abs.utils;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class Permissions {

    public static String[] imagePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            return arrayOf(READ_MEDIA_IMAGES);
        } else {
            return arrayOf(READ_EXTERNAL_STORAGE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public static String[] selectiveImagePermission() {
        return arrayOf(READ_MEDIA_VISUAL_USER_SELECTED);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] allImagesPermission() {
        return arrayOf(READ_MEDIA_IMAGES);
    }

    public static String[] externalStoragePermission() {
        return arrayOf(READ_EXTERNAL_STORAGE);
    }

    private static String[] arrayOf(String...strings) {
        return strings;
    }

    public static int permissionRequestCode() {
        return (int) (Math.random() * 2048) + 1;
    }
}
