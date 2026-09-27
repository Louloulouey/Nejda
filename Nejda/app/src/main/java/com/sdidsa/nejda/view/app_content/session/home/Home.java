package com.sdidsa.nejda.view.app_content.session.home;

import android.content.Context;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.abs.UiCache;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.position.TranslateYAnimation;
import com.sdidsa.nejda.abs.components.Page;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.fragment.Fragment;
import com.sdidsa.nejda.abs.components.layout.fragment.FragmentPane;
import com.sdidsa.nejda.abs.components.layout.overlay.MultipleOptionOverlay;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.Store;
import com.sdidsa.nejda.abs.utils.TempStore;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.controll.UserApi;
import com.sdidsa.nejda.view.app_content.auth.login.Login;
import com.sdidsa.nejda.view.app_content.session.home.main.Main;
import com.sdidsa.nejda.view.app_content.session.home.maps.Maps;
import com.sdidsa.nejda.view.app_content.session.nav.NavBar;
import com.sdidsa.nejda.view.app_content.session.nav.NavIcon;

public class Home extends Page {

    private final FragmentPane content;
    private final NavBar navBar;

    private final int addToBottomPx;

    public Home(Context owner) {
        super(owner);
        float addToBottomDp = 20;
        addToBottomPx = SizeUtils.dipToPx(addToBottomDp, owner);

        content = new FragmentPane(owner, HomePage.class);
        content.setClipToPadding(false);

        navBar = new NavBar(owner, content);

        Platform.runBack(() -> {
            Fragment.getInstance(owner, Maps.class);
        });

        addAligned(content, Alignment.TOP_CENTER);
        addAligned(navBar, Alignment.BOTTOM_CENTER);
    }

    public NavBar getNavBar() {
        return navBar;
    }

    private int lastIndex = 0;

    @Override
    public Animation setup(int direction) {
        content.unload();
        navBar.select(lastIndex);
        int by = SizeUtils.by(owner);
        navBar.setTranslationY(by + addToBottomPx);
        return new ParallelAnimation(400)
                .addAnimation(new TranslateYAnimation(navBar, addToBottomPx))
                .addAnimation(Animation.fadeIn(this))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
    }

    @Override
    public Animation destroy(int direction) {
        content.destory();
        lastIndex = navBar.selectedIndex();
        lastIndex = lastIndex == -1 ? 0 : lastIndex;
        navBar.unselect();
        int by = SizeUtils.by(owner);
        return new ParallelAnimation(400)
                .addAnimation(new TranslateYAnimation(navBar, by + addToBottomPx))
                .addAnimation(Animation.fadeOut(this))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
    }

    private MultipleOptionOverlay onExit;

    @Override
    public boolean onBack() {
        if(content.getLoaded() != null && content.getLoaded() instanceof Main) {
            if(onExit == null) {
                onExit = new MultipleOptionOverlay(owner,
                        "Do you want to leave ?",
                        s -> s.equals("stay"));

                onExit.addButton("Close App",() -> ContextUtils.finishAndRemoveTask(owner));
                onExit.addButton("Log Out", () -> {
                    onExit.startLoading("Log Out");
                    UserApi.logout(TempStore.getUser(), user -> {
                        Store.setUserId("", null);
                        onExit.hide();
                        UiCache.clearAll();
                        ContextUtils.loadPage(owner, Login.class);
                        onExit.stopLoading("Log Out");
                    });
                });
                onExit.addButton("stay", onExit::hide);

                onExit.setHideInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
                onExit.setHideDuration(400);
            }

            onExit.show();
        } else {
            navBar.select(0);
            content.previousInto(Main.class);
        }
        return true;
    }

    @Override
    public void applyInsets(Insets insets) {
        if(insets == null) return;

        boolean keyboard = insets.bottom > ContextUtils.getScreenHeight(owner) / 4;

        navBar.setPadding(0, 0, 0,
                keyboard ? 0 : (insets.bottom + addToBottomPx));
        content.setPadding(0, 0,0,insets.bottom +
                (keyboard ? 0 : SizeUtils.dipToPx(NavIcon.SIZE, owner)));
        content.applyInsets(insets);
    }
}
