package com.sdidsa.nejda.view.app_content.session.home.main;

import android.content.Context;

import androidx.annotation.DrawableRes;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.abs.CornerUtils;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredHBox;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredVBox;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.controll.AlertApi;
import com.sdidsa.nejda.model.Alert;
import com.sdidsa.nejda.view.app_content.session.report.Severity;

import java.util.Objects;

public class MainStatus extends ColoredVBox {
    private final ColoredIcon statusIcon;
    private final ColoredLabel statusLab;
    private final ColoredLabel alertTitle;

    private final HBox top;

    public MainStatus(Context owner) {
        super(owner, Style.EMPTY);
        setSpacing(15);
        top = new ColoredHBox(owner, Style.BACK_TER);
        top.setAlignment(Alignment.TOP_LEFT);

        top.setCornerRadius(CornerUtils.DEFAULT);
        top.setPadding(18);
        top.setLayoutParams(new LayoutParams(-1, -2));

        statusIcon = new ColoredIcon(owner, Style.TEXT_SEC, R.drawable.check_fill, 24);
        statusLab = new ColoredLabel(owner, Style.TEXT_SEC, "Checking status...");
        statusLab.setFont(new Font(18));


        alertTitle = new ColoredLabel(owner, Style.TEXT_SEC, "Checking status...");
        alertTitle.setFont(new Font(18, FontWeight.MEDIUM));

        alertTitle.setCornerRadius(CornerUtils.DEFAULT);
        alertTitle.setPadding(18);
        alertTitle.setLayoutParams(new LayoutParams(-1, -2));
        alertTitle.setBack(Style.BACK_TER);



        SpacerUtils.spacer(statusLab, -2, -2);

        MarginUtils.setMarginLeft(statusLab, owner, 15);

        top.addViews(statusIcon, statusLab);


        addViews(
                //statusHead,
                top);
    }

    public void setStatus(Alert alert) {
        setStatus(R.drawable.danger, "Active emergency alert", Style.TEXT_ERR);
        alertTitle.setText(alert.getTitle());
        alertTitle.setFill(Objects.requireNonNull(
                Severity.bySeverity(alert.getSeverity())).getColor());
        addView(alertTitle);
    }

    public void setStatus(@DrawableRes int icon, String text, StyleToColor color) {
        statusLab.setKey(text);
        statusIcon.setImageResource(icon);

        statusLab.setFill(color);
        statusIcon.setFill(color);
    }

    public void fetch() {
        removeView(alertTitle);
        setStatus(R.drawable.check_fill, "Checking status...", Style.TEXT_SEC);
        AlertApi.getActive(alert -> {
            if(alert == null) {
                setStatus(R.drawable.check_fill, "No active emergencies", Style.TEXT_POS);
            } else {
                setStatus(alert);
            }
        });
    }
}
