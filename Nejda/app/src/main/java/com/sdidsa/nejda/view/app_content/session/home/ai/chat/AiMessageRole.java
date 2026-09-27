package com.sdidsa.nejda.view.app_content.session.home.ai.chat;

import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.StyleToColor;

public enum AiMessageRole {
    USER(Style.ACCENT, Style.ONACCENT, 50, 0, true),
    MODEL(Style.BACK_TER, Style.TEXT_NORM, 0, 50, false);

    private final StyleToColor backFill;
    private final StyleToColor textFill;
    private final float marginLeft;
    private final float marginRight;

    private final boolean sent;

    AiMessageRole(StyleToColor backFill, StyleToColor textFill, float marginLeft, float marginRight, boolean sent) {
        this.backFill = backFill;
        this.textFill = textFill;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.sent = sent;
    }

    public StyleToColor getBackFill() {
        return backFill;
    }

    public StyleToColor getTextFill() {
        return textFill;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public boolean isSent() {
        return sent;
    }
}
