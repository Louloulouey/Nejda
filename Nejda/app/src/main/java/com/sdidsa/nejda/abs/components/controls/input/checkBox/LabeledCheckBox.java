package com.sdidsa.nejda.abs.components.controls.input.checkBox;

import android.content.Context;
import android.view.Gravity;

import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.data.property.Property;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class LabeledCheckBox extends HBox implements Styleable {

    private final CheckBox checkBox;
    private final Label label;

    public LabeledCheckBox(Context owner) {
        this(owner, "CheckBox text");
    }

    public LabeledCheckBox(Context owner, String text) {
        super(owner);
        setGravity(Gravity.CENTER);
        setPadding(7);

        checkBox = new CheckBox(owner);
        label = new Label(owner, text);
        label.setFont(new Font(18));


        addView(checkBox);
        addView(label);

        setOnClickListener(e -> setChecked(!isChecked()));

        setDefaultFocusHighlightEnabled(false);

        applyStyle(StyleUtils.getStyle(owner));
    }

    @Override
    public void setSpacing(float spacing) {
        MarginUtils.setMarginRight(checkBox, owner, spacing);
    }

    public void setFont(Font font) {
        label.setFont(font);
    }

    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }

    public Property<Boolean> checkedProperty() {
        return checkBox.checkedProperty();
    }

    public void setCheckSize(float size) {
        checkBox.setSize(size);
    }

    public void setLineSpacing(float lineSpacing) {
        label.setLineSpacing(lineSpacing);
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public void applyStyle(Style style) {
        setOnFocusChangeListener((v, focused) ->
                label.setFill(
                        focused ?
                        style.getTextNormal() :
                        style.getTextSecondary()));
    }

}
