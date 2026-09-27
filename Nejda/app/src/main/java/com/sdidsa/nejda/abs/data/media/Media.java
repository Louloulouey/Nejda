package com.sdidsa.nejda.abs.data.media;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.sdidsa.nejda.abs.components.controls.image.ImageProxy;

import java.util.function.Consumer;

public record Media(long id, Uri uri, String name) {

    public void getThumbnail(Context owner, int size, Consumer<Bitmap> onResult, Runnable onFail) {
        ImageProxy.getThumbnail(owner, uri, size, onResult, onFail);
    }
}
