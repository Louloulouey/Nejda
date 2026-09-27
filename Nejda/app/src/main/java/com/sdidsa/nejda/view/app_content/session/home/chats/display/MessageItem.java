package com.sdidsa.nejda.view.app_content.session.home.chats.display;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import com.sdidsa.nejda.abs.components.controls.text.ColoredDateLabel;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.DateFormat;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.controls.image.NetImage;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredVBox;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.components.layout.scroll.RecyclerItemView;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.Store;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.model.Message;
import com.sdidsa.nejda.model.MessageAttachment;
import com.sdidsa.nejda.view.app_content.session.profile.ViewProfileOverlay;

import java.util.List;

public class MessageItem extends RecyclerItemView<Message> {
    public static final float ATTACHMENT_SIZE = 100;
    private final ColoredVBox bubble;
    private final ColoredLabel messageText;
    private final ColoredLabel senderName;
    private final ColoredDateLabel timestamp;
    private final VBox container;
    private final VBox attachmentsContainer;

    private final NetImage pfp;

    private final String currentUserId;
    private Message message;

    private final HBox preContainer;

    private final ColoredLabel initials;

    public MessageItem(Context owner) {
        super(owner);
        currentUserId = Store.getUserId();
        
        container = new VBox(owner);
        container.setSpacing(7);
        container.setLayoutParams(new LayoutParams(-1, -2));
        
        bubble = new ColoredVBox(owner, Style.BACK_TER);
        PaddingUtils.setPaddingHorizontalVertical(bubble, 15, 10, owner);
        bubble.setCornerRadius(20);
        bubble.setSpacing(5);
        bubble.setLayoutParams(new LayoutParams(-2, -2));
        
        senderName = new ColoredLabel(owner, Style.TEXT_SEC, "");
        senderName.setFont(new Font(14, FontWeight.MEDIUM));
        
        messageText = new ColoredLabel(owner, Style.TEXT_NORM, "");
        messageText.setFont(new Font(16));

        attachmentsContainer = new VBox(owner);
        attachmentsContainer.setSpacing(8);
        
        timestamp = new ColoredDateLabel(owner, Style.TEXT_SEC);
        timestamp.setFormat(DateFormat.RELATIVE);
        timestamp.setFont(new Font(12));
        
        bubble.addView(messageText);

        MarginUtils.setMarginUnified(this, owner, 7);
        MarginUtils.setMarginHorizontal(senderName, owner, 5);
        MarginUtils.setMarginHorizontal(timestamp, owner, 10);

        pfp = new NetImage(owner, Style.BACK_TER);
        pfp.setSize(44);
        pfp.setCornerRadius(44);

        initials = new ColoredLabel(owner, Style.ACCENT, "");
        initials.setFont(new Font(22, FontWeight.MEDIUM));

        container.addView(senderName);
        container.addView(bubble);
        container.addView(attachmentsContainer);
        container.addView(timestamp);

        preContainer = new HBox(owner);
        preContainer.addView(container);

        addView(preContainer);
    }
    
    @Override
    public void load(Message item) {
        this.message = item;
        boolean isCurrentUser = item.getSender().getUserId().equals(currentUserId);
        if(item.getId() == null) {
            setAlpha(0.4f);
        } else {
            setAlpha(1);
        }

        pfp.setOnClick(() -> {
            ViewProfileOverlay.getInstance(owner).show(item.getSender());
        });

        pfp.setImageBitmap(null);
        if(item.getSender() != null && item.getSender().getPfp() != null
                && !item.getSender().getPfp().equalsIgnoreCase("null")) {
            pfp.setImageThumbUrl(item.getSender().getPfp(), 48);
            pfp.removeView(initials);
        } else {
            pfp.addCentered(initials);
            initials.setText(item.getSender().getFirstName().substring(0, 1));
        }

        preContainer.removeView(pfp);
        preContainer.addView(pfp, isCurrentUser ? 1 : 0);
        
        messageText.setKey(item.getText());

        attachmentsContainer.removeAllViews();
        attachmentsContainer.setAlignment(
                isCurrentUser ? Alignment.CENTER_RIGHT : Alignment.CENTER_LEFT
        );
        if(isCurrentUser) {
            MarginUtils.setMarginLeft(pfp, owner, 10);
        } else {
            MarginUtils.setMarginRight(pfp, owner, 10);
        }
        List<MessageAttachment> attachments = item.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            HBox line = new HBox(owner);
            line.setAlignment(
                    isCurrentUser ? Alignment.CENTER_RIGHT : Alignment.CENTER_LEFT
            );
            attachmentsContainer.addView(line);
            for (MessageAttachment attachment : attachments) {
                if(line.getChildCount() >= 3) {
                    line = new HBox(owner);
                    line.setAlignment(
                            isCurrentUser ? Alignment.CENTER_RIGHT : Alignment.CENTER_LEFT
                    );
                    attachmentsContainer.addView(line);
                }
                View view = addAttachmentView(item, attachment, line);
                if(view != null && line.getChildCount() > 1) {
                    MarginUtils.setMarginLeft(view, owner, 8);
                }
            }
            attachmentsContainer.setVisibility(View.VISIBLE);
        } else {
            attachmentsContainer.setVisibility(View.GONE);
        }
        
