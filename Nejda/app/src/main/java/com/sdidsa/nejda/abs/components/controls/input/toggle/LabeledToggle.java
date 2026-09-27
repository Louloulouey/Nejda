package com.sdidsa.nejda.abs.components.controls.input.toggle;

import android.content.Context;
import android.text.method.TransformationMethod;

import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class LabeledToggle extends HBox implements Styleable {
    private StyleToColor textFill;
    private final Toggle toggle;
    private final Label label;

    public LabeledToggle(Context owner) {
        this(owner, "Toggle Button text");
    }
    public LabeledToggle(Context owner, String text) {
        super(owner);
        setAlignment(Alignment.CENTER);
        setPadding(7);

        toggle = new Toggle(owner, 22);
        label = new Label(owner, text);
        label.setFont(new Font(18));

        setClipChildren(false);

        addView(label);
        addView(SpacerUtils.spacer(owner, Orientation.HORIZONTAL));
        addView(toggle);

        setOnClickListener(e -> toggle.toggle());

        setDefaultFocusHighlightEnabled(false);

        textFill = Style.TEXT_NORM;

        applyStyle(StyleUtils.getStyle(owner));
    }

    public void setFont(Font font) {
        label.setFont(font);
    }

    public void setEnabled(boolean checked) {
        toggle.setEnabled(checked);
    }

    public Toggle getToggle() {
        return toggle;
    }

    public Label getLabel() {
        return label;
    }

    public void setTextFill(StyleToColor textFill) {
        this.textFill = textFill;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    @Override
    public void applyStyle(Style style) {
        label.setFill(textFill.get(style));
    }

    public void setTransformationMethod(TransformationMethod method) {
        label.setTransformationMethod(method);
    }

    public boolean isEnabled() {
        return toggle.isEnabled();
    }
}
