package com.sdidsa.nejda.abs.components.layout.overlay.media;

import android.content.Context;

import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.components.layout.overlay.PartialSlideOverlay;
import com.sdidsa.nejda.abs.components.layout.overlay.media.bucketlist.BucketList;
import com.sdidsa.nejda.abs.data.media.Bucket;

import java.util.List;
import java.util.function.Consumer;

public class BucketOverlay extends PartialSlideOverlay {
    private final BucketList bucketList;
    private Consumer<Bucket> onBucket;

    public BucketOverlay(Context owner) {
        super(owner, .4);

        bucketList = new BucketList(owner);
        bucketList.setOnAction(bucket -> {
            if (onBucket != null) {
                hide();
                onBucket.accept(bucket);
            }
        });
        list.addView(bucketList);

        list.setCornerRadiusBottom(20);
        setInterpolator(Interpolator.EASE_OUT);
        setTint(false);
    }

    public void setOnBucket(Consumer<Bucket> onBucket) {
        this.onBucket = onBucket;
    }

    public void setData(List<Bucket> data) {
        bucketList.setData(data);
    }
}