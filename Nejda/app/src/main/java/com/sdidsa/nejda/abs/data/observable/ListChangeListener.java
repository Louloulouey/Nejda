package com.sdidsa.nejda.abs.data.observable;

import com.sdidsa.nejda.abs.data.property.ObservableArrayList;

public interface ListChangeListener<T> {
    void changed(ObservableArrayList<T> source, ListChange<T> change);
}
