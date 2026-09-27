package com.sdidsa.nejda.abs.components;

import android.content.Context;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.abs.UiCache;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateXAnimation;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.fragment.Fragment;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.view.LocaleUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Page extends StackPane {
    private static final ConcurrentHashMap<Class<? extends Page>, Page> cache = new ConcurrentHashMap<>();
    protected final Context owner;

    public Page(Context owner) {
        super(owner);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setFocusable(false);
        setClipChildren(false);
        setClipToPadding(false);

        this.owner = owner;
    }

    public static boolean hasInstance(Class<? extends Page> type) {
        return cache.containsKey(type);
    }

    public synchronized static <T extends Page> T getInstance(Context owner, Class<T> type) {
        Page found = cache.get(type);
        if (found == null || found.getOwner() != owner) {
            try {
                found = type.getConstructor(Context.class).newInstance(owner);
                cache.put(type, found);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                ErrorHandler.handle(e, "creating page instance of type " + type.getName());
            }
        }
        if (!type.isInstance(found)) {
            ErrorHandler.handle(new RuntimeException("incorrect page type"),
                    "loading page of type " + type.getName());
        }
        return type.cast(found);
    }

    protected Animation setup;
    public Animation setup(int direction) {
        if(setup == null) {
            setup = new ParallelAnimation(500)
                    .addAnimation(new AlphaAnimation(this, 1))
                    .addAnimation(new TranslateXAnimation(this, 0))
                    .setInterpolator(Interpolator.EASE_OUT);
        }
        setAlpha(0);
        setTranslationX(SizeUtils.by(owner) * direction * LocaleUtils.getLocaleDirection(owner));

        return setup;
    }

    protected Animation destroy;
    public Animation destroy(int direction) {
        if(destroy == null) {
            destroy = new ParallelAnimation(500)
                    .addAnimation(new AlphaAnimation(this, 0))
                    .addAnimation(new TranslateXAnimation(this, -direction * SizeUtils.by(owner)))
                    .setInterpolator(Interpolator.EASE_OUT);
        }
        return destroy;
    }

    public void clear() {

    }

    public static void clearCache() {
        cache.clear();
    }

    public static void clearCache(Class<? extends Page> type) {
        List<Class<? extends Page>> concerned = new ArrayList<>();
        for (Class<? extends Page> c : cache.keySet()) {
            if (type.isAssignableFrom(c)) {
                concerned.add(c);
            }
        }
        concerned.forEach(t -> {
            Page f;
            if((f = cache.get(t)) != null) {
                f.clear();
            }
        });
        concerned.forEach(cache::remove);
    }

    static {
        UiCache.register(Page::clearCache);
    }

    public abstract boolean onBack();

    public abstract void applyInsets(Insets insets);
}
