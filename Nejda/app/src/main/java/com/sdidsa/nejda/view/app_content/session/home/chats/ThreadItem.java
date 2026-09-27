package com.sdidsa.nejda.view.app_content.session.home.chats;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.text.ColoredDateLabel;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.DateFormat;
import com.sdidsa.nejda.abs.components.controls.text.DateLabel;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.abs.CornerUtils;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredVBox;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.components.layout.scroll.RecyclerItemView;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.TempStore;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.model.Message;
import com.sdidsa.nejda.model.Thread;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.R;

import java.util.ArrayList;
import java.util.List;

public class ThreadItem extends RecyclerItemView<Thread> {
    public static final int MAX_MESSAGES = 3;
    private final ColoredVBox root;
    private final Label title;
    private final DateLabel createdAt;
    private final VBox messagesContainer;

    private Thread thread;
    private List<Message> recentMessages = new ArrayList<>();
    private final String currentUserId;

    public ThreadItem(Context owner) {
        super(owner);
        currentUserId = TempStore.getUser().getUserId();
        
        root = new ColoredVBox(owner, Style.BACK_TER);
        root.setPadding(16);
        root.setCornerRadius(CornerUtils.DEFAULT);
        root.setSpacing(20);
        
        HBox top = new HBox(owner);
        
        title = new ColoredLabel(owner, Style.TEXT_NORM, "");
        title.setFont(new Font(18, FontWeight.MEDIUM));
        SpacerUtils.spacer(title, Orientation.HORIZONTAL);
        
        createdAt = new ColoredDateLabel(owner, Style.TEXT_SEC);
        createdAt.setFormat(DateFormat.RELATIVE);
        createdAt.setFont(new Font(16));
        
        top.addViews(title, createdAt);
        
        messagesContainer = new VBox(owner);
        messagesContainer.setAlpha(0.8f);
        messagesContainer.setSpacing(15);
        
        root.addViews(top, messagesContainer);
        
        MarginUtils.setMarginVertical(this, owner, 12);

        setOnClickListener(v -> {
            if (thread != null) {
                openChatPage(thread);
            }
        });

        MarginUtils.setMarginVertical(this, owner, 8);
        
        addView(root);
    }
    
    private void openChatPage(Thread thread) {
        if(thread != null) {
            TempStore.putThread(thread);
            ContextUtils.loadPage(owner, ChatPage.class);
        }
    }
    
    @Override
    public void load(Thread item) {
        this.thread = item;
        title.setKey(item.getTitle());

        messagesContainer.removeAllViews();
        
        if (item.getMessages() != null && !item.getMessages().isEmpty()) {
            recentMessages = item.getMessages();
            createdAt.setDate(recentMessages.get(0).getCreatedAt());

            for (int i = 0; i < Math.min(recentMessages.size(), MAX_MESSAGES); i++) {
                addMessageBubble(recentMessages.get(i));
            }

            addSeeMoreLabel();
        } else {
            createdAt.setDate(item.getCreatedAt());

            ColoredLabel noMessages = new ColoredLabel(owner, Style.TEXT_SEC, "No messages yet");
            noMessages.setFont(new Font(16));
            messagesContainer.addView(noMessages);
        }
    }

    private void addMessageBubble(Message message) {
        HBox container = new HBox(owner);
        
        ColoredVBox bubble = new ColoredVBox(owner, Style.BACK_PRI);
        PaddingUtils.setPaddingHorizontalVertical(bubble, 12, 8, owner);
        bubble.setCornerRadius(15);
        bubble.setSpacing(8);
        
        boolean isCurrentUser = message.getSender() != null && 
                message.getSender().getUserId().equals(currentUserId);

        if (isCurrentUser) {
            container.setGravity(Gravity.END);
            bubble.setFill(Style.ACCENT);
        } else {
            container.setGravity(Gravity.START);
            bubble.setFill(Style.BACK_PRI);
        }

        String txt = message.getText();
        ColoredLabel messageText = new ColoredLabel(owner, 
                isCurrentUser ? Style.ONACCENT : Style.TEXT_NORM, 
                txt.isBlank() ?
                "sent " + message.getAttachments().size() + " attachments" :
                txt);
        messageText.setFont(new Font(16));
        messageText.setMaxLines(2);
        messageText.setEllipsize(android.text.TextUtils.TruncateAt.END);

        if (!isCurrentUser && message.getSender() != null) {
            ColoredLabel senderName = new ColoredLabel(owner, Style.TEXT_SEC, "");
            senderName.setFont(new Font(14, FontWeight.MEDIUM));
            
            String name = "";
            if (message.getSender().getFirstName() != null && !message.getSender().getFirstName().isEmpty()) {
                name = message.getSender().getFirstName();
            } else {
                name = message.getSender().getUserId();
            }
            
            senderName.setKey(name);
            bubble.addView(senderName);
        }
        
        bubble.addView(messageText);
        container.addView(bubble);

        MarginUtils.setMarginHorizontal(bubble, owner, 0);
        
        messagesContainer.addView(container);
    }

    private void addSeeMoreLabel() {
        HBox labelContainer = new HBox(owner);
        labelContainer.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        labelContainer.setGravity(Gravity.CENTER);
        
        ColoredLabel seeMoreLabel = new ColoredLabel(owner, Style.ACCENT, "Open to see more...");
        seeMoreLabel.setFont(new Font(14, FontWeight.MEDIUM));
        SpacerUtils.spacer(seeMoreLabel, -2, -2);

        ColoredIcon arrowIcon = new ColoredIcon(owner, Style.ACCENT, R.drawable.arrow_right, 16);


        labelContainer.addViews(seeMoreLabel, arrowIcon);
        messagesContainer.addView(labelContainer);
    }

    public void setLastMessage(Message message) {
        if (recentMessages.size() >= MAX_MESSAGES) {
            recentMessages.remove(0);
        }
        recentMessages.add(message);

        messagesContainer.removeAllViews();
        
        for (int i = 0; i < recentMessages.size(); i++) {
            addMessageBubble(recentMessages.get(i));
        }

        addSeeMoreLabel();
    }
} 