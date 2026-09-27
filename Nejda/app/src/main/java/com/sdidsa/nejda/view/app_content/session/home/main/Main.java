package com.sdidsa.nejda.view.app_content.session.home.main;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.App;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.LinearHeightAnimation;
import com.sdidsa.nejda.abs.animation.view.RotateAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateYAnimation;
import com.sdidsa.nejda.abs.components.Page;
import com.sdidsa.nejda.abs.components.controls.image.ColorIcon;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.scratches.loading.ColoredLinearLoading;
import com.sdidsa.nejda.abs.components.controls.scratches.loading.Loading;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredHBox;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.components.layout.scroll.Recycler;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.TempStore;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.controll.AlertApi;
import com.sdidsa.nejda.model.Alert;
import com.sdidsa.nejda.services.Action;
import com.sdidsa.nejda.services.BroadcastListener;
import com.sdidsa.nejda.view.app_content.session.home.Home;
import com.sdidsa.nejda.view.app_content.session.home.HomePage;
import com.sdidsa.nejda.view.app_content.session.profile.ViewProfileOverlay;
import com.sdidsa.nejda.view.app_content.session.report.ReportEmergency;
import com.sdidsa.nejda.view.app_content.session.contacts.ContactsPage;

public class Main extends HomePage {
    private final HBox top;
    private final int addToBottomPx;

    private final VBox content;

    private final MainStatus status;

    private final Recycler<Alert, RecentAlert> recentAlerts;

    private final Loading recentLoading;

    private final VBox bottomHalf;

    private int topHalfHeight = -1;

    private boolean topHalfOpen = true;

