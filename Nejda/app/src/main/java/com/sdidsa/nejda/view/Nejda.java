package com.sdidsa.nejda.view;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sdidsa.nejda.abs.App;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.Store;
import com.sdidsa.nejda.abs.utils.TempStore;
import com.sdidsa.nejda.abs.utils.UiScale;
import com.sdidsa.nejda.abs.utils.view.PermissionUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.controll.UserApi;
import com.sdidsa.nejda.controll.abs.Cloudinary;
import com.sdidsa.nejda.view.app_content.auth.login.Login;
import com.sdidsa.nejda.view.app_content.auth.login.Welcome;
import com.sdidsa.nejda.view.app_content.misc.Loader;
import com.sdidsa.nejda.view.app_content.session.home.Home;

import java.io.File;
import java.util.function.Consumer;

public class Nejda extends App {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void postCreate() {
        super.postCreate();
        Animation.applySpeed(Store.getAnimations());

        UiScale uiScale = Store.getScale();
        if (uiScale == UiScale.AUTO) {
            int sh = getResources().getDisplayMetrics().heightPixels;

            float dp = SizeUtils.pxToDipNoScale(sh, this);
            float scale = 0.0008864f * dp + 0.2304f;
            scale = ((int) (scale * 100)) / 100f;
            SizeUtils.scale = Math.max(Math.min(scale, UiScale.BIGGEST.getScale()), UiScale.SMALLEST.getScale());
        } else {
            SizeUtils.scale = Store.getScale().getScale();
        }

        loadPage(Loader.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionUtils.requestPermissions(this, () -> {
                        //loadPage(Home.class);
                        loadApp();
                    },
                    Manifest.permission.POST_NOTIFICATIONS);
        } else {
            //loadPage(Home.class);
            loadApp();
        }
    }

    public static void uploadAttachment(File file, Consumer<String> onSuccess) {
        Cloudinary.uploadAttachment(file, "emergency_image", onSuccess);
    }

    private void loadApp() {
        NotificationChannel channel = new NotificationChannel(
                "main",
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        String uid = Store.getUserId();
        Platform.runAfter(() -> {
            if (uid != null && !uid.isBlank()) {
                UserApi.getUser(uid, user -> {
                    if (user != null) {
                        TempStore.putUser(user);
                        UserApi.saveFirebaseToken(user);
                        loadPage(Home.class);
                    } else {
                        loadPage(Login.class);
                    }
                });
            } else {
                loadPage(Welcome.class);
            }
        }, 500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}