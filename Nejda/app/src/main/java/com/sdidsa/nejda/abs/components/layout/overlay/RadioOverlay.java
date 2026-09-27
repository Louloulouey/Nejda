package com.sdidsa.nejda.abs.components.layout.overlay;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;

import com.sdidsa.nejda.abs.components.controls.button.Button;
import com.sdidsa.nejda.abs.components.controls.button.ColoredButton;
import com.sdidsa.nejda.abs.components.controls.input.radio.LabeledRadio;
import com.sdidsa.nejda.abs.components.controls.input.radio.Radio;
import com.sdidsa.nejda.abs.components.controls.input.radio.RadioGroup;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.controls.text.transformationMethods.Capitalize;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.data.ConcurrentArrayList;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

import java.util.function.Consumer;

public class RadioOverlay extends PartialSlideOverlay implements Styleable {
    protected final VBox root;
    private final Label text;
    private final ConcurrentArrayList<LabeledRadio> buttons;
    private final RadioGroup group;

    private Consumer<String> onSave;

    private final Button save;

    public RadioOverlay(Context owner) {
        this(owner, "Header text");
    }

    public RadioOverlay(Context owner, String header) {
        super(owner, -2);

        list.setPadding(20);
        list.setSpacing(30);
        list.setGravity(Gravity.TOP | Gravity.CENTER);

        text = new Label(owner, header);
        text.centerText();
        text.setFont(new Font(20, FontWeight.MEDIUM));
        text.setLineSpacing(10);

        buttons = new ConcurrentArrayList<>();
        group = new RadioGroup();

        root = new VBox(owner);
        root.setSpacing(10);
        root.setGravity(Gravity.TOP | Gravity.CENTER);

        list.addView(text);

        list.addView(root);

        addOnShowing(() -> applyStyle(StyleUtils.getStyle(owner).get()));

        save = new ColoredButton(owner, Style.ACCENT, s -> Color.WHITE, "save");
        save.setFont(new Font(18, FontWeight.MEDIUM));

        list.addView(save);

        save.setOnClick(() -> {
            if (onSave != null && group.selected().get() != null) {
                Radio selected = group.selected().get();
                if (selected.getParent() instanceof LabeledRadio labeled) {
                    onSave.accept(labeled.getLabel().getKey());
                }
            }
        });

        group.selected().addListener((ov, nv) -> applyStyle(StyleUtils.getStyle(owner).get()));

        applyStyle(StyleUtils.getStyle(owner));
    }

    public void removeSave() {
        list.removeView(save);
        group.selected().addListener((o, selected) -> {
            if (selected != null && selected.getParent() instanceof LabeledRadio labeled) {
                onSave.accept(labeled.getLabel().getKey());
            }
        });
    }

    public void setOnSave(Consumer<String> onSave) {
        this.onSave = onSave;
    }

    public RadioGroup getGroup() {
        return group;
    }

    public void select(String value) {
        for (LabeledRadio lab : buttons) {
            if (lab.getLabel().getKey().equalsIgnoreCase(value)) {
                lab.setChecked(true);
                break;
            }
        }
    }

    public void addButton(String text) {
        addButton(text, null);
    }

    public void addButton(String text, StyleToColor fill) {
        LabeledRadio button = new LabeledRadio(owner, text);
        if (fill != null)
            button.setFill(fill);
        button.setFont(new Font(20));
        button.setCornerRadius(15);
        button.setPadding(15);
        button.setTransformationMethod(new Capitalize());
        root.addView(button);
        buttons.add(button);
        group.addRadio(button.getRadio());

        applyStyle(StyleUtils.getStyle(owner).get());
    }

    @Override
    public void applyStyle(Style style) {
        text.setFill(style.getTextNormal());

        for (LabeledRadio button : buttons) {
            button.setBackground(button.isChecked() ?
                    style.getBackgroundSecondary() : Color.TRANSPARENT);
        }
    }

}
