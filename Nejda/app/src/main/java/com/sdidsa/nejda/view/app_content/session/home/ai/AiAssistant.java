package com.sdidsa.nejda.view.app_content.session.home.ai;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateYAnimation;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.Store;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.controll.ai.AiApi;
import com.sdidsa.nejda.controll.ai.AiResponse;
import com.sdidsa.nejda.view.app_content.session.home.HomePage;
import com.sdidsa.nejda.view.app_content.session.home.ai.chat.AiInput;
import com.sdidsa.nejda.view.app_content.session.home.ai.chat.AiScroller;

import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;

public class AiAssistant extends HomePage implements TextToSpeech.OnInitListener {
    private final TextToSpeech tts;

    private final StackPane top;
    private final int addToBottomPx;
    private final AiScroller chat;
    private final AiInput bottom;

    public AiAssistant(Context owner) {
        super(owner);
        tts = new TextToSpeech(owner, this);

        float addToBottomDp = 20;
        addToBottomPx = SizeUtils.dipToPx(addToBottomDp, owner);

        top = new StackPane(owner);
        top.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));

        ColoredLabel title = new ColoredLabel(owner, Style.TEXT_NORM, "NejdAi");
        title.setFont(new Font(20, FontWeight.MEDIUM));
        title.centerText();
        PaddingUtils.setPaddingHorizontalVertical(title, 48, 0, owner);

        ColoredIcon clear = new ColoredIcon(owner, Style.TEXT_SEC,
                R.drawable.create, 44);
        clear.setPadding(7);

        ColoredIcon history = new ColoredIcon(owner, Style.TEXT_SEC,
                R.drawable.history, 44);
        history.setPadding(8);

        History historyOv = new History(owner);

        top.addAligned(title, Alignment.CENTER);
        top.addAligned(clear, Alignment.TOP_RIGHT);
        top.addAligned(history, Alignment.TOP_LEFT);

        StackPane root = new StackPane(owner);

        SpacerUtils.spacer(root, Orientation.VERTICAL);

        bottom = new AiInput(owner);
        chat = new AiScroller(owner, bottom, this::speak);
        PaddingUtils.setPadding(bottom, 0, 0, 13, 0 + addToBottomDp, owner);

        root.addAligned(top, Alignment.TOP_CENTER);
        root.addAligned(chat, Alignment.CENTER);
        root.addAligned(bottom, Alignment.BOTTOM_CENTER);

        Platform.waitWhileNot(bottom::isLaidOut, () -> {
            chat.setPadding(0, top.getHeight(), 0, bottom.getHeight() - addToBottomPx);
        });

        bottom.setOnStopTts(tts::stop);

        HashMap<String, String> premade = new HashMap<>();
        premade.put(
                "d2hvIGlzIHlvdXIgZGFkZHk=",
                "TG91YXkgTW9oYW1lZCBLZXJvdWF6IGlzIG15IGRhZGR5");
        premade.put(
                "ZGFkZHkgaXMgaG9tZQ==",
                "aGkgbG91YXk=");

        bottom.setOnSend(() -> {
            String text = bottom.startLoading();
            if (text.isBlank()) {
                return;
            }
            chat.beforeSend(text);
            String encoded = encodeBase64(text);

            if(premade.containsKey(encoded)) {
                String resp = decodeBase64(premade.get(encoded));

                Platform.runAfter(() -> {
                    AiResponse mock = new AiResponse();
                    mock.addText(resp);

                    chat.afterSend(false);
                    chat.loadAiResponse(mock);

                    if (bottom.isTtsEnabled()) {
                        speak(mock.getTexts().get(0));
                    }

                    if (chat.getTitle() == null) {
                        AiApi.estimateChatTitle(chat.getConversation(), estimated -> {
                            title.animate(estimated, 50);
                            chat.setTitle(estimated);
                        });
                    }
                }, 500);

                return;
            }

            AiApi.sendGeminiRequest(chat.getConversation(), resp -> {
                chat.afterSend(false);
                chat.loadAiResponse(resp);

                if (bottom.isTtsEnabled() && !resp.getTexts().isEmpty()) {
                    speak(resp.getTexts().get(0));
                }

                if (chat.getTitle() == null) {
                    AiApi.estimateChatTitle(chat.getConversation(), estimated -> {
                        title.animate(estimated, 50);
                        chat.setTitle(estimated);
                    });
                }
            }, err -> {
                chat.afterSend(true);
            });
        });

        addViews(root);

        clear.setOnClick(() -> {
            chat.newConversation();
            title.setKey("NejdAi");
        });

        historyOv.setOnSelect(conv -> {
            chat.loadConversation(conv);
            title.setKey(conv.getTitle());
        });

        history.setOnClick(() -> {
            historyOv.setHistory(Store.chatHistory(), chat.getChatId());
            historyOv.show();
        });

        historyOv.setNewChat(() -> {
            clear.fire();
            historyOv.hide();
        });
    }

    public static String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public static String decodeBase64(String base64Input) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Input);
        return new String(decodedBytes);
    }

    public void speak(String text) {
        for (Voice voice : tts.getVoices()) {
            if (voice.getName().equals("en-us-x-tpc-network")) {
                tts.setVoice(voice);
            }
        }
        tts.stop();
        tts.speak(text.replace('*', ' '), TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported.");
            }
        } else {
            Log.e("TTS", "Initialization failed.");
        }
    }

    @Override
    public boolean setup(boolean direction) {
        int by = SizeUtils.by(owner);
        bottom.setTranslationY(by + addToBottomPx);
        top.setTranslationY(-by);
        setAlpha(0);

        new ParallelAnimation(400)
                .addAnimation(new TranslateYAnimation(bottom, addToBottomPx))
                .addAnimation(new TranslateYAnimation(top, 0))
                .addAnimation(Animation.scaleUpIn(chat))
                .addAnimation(new AlphaAnimation(this, 1))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                .start();

        return true;
    }

    @Override
    public boolean destroy(boolean direction) {
        super.destroy(direction);
        int by = SizeUtils.by(owner);

        new ParallelAnimation(400)
                .addAnimation(new TranslateYAnimation(bottom, by + addToBottomPx))
                .addAnimation(new TranslateYAnimation(top, -by))
                .addAnimation(Animation.scaleDownOut(chat))
                .addAnimation(new AlphaAnimation(this, 0))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                .start();
        return true;
    }

    @Override
    public void freeResources() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.freeResources();
    }

    @Override
    public void applyInsets(Insets insets) {
        int add = SizeUtils.dipToPx(15, owner);
        top.setPadding(add, add + insets.top, add, add);

        chat.applyInsets();
    }
}
