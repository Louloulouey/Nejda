package com.sdidsa.nejda.abs.components.layout.fragment;

import android.content.Context;
import androidx.core.graphics.Insets;

import com.sdidsa.nejda.abs.UiCache;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.utils.ErrorHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Fragment extends VBox {
    private static final ConcurrentHashMap<Class<? extends Fragment>, Fragment> cache =
            new ConcurrentHashMap<>();

    private FragmentPane pane;

    public Fragment(Context owner) {
        super(owner);
        setSpacing(10);
    }

    public synchronized static <T extends Fragment> T getInstance(Context owner, Class<T> type) {
        Fragment found = cache.get(type);
        if (found == null || found.getOwner() != owner) {
            if(found != null) found.clear();
            try {
                found = type.getConstructor(Context.class).newInstance(owner);
                cache.put(type, found);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                     NoSuchMethodException e) {
                ErrorHandler.handle(e, "creating fragment of type " + type.getName());
            }
        }
        if (!type.isInstance(found)) {
            ErrorHandler.handle(new RuntimeException("incorrect fragment type"),
                    "loading fragment of type " + type.getName());
        }
        return type.cast(found);
    }

    public static void clearCache(Class<? extends Fragment> type) {
        List<Class<? extends Fragment>> concerned = new ArrayList<>();
        for (Class<? extends Fragment> c : cache.keySet()) {
            if (type.isAssignableFrom(c)) {
                concerned.add(c);
            }
        }
        concerned.forEach(t -> {
            Fragment f;
            if((f = cache.get(t)) != null) {
                f.freeResources();
            }
        });
        concerned.forEach(cache::remove);
    }

    public FragmentPane getPane() {
        return pane;
    }

    public void setPane(FragmentPane pane) {
        this.pane = pane;
    }

    public static void clearCache() {
        cache.values().forEach(Fragment::freeResources);
        cache.clear();
    }

    static {
        UiCache.register(Fragment::clearCache);
    }

    public boolean setup(boolean direction) {
        return false;
    }

    //navigate to another fragment
    public boolean destroy(boolean direction) {
        return false;
    }

    //reset fragment to its initial state
    public void clear() {

    }

    //clear all resources on cache clearing
    public void freeResources() {

    }

    public void applyInsets(Insets insets) {
        //Override if necessary
    }
}
