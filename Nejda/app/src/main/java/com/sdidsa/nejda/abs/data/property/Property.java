package com.sdidsa.nejda.abs.data.property;

import androidx.annotation.NonNull;

import com.sdidsa.nejda.abs.data.ConcurrentArrayList;
import com.sdidsa.nejda.abs.data.observable.ChangeListener;
import com.sdidsa.nejda.abs.data.observable.Observable;
import com.sdidsa.nejda.abs.utils.ErrorHandler;

import java.util.Objects;

public class Property<T> implements Observable<T> {
    private final ConcurrentArrayList<ChangeListener<? super T>> listeners = new ConcurrentArrayList<>();
    private T value;
    private Observable<T> boundTo;
    private boolean bound;
    private ChangeListener<T> onBoundChanged;

    public Property() {
    }

    public Property(T value) {
        this.value = value;
    }

    public boolean isBound() {
        return bound;
    }

    public void bind(Observable<T> bindTo) {
        if (bound) {
            ErrorHandler.handle(new IllegalStateException(
                    "you can't bind this property because it's already bound"),
                    "binding property");
        }
        boundTo = bindTo;

        onBoundChanged = (ov, nv) -> set(nv);

        boundTo.addListener(onBoundChanged);
        bound = true;
    }

    public void unbind() {
        if (bound) {
            boundTo.removeListener(onBoundChanged);
            boundTo = null;
            bound = false;
        }
    }

    @Override
    public void set(T value) {
        T ov = this.value;
        this.value = value;
        if (!Objects.equals(ov, value)) {
            listeners.forEach(l -> l.changed(ov, value));
        }
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void addListener(ChangeListener<? super T> listener) {
        listener.changed(value, value);
        listeners.add(listener);
    }

    @Override
    public void addListener(Runnable action) {
        action.run();
        listeners.add((ov, nv) -> action.run());
    }

    public boolean isNull() {
        return value == null;
    }

    @Override
    public void removeListener(ChangeListener<? super T> listener) {
        listeners.remove(listener);
    }

    @Override
    public void clearListeners() {
        listeners.clear();
    }

    @NonNull
    @Override
    public String toString() {
        return "Property{" +
                "value=" + value +
                '}';
    }
}
