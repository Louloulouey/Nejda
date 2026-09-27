package com.sdidsa.nejda.view.app_content.session.home.chats.display;

import android.content.Context;
import android.widget.FrameLayout;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.components.controls.image.ColorIcon;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredHBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;

public class ItemOverlayHeader extends ColoredHBox {
    private static final float AVATAR_SIZE = 48;
    private final Label title;
    private final ColorIcon info;
    private final ColorIcon save;
    private final ColorIcon close;
    public ItemOverlayHeader(Context owner) {
        super(owner);
        setAlignment(Alignment.CENTER);
        setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));

        title = new ColoredLabel(owner, Style.TEXT_NORM, "")
                .setFont(new Font(22, FontWeight.MEDIUM));
        title.setMaxLines(1);
        SpacerUtils.spacer(title);
        MarginUtils.setMarginHorizontal(title, owner, 7);

        close = new ColoredIcon(owner, Style.TEXT_NORM, R.drawable.close, 38)
                .setImagePadding(10);

        info = new ColoredIcon(owner, Style.TEXT_NORM, R.drawable.info, 40)
                .setImagePadding(7);

        save = new ColoredIcon(owner, Style.TEXT_NORM, R.drawable.save, 40)
                .setImagePadding(8);

        MarginUtils.setMarginLeft(close, owner, 7);
        MarginUtils.setMarginLeft(info, owner, 7);

        addViews(title, save, info, close);
    }

    public void showInfo(boolean show) {
        info.setVisibility(show ? VISIBLE : GONE);
    }

    public void setTitleText(String text) {
        title.setText(text);
    }

    public void setTitle(String key) {
        title.setKey(key);
    }

    public void setTitle(String key, String...params) {
        title.setKey(key, params);
    }

    public void hideSave() {
        save.setVisibility(GONE);
    }

    public void hideInfo() {
        info.setVisibility(GONE);
    }

    public void setOnClose(Runnable action) {
        close.setOnClick(action);
    }

    public void setOnInfo(Runnable action) {
        info.setOnClick(action);
    }

    public void setOnSave(Runnable action) {
        save.setOnClick(action);
    }
}
