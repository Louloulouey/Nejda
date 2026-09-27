package com.sdidsa.nejda.view.app_content.session.home.ai.chat;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.components.layout.scroll.Scroller;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.Store;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.controll.ai.AiConversation;
import com.sdidsa.nejda.controll.ai.AiResponse;
import com.sdidsa.nejda.controll.ai.ChatPart;
import com.sdidsa.nejda.controll.ai.ChatRole;
import com.sdidsa.nejda.view.app_content.session.home.ai.AiFunctionButton;

import java.util.ArrayList;
import java.util.function.Consumer;

public class AiScroller extends StackPane {
    private final ArrayList<ChatPart> conversation = new ArrayList<>();
    private final ColoredLabel hint;
    private final Scroller scrollArea;
    private final VBox messages;
    private final AiInput bottom;

    private long id = -1;
    private String title;

    private Consumer<String> speak;

    public AiScroller(Context context, AiInput bottom, Consumer<String> speak) {
        super(context);
        this.speak = speak;
        this.bottom = bottom;
        hint = new ColoredLabel(owner, Style.TEXT_SEC, "What can I help with?");
        hint.setFont(new Font(24));
        hint.centerText();
        hint.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));

        scrollArea = new Scroller(owner);
        scrollArea.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));

        messages = new VBox(owner);
        messages.setPadding(15);
        messages.setSpacing(5);

        scrollArea.setContent(messages);

        addView(hint);
        addView(scrollArea);
    }

    public void loadAiResponse(AiResponse resp) {
        ArrayList<View> viewsToAdd = new ArrayList<>();
        resp.getTexts().forEach(txt -> {
            AiMessage msg = new AiMessage(owner, AiMessageRole.MODEL, txt, speak);
            msg.setAlpha(0);
            viewsToAdd.add(msg);
            messages.addView(msg);
            conversation.add(new ChatPart(ChatRole.MODEL, txt));
        });

        resp.getFunctions().forEach(func -> {
            AiFunctionButton butt = new AiFunctionButton(owner, func, resp.getArguments());
            butt.setAlpha(0);
            viewsToAdd.add(butt);
            messages.addView(butt);
        });

        Animation.sequenceFadeInUp(owner, viewsToAdd.toArray(new View[0]))
                .setDelay(-350)
                .setDuration(400)
                .setInterpolator(Interpolator.OVERSHOOT)
                .start();

        Platform.runAfter(() ->
                        scrollArea.smoothScrollTo(0, messages.getHeight()),
                50);

        saveCurrentConversation();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        saveCurrentConversation();
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChatId() {
        return id;
    }

    private AiMessage lastMessage = null;
    private ChatPart lastPart = null;

    public void beforeSend(String text) {
        if(messages.getChildCount() == 0) {
            removeView(hint);
        }
        lastMessage = new AiMessage(owner, AiMessageRole.USER, text, speak);
        lastMessage.setAlpha(0);
        lastMessage.getText().setAlpha(0.5f);
        messages.addView(lastMessage);
        Animation.fadeInUp(owner, lastMessage)
                .setDuration(400)
                .setInterpolator(Interpolator.OVERSHOOT)
                .start();
        scrollDown();
        lastPart = new ChatPart(ChatRole.USER, text);
        conversation.add(lastPart);
    }

    public void afterSend(boolean error) {
        bottom.stopLoading();

        if(error) {
            ContextUtils.toast(owner, "Something went wrong");

            Animation.fadeOutRight(owner, lastMessage)
                    .setDuration(400)
                    .setInterpolator(Interpolator.EASE_OUT)
                    .setOnFinished(() -> {
                        messages.removeView(lastMessage);
                        conversation.remove(lastPart);
                        bottom.setValue(lastPart.getContent());
                        saveCurrentConversation();
                    })
                    .start();
        } else {
            lastMessage.getText().setAlpha(1);
            saveCurrentConversation();
        }
    }

    public void newConversation() {
        saveCurrentConversation();
        title = null;
        id = -1;
        conversation.clear();
        messages.removeAllViews();
        addView(hint);
    }

    private void saveCurrentConversation() {
        id = Store.saveConversation(new AiConversation(id, title, conversation), null);
    }

    public void loadConversation(AiConversation conv) {
        title = conv.getTitle();
        id = conv.getId();
        conversation.clear();
        removeView(hint);
        conversation.addAll(conv.getChatParts());
        messages.removeAllViews();
        conv.getChatParts().forEach(part -> {
            AiMessage msg = new AiMessage(owner, part.getRole().getRole(), part.getContent(), speak);
            messages.addView(msg);
        });
        scrollDown();
    }

    public ArrayList<ChatPart> getConversation() {
        return conversation;
    }

    public void applyInsets() {
        int max = messages.getHeight() - scrollArea.getHeight();
        int current = scrollArea.getSv().getScrollY();

        if (current == max) {
            scrollDown();
        }
    }

    public void scrollDown() {
        Platform.runAfter(() -> {
            int nmax = messages.getHeight() - scrollArea.getHeight();
            scrollArea.smoothScrollTo(0, nmax);
        }, 50);
    }
}
