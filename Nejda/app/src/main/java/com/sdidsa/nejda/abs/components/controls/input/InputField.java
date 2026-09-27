package com.sdidsa.nejda.abs.components.controls.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.method.KeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;

import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.FontSizeAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateYAnimation;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.locale.Locale;
import com.sdidsa.nejda.abs.locale.Localized;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.utils.view.LocaleUtils;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.data.observable.Observable;
import com.sdidsa.nejda.abs.data.property.Property;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class InputField extends StackPane implements Styleable, Localized {
    protected final EditText input;
    private final GradientDrawable background;
    private final Property<String> value;

    private final StackPane prompts;
    private final Label prompt, error;
    private final ParallelAnimation focus, unfocus;

    private final HBox preInput;

    private StyleToColor backColor = Style.BACK_SEC;

    private StyleToColor promptColor = Style.TEXT_SEC;

    private final KeyListener keyListener;

    private final String key;
    public InputField(Context owner) {
        this(owner, "Prompt text");
    }

    public InputField(Context owner, String promptText) {
        this(owner, promptText, promptText.toLowerCase());
    }

    public InputField(Context owner, String promptText, String key) {
        super(owner);
        this.key = key;
        background = new GradientDrawable();
        setRadius(12);

        value = new Property<>("");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        prompt = new Label(owner, promptText);
        prompt.setMaxLines(1);
        prompt.setLines(1);

        error = new Label(owner, "");
        error.setMaxLines(1);
        error.setLines(1);

        input = new EditText(owner);
        keyListener = input.getKeyListener();
        input.setLayoutParams(params);
        input.setTypeface(Font.DEFAULT.getFont());
        input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        input.setShowSoftInputOnFocus(true);
        PaddingUtils.setPadding(input, 0, 30, 0, 10, owner);
        input.setBackground(null);
        input.setMaxLines(1);
        input.setLines(1);
        input.setSingleLine(true);
        input.setId(View.generateViewId());
        input.setFocusable(true);

        preInput = new HBox(owner);
        preInput.setAlignment(Alignment.CENTER_LEFT);
        PaddingUtils.setPadding(preInput, 16, 0, 4, 0, owner);
        preInput.addView(input);

        prompts = new StackPane(owner);
        prompts.setClickable(false);
        prompts.setFocusable(false);
        PaddingUtils.setPadding(prompts, 15, 20, 15, 20, owner);

        prompt.setLabelFor(input.getId());

        prompts.addView(prompt);
        prompts.addView(error);

        addView(preInput);
        addView(prompts);

        focus = new ParallelAnimation(200)
                .addAnimation(new FontSizeAnimation(prompt, 12)
                        .setLateTo(() -> font.getSize() * .8f))
                .addAnimation(new FontSizeAnimation(error, 12)
                        .setLateTo(() -> font.getSize() * .8f))
                .addAnimation(new TranslateYAnimation(prompts,
                        -SizeUtils.dipToPx(12, owner)))
                .setInterpolator(Interpolator.EASE_OUT);

        unfocus = new ParallelAnimation(200)
                .addAnimation(new FontSizeAnimation(prompt, 16)
                        .setLateTo(() -> font.getSize()))
                .addAnimation(new FontSizeAnimation(error, 16)
                        .setLateTo(() -> font.getSize()))
                .addAnimation(new TranslateYAnimation(prompts, 0))
                .setInterpolator(Interpolator.EASE_OUT);

        input.setOnFocusChangeListener((view, focused) -> {
            if (!getValue().isEmpty())
                return;

            if (focused) {
                unfocus.stop();
                focus.start();
            } else {
                focus.stop();
                unfocus.start();
            }
        });

        valueProperty().addListener((ov, nv) -> {
            if (ov != null && ov.isEmpty() && !nv.isEmpty()) {
                unfocus.stop();
                focus.start();
            }

            if(ov != null && !ov.isEmpty() && nv.isEmpty() && !isFocused()) {
                focus.stop();
                unfocus.start();
            }

            hideError();
        });

        InputUtils.bindToProperty(input, value);

        setFont(new Font(20));

        setBackground(background);
        applyStyle(StyleUtils.getStyle(owner));
        applyLocale(LocaleUtils.getLocale(owner));
    }

    public StackPane getPrompts() {
        return prompts;
    }

    public void setEditable(boolean editable) {
        preInput.setFocusable(editable);
        preInput.setFocusableInTouchMode(editable);
        preInput.setClickable(editable);

        prompts.setFocusable(editable);
        prompts.setFocusableInTouchMode(editable);
        prompts.setClickable(editable);

        prompt.setFocusable(editable);
        prompt.setFocusableInTouchMode(editable);
        prompt.setClickable(editable);

        error.setFocusable(editable);
        error.setFocusableInTouchMode(editable);
        error.setClickable(editable);

        input.setFocusable(editable);
        input.setFocusableInTouchMode(editable);
        input.setClickable(editable);
        input.setKeyListener(editable ? keyListener : null);
    }

    public EditText getInput() {
        return input;
    }

    public String getKey() {
        return key;
    }

    public void setHidden(boolean hidden) {
        input.setTransformationMethod(hidden ? new PasswordTransformationMethod() : null);
    }

    public void setError(String text) {
        if(hidingError != null && hidingError.isRunning()) {
            hidingError.stop();
            error.setKey("");
        }
        if(error.getKey().isEmpty()) {
            error.setLabelFor(input.getId());
            Animation.fadeInUp(owner, error)
                    .setDuration(250)
                    .setInterpolator(Interpolator.EASE_OUT).start();
            Animation.fadeOutUp(owner, prompt)
                    .setDuration(250)
                    .setInterpolator(Interpolator.EASE_OUT).start();
        }
        Platform.runLater(() -> error.setKey(text));
    }

    private Animation hidingError;
    public void hideError() {
        prompt.setLabelFor(input.getId());
        if((hidingError != null && hidingError.isRunning()) || error.getKey().isEmpty()) {
            return;
        }
        Animation.fadeInDown(owner, prompt)
                .setDuration(250)
                .setInterpolator(Interpolator.EASE_OUT).start();
        hidingError = Animation.fadeOutDown(owner, error)
                .setOnFinished(() -> error.setKey(""))
                .setDuration(250)
                .setInterpolator(Interpolator.EASE_OUT);
        hidingError.start();
    }

    public boolean isEmpty() {
        return getValue().isEmpty();
    }

    public void addPostInput(View view) {
        preInput.addView(view);
    }

    public ColoredIcon addPostIcon(@DrawableRes int iconRes, Runnable onClick) {
        ColoredIcon icon = new ColoredIcon(owner, Style.TEXT_SEC, iconRes, 48)
                .setImagePadding(12);
        addPostInput(icon);
        if(onClick != null) {
            icon.setOnClick(onClick);
        }
        return icon;
    }

    public void addPostIcon(@DrawableRes int iconRes) {
        addPostIcon(iconRes, null);
    }

    private Font font;
    public void setFont(Font font) {
        this.font = font;
        input.setTypeface(font.getFont());
        prompt.setTypeface(font.getFont());
        error.setTypeface(font.getFont());
        input.setTextSize(TypedValue.COMPLEX_UNIT_SP, font.getSize());
        prompt.setTextSize(TypedValue.COMPLEX_UNIT_SP, font.getSize());
        error.setTextSize(TypedValue.COMPLEX_UNIT_SP, font.getSize());
    }

    public Observable<String> valueProperty() {
        return value;
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        if(value.equals(getValue())) {
            return;
        }
        if (!isFocused()) {
            unfocus.stop();
            focus.start();
        }
        input.setText(value);
        input.setSelection(input.getText().length());
    }

    @Override
    public boolean isFocused() {
        return input.isFocused();
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

    public void setBackColor(StyleToColor backColor) {
        this.backColor = backColor;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    public void setPromptColor(StyleToColor promptColor) {
        this.promptColor = promptColor;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    @Override
    public void applyStyle(Style style) {
        setBackgroundColor(backColor.get(style));

        input.setTextColor(style.getTextNormal());
        prompt.setTextColor(promptColor.get(style));
        error.setTextColor(style.getTextError());
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void applyLocale(Locale locale) {
        input.setGravity(Gravity.CENTER_VERTICAL | (locale.isRtl() ? Gravity.RIGHT : Gravity.LEFT));
    }

}
