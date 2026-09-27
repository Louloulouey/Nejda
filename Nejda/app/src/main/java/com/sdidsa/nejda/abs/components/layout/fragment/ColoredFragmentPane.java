package com.sdidsa.nejda.abs.components.layout.fragment;

import android.content.Context;

import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class ColoredFragmentPane extends FragmentPane implements Styleable {
    private final StyleToColor fill;
    public ColoredFragmentPane(Context owner) {
        this(owner, null);
    }
    public ColoredFragmentPane(Context owner, StyleToColor fill) {
        this(owner, Fragment.class, fill);
    }

    public ColoredFragmentPane(Context owner, Class<? extends Fragment> type, StyleToColor fill) {
        super(owner, type);
        this.fill = fill;
        applyStyle(StyleUtils.getStyle(owner));
    }

    @Override
    public void applyStyle(Style style) {
        if(fill != null)
            setBackgroundColor(fill.get(style));
    }

}
