package com.sdidsa.nejda.abs.utils.view;

import android.content.Context;
import android.content.pm.PackageManager;

import com.sdidsa.nejda.abs.App;

public class PermissionUtils {
    public static void requirePermissions(Context context, Runnable onGranted,
                                          String... permissions) {
        if(context instanceof App app) {
            app.requirePermissions(onGranted, permissions);
        } else if(isGranted(context, permissions)) {
            onGranted.run();
        }
    }

    public static void requestPermissionsOr(Context context, Runnable onGranted, String... permissions) {
        if(context instanceof App app) {
            app.requestPermissionsOr(onGranted, permissions);
        } else if(isGranted(context, permissions)) {
            onGranted.run();
        }
    }

    public static void requirePermissionsOr(Context context, Runnable onGranted,
                                            String... permissions) {
        if(context instanceof App app) {
            app.requirePermissionsOr(onGranted, permissions);
        } else if(isGranted(context, permissions)) {
            onGranted.run();
        }
    }

    public static void requestPermissions(Context context, Runnable onGranted,
                                          String... permissions) {
        if(context instanceof App app) {
            app.requestPermissions(onGranted, permissions);
        }
    }

    public static boolean isGranted(Context context, String...permissions) {
        if(context instanceof App app) {
            return app.isGranted(permissions);
        }else {
            for (String permission : permissions) {
                if (!isGranted(context, permission)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean isGranted(Context context, String permission) {
        if(context instanceof App app) {
            return app.isGranted(permission);
        }else {
            return context.checkSelfPermission(permission) ==
                    PackageManager.PERMISSION_GRANTED;
        }
    }
}
