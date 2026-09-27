package com.sdidsa.nejda.view.app_content.session.home.ai.chat;

import android.content.Context;

import androidx.annotation.NonNull;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;

import java.util.function.Consumer;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.core.MarkwonTheme;

public class AiMessage extends HBox {
    private final ColoredLabel text;

    private boolean ttsShown = false;

    public AiMessage(Context owner, AiMessageRole role, String content, Consumer<String> speak) {
        super(owner);
        setAlignment(Alignment.CENTER_LEFT);

        StackPane preText = new StackPane(owner);

        text = new ColoredLabel(owner, role.getTextFill(), role.getBackFill(), "");
        text.setCornerRadius(20);
        PaddingUtils.setPaddingHorizontalVertical(text, 15, 10, owner);
        text.setFont(new Font(17));

        preText.addCentered(text);

        final Markwon markwon = Markwon.builder(owner)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                        builder.headingTypeface(new Font(17, FontWeight.BOLD).getFont());
                        super.configureTheme(builder);
                    }
                })
                .build();


        markwon.setMarkdown(text, content);

        setLayoutParams(new LayoutParams(-2, -2));
        MarginUtils.setMargin(preText, owner,
                role.getMarginLeft(), 10,
                role.getMarginRight(), 5);

        if (role.isSent()) {
            addView(SpacerUtils.spacer(owner, Orientation.HORIZONTAL));
        }

        addView(preText);

        if (!role.isSent()) {
            ColoredIcon icon = new ColoredIcon(owner, Style.TEXT_SEC, R.drawable.tts_on, 64);
            icon.setPadding(16);
            icon.setAlpha(0);
            icon.setBack(Style.BACK_TER);
            icon.setRadiusNoClip(64);
            preText.addCentered(icon);

            Runnable toggle = () -> {
                if (ttsShown) {
                    new ParallelAnimation(300)
                            .addAnimation(new AlphaAnimation(text, 1f))
                            .addAnimation(Animation.fadeOutScaleDown(icon))
                            .setInterpolator(Interpolator.EASE_OUT)
                            .start();
                } else {
                    new ParallelAnimation(300)
                            .addAnimation(new AlphaAnimation(text, 0.5f))
                            .addAnimation(Animation.fadeInScaleUp(icon))
                            .setInterpolator(Interpolator.EASE_OUT)
                            .start();
                }
                ttsShown = !ttsShown;
            };

            icon.setOnClick(() -> {
                if (speak != null && ttsShown) speak.accept(content);
                toggle.run();
            });

            text.setOnClickListener(v -> {
                toggle.run();
            });


            addView(SpacerUtils.spacer(owner, Orientation.HORIZONTAL));
        }
    }

    public ColoredLabel getText() {
        return text;
    }
}
