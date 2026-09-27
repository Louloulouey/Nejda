package com.sdidsa.nejda.abs.data.property;

import com.sdidsa.nejda.abs.data.ConcurrentArrayList;
import com.sdidsa.nejda.abs.data.observable.ListChange;
import com.sdidsa.nejda.abs.data.observable.ListChangeListener;
import com.sdidsa.nejda.abs.data.observable.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ObservableArrayList<T> extends ArrayList<T> implements ObservableList<T> {
    private final ConcurrentArrayList<ListChangeListener<T>> listeners = new ConcurrentArrayList<>();

    public ObservableArrayList() {
        super();
    }

    public ObservableArrayList(List<T> values) {
        super(values);
    }

    @Override
    public boolean add(T t) {
        ArrayList<T> original = new ArrayList<>(this);
        boolean res = super.add(t);
        ListChange<T> c = new ListChange<>(original, this);
        listeners.forEach(l -> l.changed(this, c));
        return res;
    }

    @Override
    public T remove(int index) {
        ArrayList<T> original = new ArrayList<>(this);
        T res = super.remove(index);
        ListChange<T> c = new ListChange<>(original, this);
        listeners.forEach(l -> l.changed(this, c));
        return res;
    }

    @Override
    public boolean remove(Object o) {
        ArrayList<T> original = new ArrayList<>(this);
        boolean res = super.remove(o);
        ListChange<T> c = new ListChange<>(original, this);
        listeners.forEach(l -> l.changed(this, c));
        return res;
    }

    @Override
    public void setAll(List<T> data) {
        ArrayList<T> original = new ArrayList<>(this);

        super.clear();
        super.addAll(data);

        ListChange<T> c = new ListChange<>(original, this);

        listeners.forEach(l -> l.changed(this, c));
    }

    @Override
    public void clear() {
        ArrayList<T> original = new ArrayList<>(this);
        super.clear();
        ListChange<T> c = new ListChange<>(original, this);
        listeners.forEach(l -> l.changed(this, c));
    }

    @Override
    public void addListener(ListChangeListener<T> listener) {
        listener.changed(this, new ListChange<>(this, this));
        listeners.add(listener);
    }

    @Override
    public void addListener(Runnable action) {
        addListener((src, c) -> action.run());
    }

    @Override
    public void removeListener(ListChangeListener<T> listener) {
        listeners.remove(listener);
    }

    @Override
    public void clearListeners() {
        listeners.clear();
    }
}