    public Main(Context owner) {
        super(owner);
        float addToBottomDp = 20;
        addToBottomPx = SizeUtils.dipToPx(addToBottomDp, owner);

        StackPane root = new StackPane(owner);
        SpacerUtils.spacer(root, Orientation.VERTICAL);

        top = new ColoredHBox(owner, Style.BACK_SEC);
        top.setAlignment(Alignment.CENTER_LEFT);
        top.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));

        ColorIcon logo = new ColoredIcon(owner, Style.TEXT_NORM, R.drawable.logo, 48);
        logo.setImagePadding(7);

        Label head = new ColoredLabel(owner, Style.TEXT_NORM, TempStore.getUser().getFirstName());
        head.setFont(new Font(22, FontWeight.MEDIUM));
        SpacerUtils.spacer(head, -2, -2);

        MarginUtils.setMarginLeft(head, owner, 10);

        ColorIcon alerts = new ColoredIcon(owner, Style.TEXT_NORM, R.drawable.bell_fill, 48);
        alerts.setImagePadding(12);

        ColorIcon close = new ColoredIcon(owner, Style.TEXT_NORM, R.drawable.home_fill, 48);
        close.setImagePadding(12);
        close.setAlpha(0);

        ColoredIcon userIcon = new ColoredIcon(owner, Style.TEXT_NORM, R.drawable.user_norm, 48);
        userIcon.setImagePadding(10);

        StackPane preAlerts = new StackPane(owner);

        userIcon.setOnClick(() -> {
            ViewProfileOverlay.getInstance(owner).show(TempStore.getUser());
        });

        VBox topHalf = new VBox(owner);
        topHalf.setSpacing(15);

        bottomHalf = new VBox(owner);
        bottomHalf.setSpacing(15);

        int dy = SizeUtils.dipToPx(15, owner);
        preAlerts.setOnClickListener((v) -> {
            if(topHalfOpen) {
                new ParallelAnimation(400)
                        .addAnimation(new AlphaAnimation(topHalf, 1, 0))
                        .addAnimation(new LinearHeightAnimation(topHalf, topHalfHeight, 0)
                                .setLateFromInt(() -> {
                                    topHalfHeight = topHalf.getHeight();
                                    return topHalfHeight;
                                }))
                        .addAnimation(new TranslateYAnimation(bottomHalf, -dy))
                        .addAnimation(new AlphaAnimation(alerts, 1, 0))
                        .addAnimation(new AlphaAnimation(close, 0, 1))
                        .addAnimation(new RotateAnimation(alerts, 90))
                        .addAnimation(new RotateAnimation(close, -90, 0))
                        .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                        .setOnFinished(() -> {
                            topHalf.setVisibility(GONE);
                            bottomHalf.setTranslationY(0);
                        })
                        .start();
            } else {
                topHalf.setAlpha(0);
                bottomHalf.setTranslationY(-dy);
                topHalf.setVisibility(VISIBLE);
                new ParallelAnimation(400)
                        .addAnimation(new AlphaAnimation(topHalf, 0, 1))
                        .addAnimation(new LinearHeightAnimation(topHalf, 0, topHalfHeight))
                        .addAnimation(new TranslateYAnimation(bottomHalf, 0))
                        .addAnimation(new AlphaAnimation(close, 1, 0))
                        .addAnimation(new AlphaAnimation(alerts, 0, 1))
                        .addAnimation(new RotateAnimation(close, 90))
                        .addAnimation(new RotateAnimation(alerts, -90, 0))
                        .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                        .start();
            }
            topHalfOpen = !topHalfOpen;
        });

        preAlerts.addViews(alerts, close);
        top.addViews(logo, head, preAlerts, userIcon);

        content = new VBox(owner);
        content.setSpacing(15);

        status = new MainStatus(owner);

        MainAction report = new MainAction(owner, "Report Emergency", R.drawable.danger);
        report.setOnClick(() -> ContextUtils.loadPage(owner, ReportEmergency.class));
        MainAction evac = new MainAction(owner, "Evacuation Routes", R.drawable.route);

        MarginUtils.setMarginRight(report, owner, 8);
        MarginUtils.setMarginLeft(evac, owner, 8);

        MainAction contacts = new MainAction(owner, "Call for help", R.drawable.contacts);
        contacts.setOnClick(() -> {
            ContextUtils.loadPage(owner, ContactsPage.class);
        });
        MainAction zones = new MainAction(owner, "Safe Zones", R.drawable.map_fill);
        zones.setOnClick(() -> {
            Page.getInstance(owner, Home.class).getNavBar().select(1);
        });

        MarginUtils.setMarginRight(contacts, owner, 8);
        MarginUtils.setMarginLeft(zones, owner, 8);

        HBox actions1 = new HBox(owner);
        actions1.setLayoutParams(new LayoutParams(-1, -2));
        HBox actions2 = new HBox(owner);
        actions2.setLayoutParams(new LayoutParams(-1, -2));

        actions1.addViews(report, evac);
        actions2.addViews(contacts, zones);

        Label alertsHeader = new ColoredLabel(owner, Style.TEXT_NORM, "Recent Alerts");
        alertsHeader.setFont(new Font(20, FontWeight.MEDIUM));
        MarginUtils.setMarginLeft(alertsHeader, owner, 15);

        recentLoading = new ColoredLinearLoading(owner,
                s -> App.adjustAlpha(s.getTextSecondary(), 0.5f), 12);
        MarginUtils.setMarginTop(recentLoading.getLoadingView(), owner, 20);

        recentAlerts = new Recycler<>(owner, RecentAlert.class);
        recentAlerts.setLayoutParams(new LayoutParams(-1, -1));

        recentAlerts.setOnRefresh(()->
                Animation.fadeOut(recentAlerts)
                    .setInterpolator(Interpolator.OVERSHOOT)
                    .setOnFinished(this::fetchRecentAlerts)
                    .start());

        topHalf.addViews(status, actions1, actions2);
        bottomHalf.addView(alertsHeader);
        content.addViews(topHalf, bottomHalf);

        root.addAligned(top, Alignment.TOP_CENTER);
        root.addAligned(content, Alignment.TOP_CENTER);

        Platform.waitWhileNot(top::isLaidOut, this::applyInsets);

        addView(root);



        BroadcastListener receiver = new BroadcastListener(owner);
        receiver.on(Action.ALERT_CHANGED, () -> {
            status.fetch();
            fetchRecentAlerts();
        });
    }

    public void fetchRecentAlerts() {
        recentLoading.startLoading();
        bottomHalf.removeView(recentAlerts);
        bottomHalf.addView(recentLoading.getLoadingView());
        AlertApi.getRecent(alerts -> {
            recentAlerts.setAlpha(0);
            recentAlerts.setItems(alerts);
            bottomHalf.removeView(recentLoading.getLoadingView());
            bottomHalf.addView(recentAlerts);
            recentLoading.startLoading();
            Animation.fadeInUp(owner, recentAlerts)
                    .setInterpolator(Interpolator.OVERSHOOT)
                    .start();
        });
    }

    @Override
    public boolean setup(boolean direction) {
        status.fetch();
        if(recentAlerts.getItemList().isEmpty()) {
            fetchRecentAlerts();
        }

        int by = SizeUtils.by(owner);
        top.setTranslationY(-(by + addToBottomPx));
        setAlpha(0);

        content.setAlpha(1);
        content.setTranslationY(0);

        new ParallelAnimation(400)
                .addAnimation(new TranslateYAnimation(top, -addToBottomPx))
                .addAnimation(new AlphaAnimation(this, 1))
                .addAnimation(Animation.sequenceFadeInUpParent(owner, content))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                .start();

        return true;
    }

    @Override
    public boolean destroy(boolean direction) {
        super.destroy(direction);
        int by = SizeUtils.by(owner);

        new ParallelAnimation(400)
                .addAnimation(new TranslateYAnimation(top, -(by + addToBottomPx)))
                .addAnimation(new AlphaAnimation(this, 0))
                .addAnimation(Animation.fadeOutUp(owner, content))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                .start();
        return true;
    }

    public void applyInsets() {
        int add = SizeUtils.dipToPx(15, owner);
        content.setPadding(add,add + top.getHeight()- addToBottomPx,add,add);
    }

    @Override
    public void applyInsets(Insets insets) {
        int add = SizeUtils.dipToPx(10, owner);
        top.setPadding(add, add + insets.top + addToBottomPx,add,add);
        applyInsets();
    }
}