        if ((item.getText() == null || item.getText().isEmpty()) &&
            attachments != null && !attachments.isEmpty()) {
            bubble.setVisibility(View.GONE);
        } else {
            bubble.setVisibility(View.VISIBLE);
        }
        
        if (item.getSender() != null) {
            if (isCurrentUser) {
                container.setGravity(Gravity.END);
                bubble.setFill(Style.ACCENT);
                messageText.setFill(Style.ONACCENT);
                senderName.setVisibility(GONE);
            } else {
                container.setGravity(Gravity.START);
                bubble.setFill(Style.BACK_TER);
                messageText.setFill(Style.TEXT_NORM);
                
                String name;
                if (item.getSender().getFirstName() != null && !item.getSender().getFirstName().isEmpty()) {
                    name = item.getSender().getFirstName();
                } else {
                    name = item.getSender().getUserId();
                }
                
                senderName.setKey(name);
                senderName.setVisibility(VISIBLE);
            }
        } else {
            container.setGravity(Gravity.CENTER);
            bubble.setFill(Style.BACK_TER);
            messageText.setFill(Style.TEXT_SEC);
            timestamp.setFill(Style.TEXT_SEC);
            senderName.setVisibility(GONE);
        }
        
        if (item.getCreatedAt() != null) {
            timestamp.setDate(item.getCreatedAt());
            timestamp.setVisibility(VISIBLE);
        } else {
            timestamp.setVisibility(GONE);
        }
    }
    
    private View addAttachmentView(Message message, MessageAttachment attachment, HBox parent) {
        if (attachment == null || attachment.getUrl() == null) {
            return null;
        }
        
        String type = attachment.getType();
        if (type == null) {
            type = "image";
        }

        return switch (type.toLowerCase()) {
            case "image" -> addImageAttachment(message, attachment, parent);
            case "audio" -> addAudioAttachment(attachment, parent);
            default -> addGenericAttachment(attachment, parent);
        };
    }
    
    private View addImageAttachment(Message message, MessageAttachment attachment, HBox parent) {
        NetImage imageView = new NetImage(getContext());
        imageView.setCornerRadius(10);
        imageView.setSize(ATTACHMENT_SIZE, ATTACHMENT_SIZE);
        imageView.setImageThumbUrl(attachment.getUrl(), ATTACHMENT_SIZE);

        imageView.setAlpha(0.5f);

        imageView.setOnClick(() -> {
            ImageOverlay.getInstance(owner).show(message, attachment.getUrl());
        });

        parent.addView(imageView);
        return imageView;
    }
    
    private View addAudioAttachment(MessageAttachment attachment, HBox parent) {
        AudioAttachmentView audioView = new AudioAttachmentView(getContext(), attachment.getUrl());
        parent.addView(audioView);
        return audioView;
    }
    
    private View addGenericAttachment(MessageAttachment attachment, HBox parent) {
        ColoredLabel attachmentLabel = new ColoredLabel(getContext(), Style.TEXT_NORM, 
                "Attachment: " + attachment.getUrl().substring(attachment.getUrl().lastIndexOf('/') + 1));
        attachmentLabel.setFont(new Font(14));

        parent.addView(attachmentLabel);
        return attachmentLabel;
    }
} 