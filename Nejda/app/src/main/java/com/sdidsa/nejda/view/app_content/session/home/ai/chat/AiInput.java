package com.sdidsa.nejda.view.app_content.session.home.ai.chat;

import android.content.Context;
import android.widget.FrameLayout;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.input.MinimalInputField;
import com.sdidsa.nejda.abs.components.controls.scratches.loading.ColoredSpinLoading;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.abs.CornerUtils;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredHBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;

public class AiInput extends ColoredHBox {
    private final MinimalInputField input;
    private final StackPane preSend;
    private final ColoredIcon send;
    private final ColoredSpinLoading loading;

    private Runnable onSend;

    private boolean ttsEnabled = true;

    private Runnable onStopTts;

    public AiInput(Context owner) {
        super(owner, Style.BACK_SEC);
        setCornerRadius(CornerUtils.cornerTopRadius(owner, 30));
        setAlignment(Alignment.CENTER);
        setLayoutParams(new FrameLayout.LayoutParams(-1, -2));

        preSend = new StackPane(owner);

        loading = new ColoredSpinLoading(owner, Style.TEXT_SEC, 40);
        loading.setPadding(4);

        send = new ColoredIcon(owner, Style.TEXT_SEC,
                R.drawable.send, 40);
        send.setPadding(5);

        preSend.addCentered(send);

        input = new MinimalInputField(owner, "Type your message...");
        input.setBackFill(Style.EMPTY);
        input.setFont(new Font(20));
        PaddingUtils.setPaddingHorizontalVertical(input.getInput(), 10, 18, owner);
        PaddingUtils.setPaddingUnified(input.getPreInput(), 0, owner);
        input.setMultiline(4);
        SpacerUtils.spacer(input, -2, -2);

        input.valueProperty().addListener((ov, nv) -> {
            send.setFill(nv.isEmpty() ? Style.TEXT_SEC : Style.TEXT_NORM);
        });

        send.setOnClick(() -> {
            if (onSend != null) {
                onSend.run();
            }
        });

        StackPane tts = new StackPane(owner);
        MarginUtils.setMarginLeft(tts, owner, 15);

        ColoredIcon ttsOn = new ColoredIcon(owner, Style.TEXT_SEC, R.drawable.tts_on, 48);
        ttsOn.setPadding(12);
        ColoredIcon ttsOff = new ColoredIcon(owner, Style.TEXT_SEC, R.drawable.tts_off, 48);
        ttsOff.setPadding(12);
        ttsOff.setAlpha(0);

        tts.addCentered(ttsOn);
        tts.addCentered(ttsOff);

        tts.setOnClickListener(v -> {

            ColoredIcon from = ttsEnabled ? ttsOn : ttsOff;
            ColoredIcon to = ttsEnabled ? ttsOff : ttsOn;

            new ParallelAnimation(400)
                    .addAnimation(Animation.fadeOutUp(owner, from))
                    .addAnimation(Animation.fadeInUp(owner, to))
                    .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                    .start();
            setTtsEnabled(!ttsEnabled);

            ContextUtils.toast(owner, "Text to speech " + (ttsEnabled ? "enabled" : "disabled"));

            if (!ttsEnabled && onStopTts != null) onStopTts.run();
        });

        addViews(tts, input, preSend);
    }

    public void setOnStopTts(Runnable onStopTts) {
        this.onStopTts = onStopTts;
    }

    public void setTtsEnabled(boolean ttsEnabled) {
        this.ttsEnabled = ttsEnabled;
    }

    public boolean isTtsEnabled() {
        return ttsEnabled;
    }

    public void setOnSend(Runnable onSend) {
        this.onSend = onSend;
    }

    public String startLoading() {
        String text = input.getValue().trim();
        if (text.isBlank()) {
            return "";
        }
        input.setValue("");
        loading.startLoading();
        preSend.removeView(send);
        preSend.addView(loading);
        return text;
    }

    public void stopLoading() {
        preSend.removeView(loading);
        preSend.addView(send);
        loading.stopLoading();
    }

    public void setValue(String value) {
        input.setValue(value);
    }
}
