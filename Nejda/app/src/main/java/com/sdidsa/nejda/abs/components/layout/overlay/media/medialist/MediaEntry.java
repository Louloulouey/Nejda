package com.sdidsa.nejda.abs.components.layout.overlay.media.medialist;

import android.content.Context;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.padding.UnifiedPaddingAnimation;
import com.sdidsa.nejda.abs.components.controls.image.Image;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.data.media.Media;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class MediaEntry extends Image implements Styleable {
    private final int size;
    private final Animation press;
    private final Animation release;

    private final ColoredLabel index;

    public MediaEntry(Context owner) {
        this(owner, 256);
    }

    public MediaEntry(Context owner, int size) {
        super(owner);
        setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        this.size = size;
        setSize(SizeUtils.pxToDip(size, owner));

        index = new ColoredLabel(owner, Style.ONACCENT, "");
        index.setFont(new Font(36, FontWeight.MEDIUM));
        index.setAlpha(0);

        addCentered(index);

        press = new ParallelAnimation(300)
                .addAnimation(new UnifiedPaddingAnimation(view,
                        SizeUtils.dipToPx(10, owner)))
                .addAnimation(new AlphaAnimation(view, .4f))
                .addAnimation(Animation.fadeInScaleUp(index))
                .setInterpolator(Interpolator.EASE_OUT);
        release = new ParallelAnimation(300)
                .addAnimations(new UnifiedPaddingAnimation(view, 0))
                .addAnimations(new AlphaAnimation(view, 1f))
                .addAnimation(Animation.fadeOutScaleDown(index))
                .setInterpolator(Interpolator.EASE_OUT);

        setImagePadding(1);

        view.setScaleType(ImageView.ScaleType.CENTER_CROP);

        setPadding(0, 0, 0, 0);

        applyStyle(StyleUtils.getStyle(owner));
    }

    public void select() {
        release.stop();
        press.start();
    }

    public void deselect() {
        press.stop();
        release.start();
    }

    public void setIndex(int index) {
        if(index == -1) {
            this.index.setText("");
            return;
        }
        this.index.setText(String.valueOf(index + 1));
    }

    public void load(Media media) {
        setImageResource(R.drawable.empty);
        media.getThumbnail(
                getOwner(),
                size * 2,
                this::setImageBitmap,
                () -> setImageResource(R.drawable.problem)
        );
    }

    @Override
    public void applyStyle(Style style) {
        setBackgroundColor(style.getBackgroundPrimary());
        view.setBackgroundColor(style.getAccent());
    }

}