package com.sdidsa.nejda.abs.style;

import com.sdidsa.nejda.abs.data.observable.ChangeListener;
import com.sdidsa.nejda.abs.data.property.Property;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.Platform;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface Styleable {
    void applyStyle(Style style);
    default void applyStyle(Property<Style> style) {
        bindStyle(this, style);
    }

    static void bindStyle(Styleable node, Property<Style> style) {
        try {
            bindStyleWeak(node, style);
        }catch(Exception x) {
            ErrorHandler.handle(x, "binding style");
        }
    }

    Set<WeakReference<Styleable>> bound_cache =
            Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static boolean isBound(Styleable node) {
        bound_cache.removeIf(n -> n.get() == null);
        for(WeakReference<Styleable> nodeRef : bound_cache) {
            if(nodeRef.get() == node) return true;
        }
        return false;
    }

    private static void bindStyleWeak(Styleable node, Property<Style> style) {
        node.applyStyle(style.get());
        if(isBound(node)) return;
        WeakReference<Styleable> weakNode = new WeakReference<>(node);
        ChangeListener<Style> listener = new ChangeListener<>() {
            @Override
            public void changed(Style ov, Style nv) {
                if (weakNode.get() != null) {
                    if (nv != ov) {
                        weakNode.get().applyStyle(nv);
                    }
                } else {
                    Platform.runBack(() -> {
                        style.removeListener(this);
                        bound_cache.remove(weakNode);
                    });
                }
            }
        };
        style.addListener(listener);
        bound_cache.add(weakNode);
    }
}
