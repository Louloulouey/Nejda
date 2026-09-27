package com.sdidsa.nejda.abs.components.controls.image;

import android.content.Context;
import android.graphics.Bitmap;

import com.sdidsa.nejda.abs.components.controls.scratches.loading.ColoredSpinLoading;
import com.sdidsa.nejda.abs.data.property.Property;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class NetImage extends Image implements Styleable {
    private final ColoredSpinLoading loading;
    private final StyleToColor fill;

    public NetImage(Context owner) {
        this(owner, null);
    }

    public NetImage(Context owner, StyleToColor fill) {
        super(owner);

        this.fill = fill;

        loading = new ColoredSpinLoading(owner, Style.TEXT_SEC);

        applyStyle(StyleUtils.getStyle(owner));
    }

    public void setLoadingColor(StyleToColor fill) {
        loading.setFill(fill);
    }

    public void startLoading() {
        view.setAlpha(.2f);
        removeView(loading);
        addCentered(loading);
        loading.startLoading();
    }

    public void stopLoading() {
        view.setAlpha(viewAlpha);
        removeView(loading);
        loading.stopLoading();
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        stopLoading();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        stopLoading();
    }

    private String url;

    public void setImageUrl(String url) {
        this.url = url;
        startLoading();
        ImageProxy.getImage(owner, url, this::setImageBitmap);
    }

    public void setImageThumbUrl(String url, float size) {
        this.url = url;
        startLoading();
        setSize(size);
        ImageProxy.getImageThumb(owner, url,
                SizeUtils.dipToPx(size, owner),
                this::setImageBitmap);
    }

    public void setImageThumbUrl(String url, float width, float height) {
        this.url = url;
        startLoading();
        setSize(width, height);
        ImageProxy.getImageThumb(owner, url,
                SizeUtils.dipToPx(width, owner),
                SizeUtils.dipToPx(height, owner),
                this::setImageBitmap);
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void setSize(float size) {
        super.setSize(size);

        loading.setSize(Math.min(size / 2, 72));
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);

        loading.setSize(Math.min((width + height) / 4, 72));
    }

    @Override
    public void applyStyle(Style style) {
        if(fill == null) return;
        setBackgroundColor(fill.get(style));
    }

    @Override
    public void applyStyle(Property<Style> style) {
        if(fill == null) return;
        Styleable.bindStyle(this, style);
    }
}
