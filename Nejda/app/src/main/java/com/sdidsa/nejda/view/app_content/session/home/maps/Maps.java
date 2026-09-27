package com.sdidsa.nejda.view.app_content.session.home.maps;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.components.controls.button.ColoredButton;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.input.checkBox.CheckBox;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.scratches.loading.ColoredSpinLoading;
import com.sdidsa.nejda.abs.components.controls.scratches.loading.Loading;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.abs.CornerUtils;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.controll.AlertApi;
import com.sdidsa.nejda.view.app_content.session.home.HomePage;
import com.sdidsa.nejda.view.app_content.session.home.maps.render.FilamentThread;
import com.sdidsa.nejda.view.app_content.session.home.maps.render.MapView;
import com.sdidsa.nejda.view.app_content.session.report.Severity;

import java.util.Objects;

public class Maps extends HomePage {

    private MapView map;
    private final Loading loading;

    private final StackPane root;

    private final HBox controls;

    private final ColoredLabel dangerZone;

    public Maps(Context owner) {
        super(owner);

        root = new StackPane(owner);
        root.setLayoutParams(new LayoutParams(-1, -1));

        loading = new ColoredSpinLoading(owner, Style.TEXT_SEC, 72);
        loading.getLoadingView().setAlpha(0);
        root.addCentered(loading.getLoadingView());
        loading.startLoading();

        controls = new HBox(owner);
        controls.setAlpha(0);
        controls.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        controls.setPadding(15);

        ColoredButton autoFocus = new ColoredButton(owner, Style.BACK_SEC, Style.TEXT_NORM,
                "Auto focus");
        autoFocus.setElevation(0);
        autoFocus.setRadius(CornerUtils.DEFAULT);
        PaddingUtils.setPaddingHorizontalVertical(autoFocus, 15, 0, owner);
        autoFocus.setFont(new Font(20, FontWeight.MEDIUM));
        autoFocus.setLayoutParams(new LayoutParams(-2, SizeUtils.dipToPx(56, owner)));

        CheckBox checkBox = new CheckBox(owner, 24);
        checkBox.setChecked(true);
        MarginUtils.setMarginRight(checkBox, owner, 15);
        checkBox.checkedProperty().addListener((ov, nv) -> {
            if (map != null)
                map.setAutoFocus(nv);
        });

        autoFocus.addPreLabel(checkBox);
        autoFocus.setOnClick(checkBox::toggle);

        ColoredIcon route = new ColoredIcon(owner,
                Style.TEXT_NORM, Style.BACK_SEC,
                R.drawable.route, 56);
        route.setPadding(15);
        route.setRadiusNoClip(CornerUtils.DEFAULT);

        route.setOnClick(() -> map.findPath());

        dangerZone = new ColoredLabel(owner, Style.TEXT_SEC, "");
        dangerZone.setFont(new Font(18, FontWeight.MEDIUM));

        dangerZone.setCornerRadius(CornerUtils.DEFAULT);
        dangerZone.setPadding(18);
        dangerZone.setLayoutParams(new LayoutParams(-1, -2));
        dangerZone.setBack(Style.BACK_SEC);
        dangerZone.setAlpha(0);
        MarginUtils.setMarginUnified(dangerZone, owner, 20);

        controls.addViews(
                route,
                SpacerUtils.spacer(owner, Orientation.HORIZONTAL),
                autoFocus);

        FilamentThread filamentThread = new FilamentThread();
        filamentThread.start();

        filamentThread.postOnStart(() -> {
            map = new MapView(owner);
            Platform.runLater(() -> root.addCentered(0, map));
            map.startRendering();
            Platform.runAfter(() -> setup(true), 500);
        });

        root.addAligned(dangerZone, Alignment.TOP_CENTER);
        root.addAligned(controls, Alignment.BOTTOM_RIGHT);

        addView(root);
    }

    @Override
    public void freeResources() {
        super.freeResources();

        map.onDestroy();
    }

    @Override
    public boolean destroy(boolean direction) {
        if (map == null) return true;
        map.stopRendering();
        new ParallelAnimation(400)
                .addAnimation(Animation.fadeOutDown(owner, controls))
                .addAnimation(Animation.fadeOutScaleDown(map))
                .addAnimation(Animation.fadeOutUp(owner, dangerZone))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                .start();

        return true;
    }

    @Override
    public boolean setup(boolean direction) {
        if (map == null) {
            Animation.fadeIn(loading.getLoadingView())
                    .setDuration(400)
                    .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                    .start();
            return true;
        }
        dangerZone.setAlpha(0);
        map.startRendering();
        map.clearFocus();
        Platform.runLater(() -> {
            map.clearPath();
        });
        AlertApi.getActive(alert -> {
            if(alert != null && alert.getLocation() != null) {
                dangerZone.setKey("Danger zone : ".concat(alert.getLocation()));
                StyleToColor color =
                        Objects.requireNonNull(
                                Severity.bySeverity(alert.getSeverity())).getColor();
                dangerZone.setFill(color);
                map.setDanger(alert.getLocation(), color);
            } else {
                map.clearDanger();
                dangerZone.setKey("No danger zone");
                dangerZone.setFill(Style.TEXT_SEC);
            }
            Animation.fadeInDown(owner, dangerZone)
                    .setDuration(400)
                    .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                    .start();
        });
        new ParallelAnimation(400)
                .addAnimation(Animation.fadeInUp(owner, controls))
                .addAnimation(Animation.fadeInScaleUp(map))
                .setOnFinished(() -> {
                    loading.stopLoading();
                    root.removeView(loading.getLoadingView());
                })
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                .start();
        return true;
    }

    @Override
    public void applyInsets(Insets insets) {
        super.applyInsets(insets);
        root.setPadding(0, insets.top, 0, 0);
    }
}