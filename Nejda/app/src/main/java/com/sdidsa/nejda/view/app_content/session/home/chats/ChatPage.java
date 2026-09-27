package com.sdidsa.nejda.view.app_content.session.home.chats;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.App;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateYAnimation;
import com.sdidsa.nejda.abs.components.Page;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.scratches.loading.ColoredLinearLoading;
import com.sdidsa.nejda.abs.components.controls.scratches.loading.Loading;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredHBox;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.components.layout.scroll.Recycler;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.TempStore;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.controll.MessageApi;
import com.sdidsa.nejda.model.Message;
import com.sdidsa.nejda.model.MessageAttachment;
import com.sdidsa.nejda.model.Thread;
import com.sdidsa.nejda.services.Action;
import com.sdidsa.nejda.services.BroadcastListener;
import com.sdidsa.nejda.view.app_content.session.attachments.BasicAttachment;
import com.sdidsa.nejda.view.app_content.session.home.Home;
import com.sdidsa.nejda.view.app_content.session.home.chats.display.MessageItem;
import com.sdidsa.nejda.view.app_content.session.home.chats.input.ChatInputArea;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChatPage extends Page {
    private final HBox top;
    private final int addToBottomPx;
    
    private final VBox content;
    private Thread thread;

    private final Label head;
    
    private final Recycler<Message, MessageItem> messageList;
    private final Loading messagesLoading;
    
    private final ChatInputArea inputArea;

    public ChatPage(Context owner) {
        super(owner);

        BroadcastListener receiver = new BroadcastListener(owner);
        receiver.onIntent(Action.MESSAGE_RECEIVED, this::messageReceived);
        
        float addToBottomDp = 20;
        addToBottomPx = SizeUtils.dipToPx(addToBottomDp, owner);
        
        StackPane root = new StackPane(owner);
        SpacerUtils.spacer(root, -1, -1);
        
        top = new ColoredHBox(owner, Style.BACK_SEC);
        top.setAlignment(Alignment.CENTER_LEFT);
        top.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        
        ColoredIcon backButton = new ColoredIcon(owner, Style.TEXT_NORM, R.drawable.arrow_left, 48);
        backButton.setImagePadding(13);
        backButton.setOnClick(this::onBack);
        
        head = new ColoredLabel(owner, Style.TEXT_NORM, "...");
        head.setFont(new Font(20, FontWeight.MEDIUM));
        MarginUtils.setMarginLeft(head, owner, 10);
        
        top.addViews(backButton, head);
        
        content = new VBox(owner);
        content.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        
        messagesLoading = new ColoredLinearLoading(owner,
                s -> App.adjustAlpha(s.getTextSecondary(), 0.5f), 12);
        MarginUtils.setMarginTop(messagesLoading.getLoadingView(), owner, 20);
        
        messageList = new Recycler<>(owner, MessageItem.class);
        messageList.setClipToOutline(true);
        messageList.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));

        inputArea = new ChatInputArea(owner);
        inputArea.setLayoutParams(new LayoutParams(-1, -2));

        inputArea.setOnMessageSendListener(this::sendMessage);

        inputArea.setOnAudioRecordListener(new ChatInputArea.OnAudioRecordListener() {
            @Override
            public void onRecordingStarted() {
                //TODO Handle recording started event if needed
            }

            @Override
            public void onRecordingStopped() {
                //TODO Handle recording stopped event if needed
            }

            @Override
            public void onSaveAudio(File audioFile) {
                inputArea.getAttachments().addAudio(audioFile);
            }
        });
        
        root.addAligned(top, Alignment.TOP_CENTER);
        root.addAligned(content, Alignment.TOP_CENTER);
        root.addAligned(inputArea, Alignment.BOTTOM_CENTER);
        
        addView(root);
    }

    private Message pending = null;
    private void messageReceived(Intent intent) {
        String msgData = intent.getStringExtra("message");
        try {
            Message msg = new Message(new JSONObject(msgData));

            ArrayList<Message> cache = messageCache.get(msg.getThread().getId());
            if(cache != null) {
                cache.add(msg);
            }

            if(thread != null &&
                    msg.getThread().getId().intValue() == thread.getId().intValue()) {
                if(pending != null &&
                        pending.getSender().getUserId().equals(msg.getSender().getUserId())) {
                    messageList.removeItem(pending);
                    inputArea.stopLoading();
                    inputArea.clearMessageText();
                    inputArea.getAttachments().reset();
                }
                messageList.addItem(msg);
                messageList.smoothScrollToBottom();
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(String messageText) {
        if (messageText.isBlank() && inputArea.getAttachments().isEmpty()) {
            return;
        }
        inputArea.startLoading();
        List<BasicAttachment> toUpload = new ArrayList<>(inputArea.getAttachments().getMedia());
        Platform.runBack(() -> {
            pending = new Message(new JSONObject());
            pending.setSender(TempStore.getUser());
            pending.setThread(thread);
            pending.setText(messageText);
            pending.setCreatedAt(LocalDateTime.now());

            Platform.runLater(() -> {
                messageList.addItem(pending);
                messageList.smoothScrollToBottom();
            });

            List<MessageAttachment> atts = inputArea.getAttachments().uploadAll(toUpload,
                            "message_" + pending.getSender().getUserId(),
                            System.currentTimeMillis() % 100000)
                    .stream().map(uatt ->
                            new MessageAttachment(uatt.url(), uatt.type()))
                    .collect(Collectors.toList());

            pending.setAttachments(atts);


            MessageApi.createMessage(pending, response -> {
                if(response == null) {
                    inputArea.setMessageText(messageText);
                    ContextUtils.toast(owner, "Something went wrong");
                    pending = null;
                }
            });
        });
    }

    private final HashMap<Long, ArrayList<Message>> messageCache = new HashMap<>();
    
    public void fetchMessages() {
        content.removeView(messageList);
        content.addView(messagesLoading.getLoadingView());

        if(messageCache.containsKey(thread.getId())) {
            handleMessages(Objects.requireNonNull(messageCache.get(thread.getId())));
        } else {
            messagesLoading.startLoading();
            messageCache.put(thread.getId(), new ArrayList<>());
            MessageApi.getMessagesByThread(thread.getId(), this::handleMessages);
        }
    }

    private void handleMessages(List<Message> messages) {
        if (messages.isEmpty()) {
            messageList.setItems(new ArrayList<>());
            content.removeView(messagesLoading.getLoadingView());
            content.addView(messageList);
            messagesLoading.stopLoading();
            return;
        }
        ArrayList<Message> cache = messageCache.get(messages.get(0).getThread().getId());
        assert cache != null;
        if(messages != cache) {
            cache.clear();
            cache.addAll(messages);
        }
        messageList.setAlpha(0);
        messageList.setItems(messages);
        content.removeView(messagesLoading.getLoadingView());
        content.addView(messageList);
        messagesLoading.stopLoading();
        Platform.runLater(() -> {
            messageList.scrollToBottom();
            Animation.fadeInDown(owner, messageList)
                    .setDuration(400)
                    .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                    .start();
        });
    }

    @Override
    public Animation setup(int direction) {
        Platform.waitWhileNot(top::isLaidOut, this::applyInsets);
        super.setup(direction);
        this.thread = TempStore.getThread();

        if(thread.getRestricted()) {
            inputArea.setVisibility(GONE);
        } else {
            inputArea.setVisibility(VISIBLE);
        }

        head.setKey(thread.getTitle());
        fetchMessages();

        int by = SizeUtils.by(owner);
        top.setTranslationY(-(by + addToBottomPx));
        setAlpha(0);
        content.setAlpha(0);
        setTranslationX(0);
        
        inputArea.setTranslationY(by + addToBottomPx);
        
        return new ParallelAnimation(400)
                .addAnimation(new TranslateYAnimation(top, -addToBottomPx))
                .addAnimation(new AlphaAnimation(this, 1))
                .addAnimation(Animation.fadeInDown(owner, content))
                .addAnimation(new TranslateYAnimation(inputArea, 0))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
    }
    
    @Override
    public Animation destroy(int direction) {
        super.destroy(direction);
        thread = null;
        int by = SizeUtils.by(owner);
        
        return new ParallelAnimation(400)
                .addAnimation(new TranslateYAnimation(top, -(by + addToBottomPx)))
                .addAnimation(new AlphaAnimation(this, 0))
                .addAnimation(Animation.fadeOutUp(owner, content))
                .addAnimation(new TranslateYAnimation(inputArea, by + addToBottomPx))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
    }
    
    public void applyInsets() {
        int add = SizeUtils.dipToPx(5, owner);
        content.setPadding(add, add + top.getHeight() - addToBottomPx, add, add +
                inputArea.getHeight());
    }

    @Override
    public boolean onBack() {
        if (inputArea.isRecording()) {
            inputArea.toggleRecording();
            return true;
        }
        
        ContextUtils.loadPage(owner, Home.class);
        return true;
    }

    @Override
    public void applyInsets(Insets insets) {
        int addC = SizeUtils.dipToPx(5, owner);

        int add = SizeUtils.dipToPx(13, owner);
        top.setPadding(add, add + insets.top + addToBottomPx, add, add);
        inputArea.applyInsets(insets);

        Platform.runLater(() -> {
            content.setPadding(addC, addC + top.getHeight() - addToBottomPx, addC,
                    addC + inputArea.getHeight());

            Platform.runLater(messageList::scrollToBottom);
        });
    }
} 