package com.sdidsa.nejda.view.app_content.session.nav;


import android.content.Context;

import androidx.annotation.DrawableRes;

import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateYAnimation;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;

public class NavIcon extends StackPane {
    public static final int SIZE = 85;

    private final ColoredIcon fill;
    private final ColoredIcon outline;
    private final ColoredLabel label;

    private final NavBar parent;

    private Runnable onSelect;


    public NavIcon(Context owner, @DrawableRes int fillRes, @DrawableRes int outlineRes, String text,
                   NavBar parent) {
        super(owner);
        this.parent = parent;

        fill = new ColoredIcon(owner, Style.ACCENT, fillRes, SIZE);
        outline = new ColoredIcon(owner, Style.ACCENT, outlineRes, SIZE);

        label = new ColoredLabel(owner, Style.ACCENT, text);
        label.setFont(new Font(14, FontWeight.MEDIUM));
        label.setAlpha(0);

        addCentered(fill);
        addCentered(outline);
        addCentered(label);

        fill.setAlpha(0);

        setOnClickListener(e -> select());

        fill.setPadding(30);
        outline.setPadding(30);
        SpacerUtils.spacer(this, -2, -2);
    }

    public void setOnSelect(Runnable onSelect) {
        this.onSelect = onSelect;
    }

    public synchronized void select() {
        if(parent.getSelected() == this) return;
        if(parent.getSelected() != null) parent.getSelected().unselect();

        float div = 5.5f;
        new ParallelAnimation(400)
                .addAnimation(Animation.fadeOut(outline))
                .addAnimation(Animation.fadeIn(fill))
                .addAnimation(new AlphaAnimation(label, 1))
                .addAnimation(new TranslateYAnimation(label, SizeUtils.dipToPx(SIZE / div, owner)))
                .addAnimation(new TranslateYAnimation(fill, -SizeUtils.dipToPx(SIZE / div, owner)))
                .addAnimation(new TranslateYAnimation(outline, -SizeUtils.dipToPx(SIZE / div, owner)))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                        .start();

        if(onSelect != null) onSelect.run();
        parent.setSelected(this);
    }

    public synchronized void unselect() {

        new ParallelAnimation(400)
                .addAnimation(Animation.fadeOut(fill))
                .addAnimation(Animation.fadeIn(outline))
                .addAnimation(new AlphaAnimation(label, 0))
                .addAnimation(new TranslateYAnimation(label, 0))
                .addAnimation(new TranslateYAnimation(fill, 0))
                .addAnimation(new TranslateYAnimation(outline, 0))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                .start();
    }
}
