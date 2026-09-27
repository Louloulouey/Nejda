package com.sdidsa.nejda.abs.components.controls.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.sdidsa.nejda.abs.App;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.locale.Locale;
import com.sdidsa.nejda.abs.locale.Localized;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.data.observable.Observable;
import com.sdidsa.nejda.abs.data.property.Property;
import com.sdidsa.nejda.abs.utils.view.LocaleUtils;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class MinimalInputField extends StackPane implements Styleable, Localized {
    protected final EditText input;
    private final GradientDrawable background;
    private final Property<String> value;
    private final HBox preInput;

    private final String promptKey;

    private StyleToColor backFill = Style.BACK_SEC;
    private StyleToColor textFill = Style.TEXT_NORM;

    public MinimalInputField(Context owner) {
        this(owner,"Input Prompt");
    }

    public MinimalInputField(Context owner, String promptKey) {
        super(owner);
        background = new GradientDrawable();
        this.promptKey = promptKey;
        setRadius(12);

        value = new Property<>("");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        setFocusable(true);

        input = new EditText(owner);
        input.setLayoutParams(params);
        input.setTypeface(Font.DEFAULT.getFont());
        input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        input.setShowSoftInputOnFocus(true);
        input.setBackground(null);
        input.setLines(1);
        input.setSingleLine(true);
        input.setEnabled(true);
        input.setPadding(0,0,0,0);

        preInput = new HBox(owner);
        preInput.setAlignment(Alignment.CENTER_LEFT);
        preInput.addView(input);
        PaddingUtils.setPadding(preInput, 15, 5, 4, 4, owner);

        preInput.setFocusable(true);
        preInput.setOnClickListener(e -> {
            input.requestFocus();
            ContextUtils.showKeyboard(owner, input);
        });

        addView(preInput);

        InputUtils.bindToProperty(input, value);

        setBackground(background);

        applyStyle(StyleUtils.getStyle(owner));
        applyLocale(LocaleUtils.getLocale(owner));
    }

    public void setLineSpacing(float spacing) {
        input.setLineSpacing(SizeUtils.dipToPx(spacing, owner), 1);
    }

    public EditText getInput() {
        return input;
    }

    public HBox getPreInput() {
        return preInput;
    }

    public void setMultiline(int lines) {
        int max = lines == -1 ? Integer.MAX_VALUE : lines;
        input.setMaxLines(max);
        input.setSingleLine(lines == 1);
    }

    public void addPostInput(View view) {
        preInput.addView(view);
    }

    public void setFont(Font font) {
        input.setTypeface(font.getFont());
        input.setTextSize(TypedValue.COMPLEX_UNIT_SP, font.getSize());
    }

    public Observable<String> valueProperty() {
        return value;
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        input.setText(value);
        input.setSelection(input.getText().length());
    }

    public void setRadius(float radius) {
        background.setCornerRadius(SizeUtils.dipToPx(radius, owner));
    }

    public void setBackgroundColor(int color) {
        background.setColor(color);
    }

    @Override
    public void setBackground(int color) {
        setBackgroundColor(color);
    }

    public void setBackFill(StyleToColor backFill) {
        this.backFill = backFill;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    public void setTextFill(StyleToColor textFill) {
        this.textFill = textFill;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    @Override
    public void applyStyle(Style style) {
        setBackgroundColor(backFill.get(style));
        int tf = textFill.get(style);
        input.setTextColor(tf);
        input.setHintTextColor(App.adjustAlpha(tf, .5f));
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void applyLocale(Locale locale) {
        input.setHint(locale.get(promptKey));

        input.setGravity(Gravity.CENTER_VERTICAL | (locale.isRtl() ? Gravity.RIGHT : Gravity.LEFT));
    }

    private int type;
    public void setEditable(boolean b) {
        if(!b) type = input.getInputType();
        input.setInputType(b ? type : InputType.TYPE_NULL);
    }
}
