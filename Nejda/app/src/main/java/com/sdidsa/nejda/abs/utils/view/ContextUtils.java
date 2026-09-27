package com.sdidsa.nejda.abs.utils.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.App;
import com.sdidsa.nejda.abs.components.Page;

import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.core.graphics.Insets;

import com.sdidsa.nejda.abs.components.layout.overlay.Overlay;
import com.sdidsa.nejda.abs.data.media.Media;

import java.util.List;
import java.util.function.Consumer;

public class ContextUtils {
    public static View[] getViewChildren(ViewGroup viewGroup) {
        return getViewChildren(viewGroup, 0);
    }

    public static View[] getViewChildren(ViewGroup viewGroup, int from) {
        View[] res = new View[viewGroup.getChildCount()];
        for (int i = from; i < viewGroup.getChildCount(); i++) {
            if(viewGroup.getChildAt(i).getVisibility() == View.VISIBLE)
                res[i] = viewGroup.getChildAt(i);
        }
        return res;
    }

    public static String getAppName(Context context) {
        return context.getResources().getString(R.string.app_name);
    }

    public static void removeOverlay(Context context, Overlay overlay) {
        if(context instanceof App app) {
            app.removeOverlay(overlay);
        }
    }

    public static void addOverlay(Context context, Overlay overlay) {
        if(context instanceof App app) {
            app.addOverlay(overlay);
        }
    }

    public static void finishAndRemoveTask(Context context) {
        if(context instanceof App app) {
            app.finishAndRemoveTask();
        }
    }

    public static void moveTaskToBack(Context context, boolean nonRoot) {
        if(context instanceof App app) {
            app.moveTaskToBack(nonRoot);
        }
    }

    public static void unloadApp(Context owner, Runnable post) {
        if(owner instanceof App app) {
            app.unloadApp(post);
        }
    }

    public static void loadApp(Context owner, Runnable post) {
        if(owner instanceof App app) {
            app.loadApp(post);
        }
    }

    public static void loadPage(Context context, Class<? extends Page> type) {
        loadPage(context, type, null, 1);
    }

    public static void loadPage(Context context, Class<? extends Page> type, Runnable post) {
        loadPage(context, type, post, 1);
    }

    public static void loadPage(Context context, Class<? extends Page> type, int direction) {
        loadPage(context, type, null, direction);
    }

    public static void loadPage(Context context, Class<? extends Page> type,
                                Runnable post, int direction) {
        if(context instanceof App app) {
            app.loadPage(type, post, direction);
        }
    }

    public static int getScreenHeight(Context context) {

        if(context instanceof App app) {
            return app.root.getHeight();
        }


        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static void pickImage(Context context, Consumer<Media> onRes) {
        if(context instanceof App app) {
            app.pickImage(onRes);
        }
    }

    public static void pickImages(Context context, Consumer<List<Media>> onRes) {
        if(context instanceof App app) {
            app.pickImages(onRes);
        }
    }

    public static Insets getSystemInsets(Context context) {
        if(context instanceof App app) {
            return app.getSystemInsets();
        }
        return Insets.of(0,0,0,0);
    }

    public static void showKeyboard(Context context, EditText input) {
        if(context instanceof App app) {
            app.showKeyboard(input);
        }
    }

    public static void hideKeyboard(Context context) {
        if(context instanceof App app) {
            app.hideKeyboard();
        }
    }

    public static void toast(Context context, String content) {
        if(context instanceof App app) {
            app.toast(content);
        } else {
            Toast.makeText(context, LocaleUtils.getLocale(context).get().get(content),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void toast(Context context, String content, long duration) {
        if(context instanceof App app) {
            app.toast(content, duration);
        } else {
            Toast.makeText(context, content, Toast.LENGTH_LONG).show();
        }
    }

    public static void startForResult(Context context,
                                      Intent intent,
                                      Consumer<ActivityResult> handler) {
        if(context instanceof App app) {
            app.startForResult(intent, handler);
        }
    }

    public static void captureWithCamera(Context context, Consumer<Bitmap> handler) {
        if(context instanceof App app) {
            app.captureWithCamera(handler);
        }
    }

    public static void postCreate(Context context) {
        if(context instanceof App app) {
            app.postCreate();
        }
    }

    public static void applyEnabled(View view, boolean enabled) {
        Drawable drawable = view.getBackground();
        if (drawable != null) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(enabled ? 1 : 0f);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            drawable.setColorFilter(filter);
            view.setBackground(drawable);
        }
    }

    public static void getLocationOnScreen(View view, int[] result) {
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        int x = loc[0];
        Context context = view.getContext();
        if(LocaleUtils.isRtl(context)) {
            x = getScreenWidth(context) - view.getWidth() - x;
        }
        result[0] = x;
        result[1] = loc[1];
    }
}
