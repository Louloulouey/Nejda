package com.sdidsa.nejda.view.app_content.session.home.main;

import android.content.Context;

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
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.model.Alert;

import java.time.LocalDateTime;

public class RecentAlert extends RecyclerItemView<Alert> {
    private final VBox root;
    private final Label title, content;
    private DateLabel time;

    public RecentAlert(Context owner) {
        super(owner);
        root = new ColoredVBox(owner, Style.BACK_TER);
        root.setPadding(18);
        root.setCornerRadius(CornerUtils.DEFAULT);
        root.setSpacing(15);

        HBox top = new HBox(owner);

        title = new ColoredLabel(owner, Style.TEXT_NORM, "");
        title.setFont(new Font(18, FontWeight.MEDIUM));
        SpacerUtils.spacer(title, Orientation.HORIZONTAL);

        time = new ColoredDateLabel(owner, Style.TEXT_SEC);
        time.setFormat(DateFormat.RELATIVE);
        time.setFont(new Font(16));

        content = new ColoredLabel(owner, Style.TEXT_NORM, "");
        content.setFont(new Font(16));

        top.addViews(title, time);

        root.addViews(top, content);

        MarginUtils.setMarginVertical(this, owner, 7);

        addView(root);
    }

    @Override
    public void load(Alert item) {
        title.setKey(item.getTitle());
        content.setKey(item.getBody());
        time.setDate(item.getCreatedAt());
    }
}
