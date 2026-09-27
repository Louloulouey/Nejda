package com.sdidsa.nejda.abs.data.observable;

public interface ChangeListener<T> {
    void changed(T oldVal, T newVal);
}
