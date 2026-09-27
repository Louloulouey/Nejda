package com.sdidsa.nejda.abs.components.controls.input.radio;

import android.content.Context;
import android.text.method.TransformationMethod;
import android.view.Gravity;

import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class LabeledRadio extends HBox implements Styleable {

    private final Radio radio;
    private final Label label;

    private StyleToColor fill = Style.TEXT_SEC;

    public LabeledRadio(Context owner) {
        this(owner, "Radio Button text");
    }
    public LabeledRadio(Context owner, String text) {
        super(owner);
        setGravity(Gravity.CENTER);
        setPadding(7);

        radio = new Radio(owner, 22);
        label = new Label(owner, text);
        label.setFont(new Font(18));


        addView(label);
        addView(SpacerUtils.spacer(owner, Orientation.HORIZONTAL));
        addView(radio);

        setOnClickListener(e -> setChecked(true));

        setDefaultFocusHighlightEnabled(false);

        applyStyle(StyleUtils.getStyle(owner));
    }

    public void setFont(Font font) {
        label.setFont(font);
    }

    public void setChecked(boolean checked) {
        radio.setChecked(checked);
    }

    public Radio getRadio() {
        return radio;
    }

    public Label getLabel() {
        return label;
    }

    public void setFill(StyleToColor fill) {
        this.fill = fill;
        radio.setFill(fill);
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    @Override
    public void applyStyle(Style style) {
        label.setFill(fill.get(style));
    }

    public void setTransformationMethod(TransformationMethod method) {
        label.setTransformationMethod(method);
    }

    public boolean isChecked() {
        return radio.isChecked();
    }
}
