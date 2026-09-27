package com.sdidsa.nejda.view.app_content.session.home.ai;

import android.content.Context;

import com.sdidsa.nejda.abs.components.controls.button.ColoredButton;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.controll.ai.AiFunction;
import com.sdidsa.nejda.view.app_content.session.home.ai.chat.AiMessageRole;

import org.json.JSONObject;

public class AiFunctionButton extends ColoredButton {
    public AiFunctionButton(Context context, AiFunction function, JSONObject args) {
        super(context, Style.BACK_TER, Style.TEXT_NORM, function.getDisplayName());
        setCornerRadius(20);
        setPadding(10);
        setFont(new Font(18, FontWeight.MEDIUM));
        MarginUtils.setMarginRight(this, context, AiMessageRole.MODEL.getMarginRight());

        setElevation(0);

        setOnClick(() -> function.getAction().accept(context, args));
    }
}
