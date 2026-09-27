package com.sdidsa.nejda.view.app_content.misc;

import android.content.Context;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.components.Page;
import com.sdidsa.nejda.abs.components.controls.button.Button;
import com.sdidsa.nejda.abs.components.controls.button.ColoredButton;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;

public class NetErr extends Page {

    public NetErr(Context owner) {
        super(owner);

        VBox root = new VBox(owner);
        root.setSpacing(50);
        root.setAlignment(Alignment.TOP_CENTER);
        root.setPadding(20);

        HBox top = new HBox(owner);
        top.setAlignment(Alignment.CENTER);
        ColoredIcon heart = new ColoredIcon(owner, Style.ACCENT, R.drawable.heart_broken, 72);
        ColoredLabel name = new ColoredLabel(owner, Style.ACCENT, "").setFont(new Font(42, FontWeight.BOLD));
        name.setText(ContextUtils.getAppName(owner));
        top.addViews(heart, name);

        MarginUtils.setMarginLeft(name, owner, 20);

        ColoredLabel rest = new ColoredLabel(owner,
                Style.TEXT_NORM, "Failed to connect to the servers..."
        )
                .setFont(new Font(20, FontWeight.MEDIUM));
        rest.centerText();

        Button retry = new ColoredButton(owner,
                Style.ACCENT,
                Style.ONACCENT, "Retry");
        retry.setFont(new Font(20, FontWeight.MEDIUM));
        retry.setElevation(0);

        retry.setOnClick(() -> ContextUtils.postCreate(owner));

        VBox buttons = new VBox(owner);
        buttons.setSpacing(10);
        buttons.addViews(retry);

        root.addViews(SpacerUtils.spacer(owner, Orientation.VERTICAL), top, rest, buttons, SpacerUtils.spacer(owner, Orientation.VERTICAL));

        addCentered(root);
    }

    @Override
    public Animation setup(int direction) {
        if(setup == null) {
            setup = new ParallelAnimation(400)
                    .addAnimation(Animation.fadeInScaleUp(this))
                    .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
        }
        setAlpha(0);
        return setup;
    }

    @Override
    public Animation destroy(int direction) {
        if(destroy == null) {
            destroy = new ParallelAnimation(400)
                    .addAnimation(Animation.fadeOutScaleDown(this))
                    .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
        }
        return destroy;
    }

    @Override
    public boolean onBack() {
        return false;
    }

    @Override
    public void applyInsets(Insets insets) {
        setPadding(insets.left, insets.top, insets.right, insets.bottom);
    }
}
