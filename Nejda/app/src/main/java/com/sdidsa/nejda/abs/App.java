package com.sdidsa.nejda.abs;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.filament.Filament;
import com.google.android.filament.gltfio.Gltfio;
import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.base.ValueAnimation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateYAnimation;
import com.sdidsa.nejda.abs.animation.view.scale.ScaleXYAnimation;
import com.sdidsa.nejda.abs.components.Page;
import com.sdidsa.nejda.abs.components.controls.audio.AudioProxy;
import com.sdidsa.nejda.abs.components.controls.image.ImageProxy;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredVBox;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.components.layout.overlay.Overlay;
import com.sdidsa.nejda.abs.components.layout.overlay.media.MediaPickerOverlay;
import com.sdidsa.nejda.abs.data.media.Media;
import com.sdidsa.nejda.abs.data.property.Property;
import com.sdidsa.nejda.abs.locale.Locale;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.OnPermission;
import com.sdidsa.nejda.abs.utils.Permissions;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.Store;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class App extends AppCompatActivity {
    private static final int toastDur = 2000;
    private final ArrayList<Overlay> loadedOverlay = new ArrayList<>();
    private final ArrayList<View> oldToasts = new ArrayList<>();
    private final ArrayList<ParallelAnimation> toasting = new ArrayList<>();
    private final HashMap<Integer, OnPermission> onPermission = new HashMap<>();
    public Locale ar_ar;
    Animation running = null;
    public StackPane root;
    private Page loaded;
    private Property<Style> style;
    private Property<Locale> locale;
    private Insets systemInsets;
    private MediaPickerOverlay mediaPicker;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Consumer<ActivityResult> activityResultHandler;
    private Animation theming;

    @ColorInt
    public static int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Store.init(this);
        ImageProxy.init(this);
        AudioProxy.init(this);

        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();

        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backPressed();
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
            if (result.getResultCode() == RESULT_OK && activityResultHandler != null) {
                activityResultHandler.accept(result);
                activityResultHandler = null;
            }
        });

        root = new StackPane(this);

        root.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            WindowInsets wi = root.getRootWindowInsets();

            Insets win = wi == null ? Insets.NONE : Insets.of(wi.getSystemWindowInsetLeft(),
                    wi.getSystemWindowInsetTop(),
                    wi.getSystemWindowInsetRight(),
                    wi.getSystemWindowInsetBottom());

            if (!win.equals(systemInsets)) {
                this.systemInsets = win;

                if (loaded != null) {
                    loaded.applyInsets(systemInsets);
                }

                if (!loadedOverlay.isEmpty()) {
                    for (Overlay overlay : loadedOverlay) {
                        overlay.applySystemInsets(systemInsets);
                    }
                }
            }
        });

        Platform.runBack(() -> {
            Page.clearCache();

            style = new Property<>();
            applyTheme();

            style.addListener((ov, nv) -> Platform.runLater(() -> applyStyle(nv)));

            root.setClipChildren(false);
            Platform.runLater(() -> setContentView(root));

            Platform.runLater(() ->
                    WindowCompat.
                            setDecorFitsSystemWindows(this.getWindow(), false));

            new Locale(this, "fr_FR");
            new Locale(this, "en_US");
            ar_ar = new Locale(this, "ar_AR", true);

            Font.init(this);

            locale = new Property<>(Locale.forName(this, Store.getLanguage()));
            locale.addListener((ov, nv) ->
                    Platform.runLater(() -> {
                        if (nv != null && nv.isRtl()) {
                            root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                        } else {
                            root.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        }
            }));
        }, this::postCreate);
    }

    public void postCreate() {
    }

    public void startForResult(Intent intent, Consumer<ActivityResult> handler) {
        this.activityResultHandler = handler;
        activityResultLauncher.launch(intent);

    }

    public void captureWithCamera(Consumer<Bitmap> handler) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("action", "qr_scan");

        String appName = ContextUtils.getAppName(this).toLowerCase();

        int unique = (int) (Math.random() * 1000000);

        String fileName = appName + "_" + unique + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        final var contentResolver = getContentResolver();
        final var uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);
        assert uri != null;

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        startForResult(intent, result -> {
            try {
                ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, uri);
                Bitmap bitmap = ImageDecoder.decodeBitmap(source);

                handler.accept(bitmap);
            } catch (Exception x) {
                ErrorHandler.handle(x, "capture image with camera");
            }
        });
    }

    public void requirePermissions(Runnable onGranted, String... permissions) {
        if (isGranted(permissions)) {
            onGranted.run();
        } else {
            requestPermissions(onGranted, permissions);
        }
    }

    public void requirePermissionsOr(Runnable onGranted, String... permissions) {
        if (isGrantedOr(permissions)) {
            onGranted.run();
        } else {
            requestPermissionsOr(onGranted, permissions);
        }
    }

    public void requestPermissions(Runnable onGranted, String... permissions) {
        int code = Permissions.permissionRequestCode();
        if (onGranted != null) onPermission.put(code, new OnPermission(onGranted));
        requestPermissions(permissions, code);
    }

    public void requestPermissionsOr(Runnable onGranted, String... permissions) {
        int code = Permissions.permissionRequestCode();
        if (onGranted != null) onPermission.put(code, new OnPermission(onGranted, true));
        requestPermissions(permissions, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        OnPermission handler = onPermission.get(requestCode);
        if (handler != null &&
                (handler.isOr() ? isGrantedOr(permissions) : isGranted(permissions))) {
            handler.getOnPermission().run();
            onPermission.remove(requestCode);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean isGranted(String[] permissions) {
        for (String permission : permissions) {
            if (!isGranted(permission)) {
                return false;
            }
        }
        return true;
    }

    public boolean isGrantedOr(String[] permissions) {
        for (String permission : permissions) {
            if (isGranted(permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean isGranted(String permission) {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void pickImage(Consumer<Media> onRes) {
        if (mediaPicker == null) {
            mediaPicker = new MediaPickerOverlay(this);
        }
        mediaPicker.setOnMultiple(null);
        mediaPicker.setOnMedia(onRes);
        mediaPicker.show();
    }

    public void pickImages(Consumer<List<Media>> onRes) {
        if (mediaPicker == null) {
            mediaPicker = new MediaPickerOverlay(this);
        }
        mediaPicker.setOnMedia(null);
        mediaPicker.setOnMultiple(onRes);
        mediaPicker.show();
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void loadPage(Class<? extends Page> pageType) {
        loadPage(pageType, null, 1);
    }

    public void loadPage(Class<? extends Page> pageType, Runnable post, int direction) {
        hideKeyboard();

        if(pageType.isInstance(loaded)) {
            if(post != null) post.run();
            return;
        }

        Platform.runBack(() -> {
            if (running != null && running.isRunning()) {
                Platform.waitWhile(running::isRunning);
            }

            if (loaded != null && pageType.isInstance(loaded) && Page.hasInstance(pageType)) {
                Platform.runLater(() -> loaded.setup(direction));
                return;
            }

            AtomicReference<Page> page = new AtomicReference<>();
            Page old = loaded;
            loaded = null;
            if (old != null) {
                running = old.destroy(direction);
                running.setOnFinished(() -> root.removeView(old));
            }

            Semaphore waiter = new Semaphore(0);
            Platform.runBack(() -> {
                if (old != null) Platform.sleepReal(10);
                waiter.acquireUninterruptibly();
                Platform.runLater(() -> {
                    if (running != null) running.start();
                    page.get().setAlpha(1);
                    page.get().setup(direction)
                            .setOnFinished(() -> {
                                if (post != null) post.run();
                            }).start();
                    try {
                        ((ViewGroup) page.get().getParent()).removeView(page.get());
                    } catch (Exception x) {
                        //ignore
                    }
                    root.addView(page.get(), 0);
                });
            });

            page.set(Page.getInstance(this, pageType));
            waiter.release();

            if (page.get() == null) {
                ErrorHandler.handle(new Exception("Page instance is null"),
                        "Failed to load page: " + pageType.getSimpleName());
                return;
            }

            Platform.runLater(() -> {
                loaded = page.get();
                loaded.applyInsets(this.getSystemInsets());
                loaded.setAlpha(1);
                loaded.setScaleX(1);
                loaded.setScaleY(1);
                loaded.setTranslationY(0);
                loaded.setTranslationX(0);
            });
        });
    }

    public void addOverlay(Overlay overlay) {
        root.addView(overlay);
        overlay.applySystemInsets(systemInsets);
        loadedOverlay.add(0, overlay);
    }

    public void removeOverlay(Overlay overlay) {
        root.removeView(overlay);
        loadedOverlay.remove(overlay);
    }

    public void unloadApp(Runnable post) {
        Animation.fadeOutScaleDown(root)
                .setInterpolator(Interpolator.EASE_OUT)
                .setOnFinished(() -> {
                    root.removeAllViews();
                    loaded = null;
                    loadedOverlay.clear();

                    UiCache.clearAll();

                    if(post != null) {
                        post.run();
                    }
                }).start();
    }

    public void loadApp(Runnable post) {
        Animation.fadeInScaleUp(root)
                .setInterpolator(Interpolator.OVERSHOOT)
                .setOnFinished(() -> {
                    if(post != null) {
                        post.run();
                    }
                })
                .start();
    }

    public void toast(long duration, View... views) {
        Platform.runLater(() -> toastPrivate(duration, views));
    }

    private final Semaphore toastLock = new Semaphore(1);
    private void toastPrivate(long duration, View... views) {
        Platform.runBack(() -> {
            toastLock.acquireUninterruptibly();
            VBox toast = new ColoredVBox(this, Style.BACK_SEC);
            toast.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

            StackPane.LayoutParams params = new StackPane.LayoutParams(StackPane.LayoutParams.MATCH_PARENT, StackPane.LayoutParams.WRAP_CONTENT);

            int margins = SizeUtils.dipToPx(30, this);
            params.setMargins(margins, margins, margins, (ContextUtils.getScreenHeight(this) / 10) + systemInsets.bottom);
            params.gravity = Gravity.BOTTOM;

            toast.setLayoutParams(params);

            toast.setCornerRadius(12);
            toast.setPadding(15);
            toast.setSpacing(15);

            toast.setElevation(SizeUtils.dipToPx(15, this));

            toast.setAlpha(0);
            toast.setTranslationY(SizeUtils.dipToPx(100, this));
            toast.setScaleX(.7f);
            toast.setScaleY(.7f);

            for (View view : views) {
                toast.addView(view);
            }

            Platform.runLater(() -> root.addView(toast));

            ParallelAnimation anim = new ParallelAnimation(300).addAnimation(new AlphaAnimation(toast, 1f)).addAnimation(new TranslateYAnimation(toast, 0)).addAnimation(new ScaleXYAnimation(toast, 1)).setInterpolator(Interpolator.OVERSHOOT);
            anim.setOnFinished(() -> toasting.remove(anim));
            toasting.add(anim);

            Platform.runAfter(toastLock::release, 50);

            Platform.waitWhile(() -> toast.getHeight() == 0, () -> {
                if (!oldToasts.isEmpty()) {
                    ParallelAnimation up = new ParallelAnimation(300).setInterpolator(Interpolator.OVERSHOOT);
                    int y = toast.getHeight() + SizeUtils.dipToPx(20, this);
                    for (View old : oldToasts) {
                        if (old != toast) {
                            up.addAnimation(new TranslateYAnimation(old, -y));
                            y += old.getHeight() + SizeUtils.dipToPx(20, this);
                        }
                    }
                    up.start();
                }
            });

            anim.start();

            Platform.runAfter(() -> Platform.waitWhile(() -> !toasting.isEmpty(), () -> {
                oldToasts.remove(toast);
                new ParallelAnimation(400).addAnimation(new AlphaAnimation(toast, 0))
                        .addAnimation(new TranslateYAnimation(toast, 0)
                                .setLateTo(() -> toast.getTranslationY() -
                                        SizeUtils.dipToPx(60, this)))
                        .setInterpolator(Interpolator.EASE_OUT).setOnFinished(() -> root.removeView(toast)).start();
            }), duration);

            oldToasts.add(0, toast);
        });
    }

    public void toast(String content, String... params) {
        toast(content, toastDur, params);
    }

    public void toast(String content, long dur, String... params) {
        Label lab = new ColoredLabel(this, Style.TEXT_NORM, content);
        lab.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        lab.setLineSpacing(10);
        lab.setFont(new Font(20));
        for (int i = 0; i < params.length; i++) {
            lab.addParam(i, params[i]);
        }
        toast(dur, lab);
    }

    public Insets getSystemInsets() {
        return systemInsets;
    }

    public void hideKeyboard() {
        View currentFocus = getWindow().getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
        }
    }

    public void showKeyboard(EditText input) {
        View currentFocus;
        if ((currentFocus = getWindow().getCurrentFocus()) != null) currentFocus.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
    }

    public void backPressed() {
        if (!loadedOverlay.isEmpty()) {
            loadedOverlay.get(0).back();
        } else if (loaded == null || !loaded.onBack()) {
            moveTaskToBack(true);
        }
    }

    public Property<Style> getStyle() {
        return style;
    }

    public void setBackgroundColor(int color) {
        Platform.runLater(() -> {
            int trans = adjustAlpha(color, 0.005f);
            Window win = getWindow();
            root.setBackgroundColor(color);
            win.setStatusBarColor(trans);
            win.setNavigationBarColor(trans);
            GradientDrawable b = new GradientDrawable();
            b.setColor(color);
            win.setBackgroundDrawable(b);
        });
    }

    public void applyTheme() {
        String theme = Store.getTheme();
        String color = Store.getColor();
        boolean dark = theme.equals(Style.THEME_DARK) || ((theme.equals(Style.THEME_SYSTEM)
                && isDarkMode(getResources().getConfiguration())));
        Style s = Style.get(this, dark, color);

        if (s == style.get()) return;

        Style old = style.get();
        if (old != null) {
            if (theming != null && theming.isRunning()) theming.stop();
            Animation a = new ValueAnimation(400, 0, 1) {
                @Override
                public void updateValue(float v) {
                    style.set(Style.interpolateColors(old, s, v));
                }
            }.setOnFinished(() -> {
                StyleUtils.setStyle(this, s);
                setTheme(s.isDark() ? R.style.Theme_Nejda_Dark : R.style.Theme_Nejda_Light);
            }).setInterpolator(Interpolator.OVERSHOOT);
            theming = a;
            a.start();
        } else {
            StyleUtils.setStyle(this, s);
            setTheme(s.isDark() ? R.style.Theme_Nejda_Dark : R.style.Theme_Nejda_Light);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        applyTheme();
    }

    public void applyStyle(Style style) {
        if (style == null) return;
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        WindowInsetsControllerCompat controllerCompat = new WindowInsetsControllerCompat(window, root);
        controllerCompat.setAppearanceLightStatusBars(style.isLight());
        controllerCompat.setAppearanceLightNavigationBars(style.isLight());

        setBackgroundColor(style.getBackgroundPrimary());
    }

    public boolean isDarkMode(Configuration newConfig) {
        return (newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    public Property<Locale> getLocale() {
        return locale;
    }

}
