package com.sdidsa.nejda.view.app_content.session.home.chats.input;

import android.Manifest;
import android.content.Context;
import android.widget.LinearLayout;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.position.TranslateXAnimation;
import com.sdidsa.nejda.abs.components.controls.audio.AudioRecorder;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.input.MinimalInputField;
import com.sdidsa.nejda.abs.components.controls.scratches.loading.ColoredSpinLoading;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.abs.CornerUtils;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredVBox;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.data.media.Media;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.abs.utils.view.PermissionUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.view.app_content.session.attachments.Attachments;
import com.sdidsa.nejda.view.app_content.session.attachments.CaptureImage;

import java.io.File;

public class ChatInputArea extends ColoredVBox {


    private final Attachments attachments;
    private final StackPane bottom;

    private final StackPane preSend;
    private final ColoredSpinLoading loading;

    private final MinimalInputField messageInput;
    private final VoiceRecordingView voiceRecordingView;
    private final ColoredIcon sendButton;

    private boolean isRecording = false;
    private final AudioRecorder audioRecorder;

    private CaptureImage ci;
    
    private OnMessageSendListener messageSendListener;
    private OnAudioRecordListener audioRecordListener;

    public ChatInputArea(Context context) {
        super(context, Style.BACK_SEC);

        attachments = new Attachments(context, Style.BACK_PRI);
        bottom = new StackPane(context);

        attachments.setVisibility(GONE);

        bottom.setPadding(8, 8, 8, 8);
        bottom.setLayoutParams(new LayoutParams(-1, -2));
        bottom.setClipToPadding(false);

        messageInput = new MinimalInputField(context, "Message");
        messageInput.setCornerRadius(CornerUtils.DEFAULT);
        messageInput.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        messageInput.setFont(new Font(18));
        messageInput.getPreInput().setPadding(5);
        messageInput.setMultiline(5);
        PaddingUtils.setPadding(messageInput.getInput(), 20, 11, 5, 11, context);
        messageInput.setBackFill(Style.BACK_PRI);
        messageInput.setRadius(28);

        sendButton = new ColoredIcon(context, Style.ACCENT, R.drawable.send, 48);
        sendButton.setImagePadding(10);

        preSend = new StackPane(owner);

        loading = new ColoredSpinLoading(owner, Style.TEXT_SEC, 48);
        loading.setPadding(8);

        preSend.addCentered(sendButton);
        preSend.setAlpha(0);

        HBox icons = new HBox(context);
        icons.setAlignment(Alignment.TOP_RIGHT);
        icons.setLayoutParams(new LinearLayout.LayoutParams(-2, -1));

        ColoredIcon micButton = new ColoredIcon(context, Style.ACCENT, R.drawable.mic_fill, 48);
        micButton.setImagePadding(12);

        ColoredIcon imageButton = new ColoredIcon(context, Style.ACCENT, R.drawable.photo_video, 48);
        imageButton.setImagePadding(12);
        imageButton.setOnClick(() -> {
            ContextUtils.pickImages(context, media -> {
                attachments.add(media.toArray(new Media[0]));
            });
        });

        ColoredIcon camera = new ColoredIcon(owner, Style.ACCENT, R.drawable.camera, 48);
        camera.setImagePadding(12);
        camera.setOnClick(() -> {
            if(ci == null) {
                ci = new CaptureImage(context);
                ci.setOnCapture(bmp -> {
                    ci.hide();
                    attachments.add(bmp);
                });
            }

            PermissionUtils.requirePermissions(owner, ci::show, Manifest.permission.CAMERA);
        });

        icons.addViews(camera, imageButton, micButton, preSend);

        Platform.waitWhileNot(preSend::isLaidOut, () -> {
            int w = preSend.getWidth();
            icons.setTranslationX(w);
        });

        Runnable show = () -> new ParallelAnimation(400)
                .addAnimation(new TranslateXAnimation(icons, 0))
                .addAnimation(Animation.fadeIn(preSend))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                .start();
        Runnable hide = () -> new ParallelAnimation(400)
                .addAnimation(new TranslateXAnimation(icons, preSend.getWidth()))
                .addAnimation(Animation.fadeOut(preSend))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                .start();

        messageInput.valueProperty().addListener((ov, nv) -> {
            if (ov.isBlank() && !nv.isBlank() && attachments.isEmpty()) {
                show.run();
            } else if (!ov.isBlank() && nv.isBlank() && attachments.isEmpty()) {
                hide.run();
            }
        });

        attachments.addListener((src, c) -> {
            if(!attachments.isEmpty() && !c.getAdded().isEmpty() && messageInput.getValue().isBlank()) {
                show.run();
            } else if(attachments.isEmpty() && !c.getRemoved().isEmpty() && messageInput.getValue().isBlank()) {
                hide.run();
            }
        });

        audioRecorder = new AudioRecorder();

        voiceRecordingView = new VoiceRecordingView(context);
        voiceRecordingView.setOnRecordingActionListener(new VoiceRecordingView.OnRecordingActionListener() {
            @Override
            public void onStopRecording() {
                stopRecording(false);
            }

            @Override
            public void onSendRecording() {
                stopRecording(true);
            }
        });

        micButton.setOnClick(this::toggleRecording);
        sendButton.setOnClick(() -> {
            if (messageSendListener != null) {
                messageSendListener.onSendTextMessage(messageInput.getValue());
            }
        });

        messageInput.addPostInput(icons);

        bottom.addCentered(messageInput);
        bottom.addCentered(voiceRecordingView);

        addViews(attachments, bottom);
    }

