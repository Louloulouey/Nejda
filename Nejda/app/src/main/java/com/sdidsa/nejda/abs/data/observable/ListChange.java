package com.sdidsa.nejda.abs.data.observable;

import java.util.List;
import java.util.stream.Collectors;

public class ListChange<T> {
    private final List<T> added;
    private final List<T> removed;
    private final List<T> oldState;
    private final List<T> newState;

    public ListChange(List<T> added, List<T> removed, List<T> oldState, List<T> newState) {
        this.added = added;
        this.removed = removed;
        this.oldState = oldState;
        this.newState = newState;
    }

    public ListChange(List<T> ov, List<T> nv) {
        added = nv.stream().filter(i -> !ov.contains(i)).collect(Collectors.toList());
        removed = ov.stream().filter(i -> !nv.contains(i)).collect(Collectors.toList());
        oldState = ov;
        newState = nv;
    }

    public List<T> getAdded() {
        return added;
    }

    public List<T> getRemoved() {
        return removed;
    }

    public List<T> getOldState() {
        return oldState;
    }

    public List<T> getNewState() {
        return newState;
    }
}
