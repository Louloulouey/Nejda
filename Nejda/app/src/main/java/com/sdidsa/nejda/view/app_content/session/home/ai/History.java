package com.sdidsa.nejda.view.app_content.session.home.ai;

import android.content.Context;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.components.controls.button.Button;
import com.sdidsa.nejda.abs.components.controls.button.ColoredButton;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.abs.CornerUtils;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.components.layout.overlay.PartialSlideOverlay;
import com.sdidsa.nejda.abs.components.layout.scroll.Scroller;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.Store;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.controll.ai.AiConversation;

import java.util.List;
import java.util.function.Consumer;

public class History extends PartialSlideOverlay {
    private Consumer<AiConversation> onSelect;
    private Runnable newChat;

    private final VBox root;

    public History(Context owner) {
        super(owner, 0.5f);

        list.setFill(Style.EMPTY);
        list.setSpacing(10);

        root = new VBox(owner);
        root.setSpacing(15);
        root.setPadding(15);

        Scroller scroller = new Scroller(owner);
        scroller.setContent(root);
        scroller.setFill(Style.BACK_PRI);
        scroller.setCornerRadius(CornerUtils.DEFAULT);
        scroller.setClipToOutline(true);

        Button newConv = new ColoredButton(owner, Style.BACK_PRI, Style.TEXT_NORM, "New Chat");
        newConv.setFont(new Font(18, FontWeight.MEDIUM));
        newConv.setCornerRadius(CornerUtils.DEFAULT);
        newConv.setOnClick(() -> {
            if(newChat != null) newChat.run();
        });
        addAligned(newConv, Alignment.TOP_CENTER);

        list.addViews(newConv, scroller);
    }

    public void setNewChat(Runnable newChat) {
        this.newChat = newChat;
    }

    public void setOnSelect(Consumer<AiConversation> onSelect) {
        this.onSelect = onSelect;
    }

    public void addConversation(AiConversation conv, long chatId) {
        ColoredButton entry = new ColoredButton(owner, Style.BACK_PRI, Style.TEXT_NORM,
                conv.getTitle());
        entry.setFont(new Font(18));
        entry.setElevation(0);
        entry.setPadding(10);
        entry.setTextAlignment(TEXT_ALIGNMENT_TEXT_START);
        entry.addPostLabel(SpacerUtils.spacer(owner, Orientation.HORIZONTAL));
        SpacerUtils.spacer(entry, -2, SizeUtils.dipToPx(48, owner));

        entry.setOnClick(() -> {
            hide();
            if(onSelect != null) onSelect.accept(conv);
        });

        ColoredIcon delete = new ColoredIcon(owner, Style.TEXT_NORM, Style.BACK_PRI,
                R.drawable.delete, 48);
        delete.setPadding(13);
        delete.setCornerRadius(12);
        MarginUtils.setMarginLeft(delete, owner, 5);

        HBox line = new HBox(owner);
        line.addView(entry);

        if(conv.getId() != chatId) {
            line.addView(delete);
        } else {
            entry.setFill(Style.BACK_SEC);
        }

        delete.setOnClick(() -> {
            root.removeView(line);
            Store.deleteConversation(conv);
        });

        root.addView(line);
    }

    public void setHistory(List<AiConversation> history, long chatId) {
        root.removeAllViews();
        for(AiConversation conv : history) {
            addConversation(conv, chatId);
        }
    }
}
