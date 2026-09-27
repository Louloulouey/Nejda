package com.sdidsa.nejda.abs.components.layout.scroll;

import android.content.Context;
import android.widget.LinearLayout;

import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;

public abstract class RecyclerItemView<T> extends StackPane {
    public RecyclerItemView(Context owner) {
        super(owner);
        setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        MarginUtils.setMarginUnified(this, owner, 10);
    }

    public abstract void load(T item);
}
