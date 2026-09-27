package com.sdidsa.nejda.abs.data.observable;

public interface Observable<T> {
    void set(T value);
    T get();
    void addListener(ChangeListener<? super T> listener);
    void addListener(Runnable action);
    void removeListener(ChangeListener<? super T> listener);
    void clearListeners();
}