    public void startLoading() {
        loading.startLoading();
        preSend.removeView(sendButton);
        preSend.addView(loading);
    }

    public void stopLoading() {
        preSend.removeView(loading);
        preSend.addView(sendButton);
        loading.stopLoading();
    }

    public Attachments getAttachments() {
        return attachments;
    }

    public void toggleRecording() {
        if (!isRecording) {
            new ParallelAnimation(400)
                    .addAnimation(Animation.fadeOutDown(getContext(), messageInput))
                    .addAnimation(Animation.fadeInDown(getContext(), voiceRecordingView))
                    .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                    .start();

            voiceRecordingView.resetWaveform();

            audioRecorder.startRecording(getContext(), () -> {
                audioRecorder.addAmplitudeListener(voiceRecordingView::updateWaveform);

                audioRecorder.setTimeUpdateListener(voiceRecordingView::updateTimer);

                if (audioRecordListener != null) {
                    audioRecordListener.onRecordingStarted();
                }
            });
            isRecording = true;
        } else {
            stopRecording(false);
        }
    }

    private void stopRecording(boolean shouldSend) {
        if(!isRecording) return;
        audioRecorder.stopRecording(audioFile -> {
            new ParallelAnimation(400)
                    .addAnimation(Animation.fadeInUp(getContext(), messageInput))
                    .addAnimation(Animation.fadeOutUp(getContext(), voiceRecordingView))
                    .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                    .start();

            if (audioRecordListener != null) {
                audioRecordListener.onRecordingStopped();
            }

            if (shouldSend && audioRecordListener != null) {
                audioRecordListener.onSaveAudio(audioFile);
            }
        });
        
        isRecording = false;
    }
    
    public boolean isRecording() {
        return isRecording;
    }
    
    public void setMessageText(String text) {
        messageInput.setValue(text);
    }
    
    public String getMessageText() {
        return messageInput.getValue();
    }
    
    public void clearMessageText() {
        messageInput.setValue("");
    }
    
    public void setOnMessageSendListener(OnMessageSendListener listener) {
        this.messageSendListener = listener;
    }
    
    public void setOnAudioRecordListener(OnAudioRecordListener listener) {
        this.audioRecordListener = listener;
    }

    public void applyInsets(Insets insets) {
        int add = SizeUtils.dipToPx(13, owner);
        bottom.setPadding(add, add, add, add + insets.bottom);
    }
    
    public interface OnMessageSendListener {
        void onSendTextMessage(String message);
    }
    
    public interface OnAudioRecordListener {
        void onRecordingStarted();
        void onRecordingStopped();
        void onSaveAudio(File audioFile);
    }
} 