package com.sdidsa.nejda.view.app_content.session.attachments;


import android.content.Context;
import android.graphics.Bitmap;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.image.Image;
import com.sdidsa.nejda.abs.components.controls.image.ImageProxy;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;

import java.io.File;

public class ImageAttachment extends BasicAttachment {
    private final Bitmap media;

    public ImageAttachment(Context owner) {
        this(owner, null);
    }
    public ImageAttachment(Context owner, Bitmap media) {
        super(owner);
        setCornerRadius(10);
        this.media = media;
        Image img = new Image(owner);
        img.setSize(80);
        img.setCornerRadius(10);
        img.setAlpha(.4f);

        if(media != null) {
            Bitmap thumb = ImageProxy.generateThumbnail(media, SizeUtils.dipToPx(80, owner));
            img.setImageBitmap(thumb);
        }

        ColoredIcon remove = new ColoredIcon(owner, Style.TEXT_NORM, R.drawable.close, 42);
        remove.setPadding(15);
        remove.setRadiusNoClip(48);
        remove.setBorder(Style.TEXT_NORM, 2);

        addAligned(img, Alignment.CENTER);
        addAligned(remove, Alignment.CENTER);
    }

    @Override
    public File toFile(String name) {
        return ImageProxy.saveTemp(media, name);
    }
}
