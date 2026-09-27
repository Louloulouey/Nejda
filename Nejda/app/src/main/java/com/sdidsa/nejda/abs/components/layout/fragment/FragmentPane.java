package com.sdidsa.nejda.abs.components.layout.fragment;

import android.content.Context;
import androidx.core.graphics.Insets;
import android.widget.FrameLayout;

import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.position.TranslateXAnimation;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;

import java.util.concurrent.Semaphore;

public class FragmentPane extends FrameLayout {
    protected final Context owner;
    Animation running = null;
    private Fragment loaded;
    private final Class<? extends Fragment> type;

    public FragmentPane(Context owner) {
        this(owner, Fragment.class);
    }

    public FragmentPane(Context owner, Class<? extends Fragment> type) {
        super(owner);
        this.owner = owner;
        this.type = type;
        setClipChildren(false);
        if(type.equals(Fragment.class)) {
            ErrorHandler.handle(new IllegalArgumentException("you shouldn't use raw Fragment type"),
                    "create fragment pane of type Fragment");
        }
        Fragment.clearCache(type);
    }

    public void nextInto(Class<? extends Fragment> pageType, Runnable post) {
        navigateInto(pageType, 1, post);
    }

    public void nextInto(Class<? extends Fragment> pageType) {
        navigateInto(pageType, 1, null);
    }

    public void previousInto(Class<? extends Fragment> pageType, Runnable post) {
        navigateInto(pageType, -1, post);
    }

    public void previousInto(Class<? extends Fragment> pageType) {
        navigateInto(pageType, -1, null);
    }

    private final Semaphore mutex = new Semaphore(1);
    private void navigateInto(Class<? extends Fragment> fragmentType, int direction,
                              Runnable post) {
        if (!type.isAssignableFrom(fragmentType)) {
            ErrorHandler.handle(new IllegalArgumentException("incompatible types"),
                    "navigate to fragment of type " + fragmentType.getName() +
                    "in a fragment pane of type " + type.getName());
        }

        Platform.runLater(() -> ContextUtils.hideKeyboard(owner));

        Platform.runBack(() -> {
            mutex.acquireUninterruptibly();
            Platform.waitWhile(this::isRunning);

            Fragment old = loaded;
            if (old != null && fragmentType.isInstance(old)) {
                if(post != null) post.run();
                loaded = old;
                mutex.release();
                return;
            }

            float fromX = old == null ? 0 : (ContextUtils.getScreenWidth(owner) * direction) / (direction == 1 ? 1f : 2f);
            float toX = -(ContextUtils.getScreenWidth(owner) * direction) / (direction == 1 ? 2f : 1f);

            Fragment nw = Fragment.getInstance(owner, fragmentType);
            if (nw == null) {
                mutex.release();
                return;
            }

            loaded = nw;
            boolean ignoreTrans = loaded.setup(direction == 1);

            ParallelAnimation showNew = new ParallelAnimation(400)
                    .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
            if(!ignoreTrans) {
                showNew.addAnimation(new TranslateXAnimation(loaded, fromX / 2, 0));
                showNew.addAnimation(Animation.fadeIn(loaded));
            }
            if(old != null) {
                ignoreTrans = old.destroy(direction == 1);
                if(!ignoreTrans) {
                    showNew.addAnimation(new TranslateXAnimation(old, 0, toX / 2));
                    showNew.addAnimation(Animation.fadeOut(old));
                }
            }
            showNew.setOnFinished(() -> {
                if(post != null) post.run();
                if(old != null) {
                    removeView(old);
                }
            });

            running = showNew;

            Platform.runLater(() -> {
                loaded.setPane(this);
                loaded.applyInsets(ContextUtils.getSystemInsets(owner));
                showNew.start();
                addView(loaded);
                mutex.release();
            });
        });
    }

    public void applyInsets(Insets insets) {
        if(loaded == null) return;
        loaded.applyInsets(insets);
    }

    public void unload() {
        if(loaded == null) return;
        removeView(loaded);
        loaded = null;
    }

    public void destory() {
        if(loaded == null) return;
        loaded.destroy(false);
    }

    public boolean isRunning() {
        return running != null && running.isRunning();
    }

    public Fragment getLoaded() {
        return loaded;
    }
}
