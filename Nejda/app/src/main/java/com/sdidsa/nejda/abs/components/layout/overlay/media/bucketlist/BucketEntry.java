package com.sdidsa.nejda.abs.components.layout.overlay.media.bucketlist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.App;
import com.sdidsa.nejda.abs.components.controls.image.Image;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.data.media.Bucket;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class BucketEntry extends HBox implements Styleable {
    private final Label name;
    private final Label count;
    private final Image thumb;

    public BucketEntry(Context owner) {
        super(owner);
        PaddingUtils.setPaddingHorizontalVertical(this, 20, 10, owner);
        setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER);

        setFocusable(true);
        setClickable(true);

        VBox text = new VBox(owner);
        text.setAlignment(Alignment.CENTER_LEFT);
        text.setSpacing(6);

        name = new Label(owner, "");
        name.setFont(new Font(16));
        name.setMaxLines(1);
        count = new Label(owner, "");
        count.setFont(new Font(14));

        text.addView(name);
        text.addView(count);

        thumb = new Image(owner);
        thumb.setSize(64);
        thumb.setCornerRadius(7);
        thumb.setFocusable(false);
        thumb.setClickable(false);

        SpacerUtils.spacer(text, Orientation.HORIZONTAL);
        addView(text);
        addView(thumb);

        setBackground(new GradientDrawable());

        applyStyle(StyleUtils.getStyle(owner));
    }

    public void load(Bucket data) {
        name.setText(data.getName());
        count.setText(String.valueOf(data.getItems().size()).concat(" items"));

        if (!data.getItems().isEmpty())
            data.getItems().get(0)
                    .getThumbnail(
                            owner,
                            SizeUtils.dipToPx(64, owner),
                            thumb::setImageBitmap,
                            () -> thumb.setImageResource(R.drawable.problem)
                    );
    }

    @Override
    public void applyStyle(Style style) {
        name.setTextColor(style.getTextNormal());
        count.setTextColor(style.getTextMuted());

        setOnTouchListener((view, action) -> {
            switch (action.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setBackgroundColor(App.adjustAlpha(style.getTextSecondary(), .2f));
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                case MotionEvent.ACTION_CANCEL:
                    setBackgroundColor(Color.TRANSPARENT);
                    break;
            }
            return true;
        });
    }

}