package com.sdidsa.nejda.abs.data.observable;

import java.util.List;

public interface ObservableList<T> {
    boolean add(T value);
    T remove(int index);
    boolean remove(T object);
    void setAll(List<T> data);
    void clear();

    void addListener(ListChangeListener<T> listener);
    void addListener(Runnable action);
    void removeListener(ListChangeListener<T> listener);
    void clearListeners();
}