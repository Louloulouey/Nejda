package com.sdidsa.nejda.abs.locale;

import com.sdidsa.nejda.abs.data.observable.ChangeListener;
import com.sdidsa.nejda.abs.data.property.Property;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.Platform;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface Localized {
    void applyLocale(Locale locale);
    default void applyLocale(Property<Locale> locale) {
        bindLocale(this, locale);
    }

    static void bindLocale(Localized node, Property<Locale> locale) {
        try {
            bindLocaleWeak(node, locale);
        }catch(Exception x) {
            ErrorHandler.handle(x, "binding locale");
        }
    }

    Set<WeakReference<Localized>> bound_cache =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static boolean isBound(Localized node) {
        bound_cache.removeIf(n -> n.get() == null);
        for(WeakReference<Localized> nodeRef : bound_cache) {
            if(nodeRef.get() == node) return true;
        }
        return false;
    }

    private static void bindLocaleWeak(Localized node, Property<Locale> locale) {
        node.applyLocale(locale.get());
        if(isBound(node)) return;
        WeakReference<Localized> weakNode = new WeakReference<>(node);
        ChangeListener<Locale> listener = new ChangeListener<>() {
            @Override
            public void changed(Locale ov, Locale nv) {
                if (weakNode.get() != null) {
                    if (nv != ov) {
                        weakNode.get().applyLocale(nv);
                    }
                } else {
                    Platform.runBack(() -> {
                        locale.removeListener(this);
                        bound_cache.remove(weakNode);
                    });
                }
            }
        };
        locale.addListener(listener);
        bound_cache.add(weakNode);
    }
}
