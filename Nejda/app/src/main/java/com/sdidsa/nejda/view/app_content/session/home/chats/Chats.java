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
import com.sdidsa.nejda.abs.components.controls.image.ColorIcon;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
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
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.controll.ThreadApi;
import com.sdidsa.nejda.model.Message;
import com.sdidsa.nejda.model.Thread;
import com.sdidsa.nejda.services.Action;
import com.sdidsa.nejda.services.BroadcastListener;
import com.sdidsa.nejda.view.app_content.session.home.HomePage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Chats extends HomePage {
    private final HBox top;
    private final int addToBottomPx;
    
    private final VBox content;

    private final Recycler<Thread, ThreadItem> threadList;
    private final Loading threadsLoading;

    public Chats(Context owner) {
        super(owner);
        float addToBottomDp = 20;
        addToBottomPx = SizeUtils.dipToPx(addToBottomDp, owner);

        BroadcastListener receiver = new BroadcastListener(owner);
        receiver.onIntent(Action.MESSAGE_RECEIVED, this::messageReceived);

        StackPane root = new StackPane(owner);
        SpacerUtils.spacer(root, Orientation.VERTICAL);
        
        top = new ColoredHBox(owner, Style.BACK_SEC);
        top.setAlignment(Alignment.CENTER_LEFT);
        top.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        
        Label head = new ColoredLabel(owner, Style.TEXT_NORM, "Chats and Announcements");
        head.setFont(new Font(22, FontWeight.MEDIUM));
        SpacerUtils.spacer(head, -2, -2);
        
        ColorIcon newChat = new ColoredIcon(owner, Style.TEXT_NORM, R.drawable.create, 48);
        newChat.setImagePadding(12);
        
        top.addViews(head, newChat);
        
        content = new VBox(owner);
        content.setSpacing(15);
        
        threadsLoading = new ColoredLinearLoading(owner,
                s -> App.adjustAlpha(s.getTextSecondary(), 0.5f), 12);
        MarginUtils.setMarginTop(threadsLoading.getLoadingView(), owner, 20);
        
        threadList = new Recycler<>(owner, ThreadItem.class);
        threadList.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        
        threadList.setOnRefresh(() ->
                Animation.fadeOut(threadList)
                    .setInterpolator(Interpolator.OVERSHOOT)
                    .setOnFinished(this::fetchThreads)
                    .start());
        
        root.addAligned(top, Alignment.TOP_CENTER);
        root.addAligned(content, Alignment.TOP_CENTER);
        
        Platform.waitWhileNot(top::isLaidOut, this::applyInsets);
        
        addView(root);
    }

    private void messageReceived(Intent intent) {
        String msgData = intent.getStringExtra("message");
        try {
            Message msg = new Message(new JSONObject(msgData));
            threadList.getItemList().forEach(thread -> {
               if(thread.getId().intValue() == msg.getThread().getId().intValue()) {
                    ThreadItem item = threadList.getViewFor(thread);
                    item.setLastMessage(msg);

                    if (thread.getMessages() == null) {
                        thread.setMessages(new ArrayList<>());
                    }
                    thread.getMessages().add(0, msg);
                    if (thread.getMessages().size() > 3) {
                        thread.getMessages().remove(thread.getMessages().size() - 1);
                    }
               }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void fetchThreads() {
        threadsLoading.startLoading();
        content.removeView(threadList);
        content.addView(threadsLoading.getLoadingView());

        ThreadApi.getAllThreads(ThreadItem.MAX_MESSAGES, threads -> {
            Platform.runLater(() -> {
                threadList.setAlpha(0);
                threadList.setItems(threads);
                content.removeView(threadsLoading.getLoadingView());
                content.addView(threadList);
                threadsLoading.stopLoading();
                Animation.fadeInUp(owner, threadList)
                        .setInterpolator(Interpolator.OVERSHOOT)
                        .start();
            });
        });
    }

    @Override
    public boolean setup(boolean direction) {
        if(threadList.getItemList().isEmpty()) {
            fetchThreads();
        }
        
        int by = SizeUtils.by(owner);
        top.setTranslationY(-(by + addToBottomPx));
        setAlpha(0);
        
        content.setAlpha(1);
        content.setTranslationY(0);
        
        new ParallelAnimation(400)
                .addAnimation(new TranslateYAnimation(top, -addToBottomPx))
                .addAnimation(new AlphaAnimation(this, 1))
                .addAnimation(Animation.fadeInDown(owner, content))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                .start();
        
        return true;
    }
    
    @Override
    public boolean destroy(boolean direction) {
        super.destroy(direction);
        int by = SizeUtils.by(owner);
        
        new ParallelAnimation(400)
                .addAnimation(new TranslateYAnimation(top, -(by + addToBottomPx)))
                .addAnimation(new AlphaAnimation(this, 0))
                .addAnimation(Animation.fadeOutUp(owner, content))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                .start();
        return true;
    }
    
    public void applyInsets() {
        int add = SizeUtils.dipToPx(15, owner);
        content.setPadding(add, add + top.getHeight() - addToBottomPx, add, add);
    }
    
    @Override
    public void applyInsets(Insets insets) {
        int add = SizeUtils.dipToPx(15, owner);
        top.setPadding(add, add + insets.top + addToBottomPx, add, add);
        applyInsets();
    }
}
