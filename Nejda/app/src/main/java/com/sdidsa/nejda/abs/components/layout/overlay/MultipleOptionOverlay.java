package com.sdidsa.nejda.abs.components.layout.overlay;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;

import com.sdidsa.nejda.abs.components.controls.button.Button;
import com.sdidsa.nejda.abs.components.controls.image.ColorIcon;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.controls.text.transformationMethods.Capitalize;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.data.ConcurrentArrayList;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

import java.util.function.Function;

public class MultipleOptionOverlay extends PartialSlideOverlay implements Styleable {
    protected final VBox root;
    private final Label text;
    private final ConcurrentArrayList<OptionButton> buttons;

    private final Function<String, Boolean> isSelected;

    public MultipleOptionOverlay(Context owner) {
        this(owner, "Overlay header", (s) -> false);
    }

    public MultipleOptionOverlay(Context owner, String header, Function<String,
            Boolean> isSelected) {
        super(owner, -2);

        this.isSelected = isSelected;

        root = new VBox(owner);
        root.setSpacing(10);
        root.setPadding(20);
        root.setGravity(Gravity.TOP | Gravity.CENTER);

        text = new Label(owner, header);
        text.centerText();
        text.setFont(new Font(20, FontWeight.MEDIUM));
        text.setLineSpacing(10);
        MarginUtils.setMarginBottom(text, owner, 20);

        buttons = new ConcurrentArrayList<>();

        root.addView(text);

        list.addView(root);

        addOnShowing(() -> applyStyle(StyleUtils.getStyle(owner).get()));

        applyStyle(StyleUtils.getStyle(owner));
    }

    public void addButton(String text, Runnable onClick) {
        addButton(new OverlayOption(text), onClick);
    }

    public void addButton(OverlayOption option, Runnable onClick) {
        OptionButton button = new OptionButton(owner, option, onClick);
        root.addView(button);
        buttons.add(button);

        applyStyle(StyleUtils.getStyle(owner).get());
    }

    public void startLoading(String option) {
        for(Button b : buttons) {
            if(b.getKey().equals(option)) {
                b.startLoading();
            }
        }
    }

    public void stopLoading(String option) {
        for(Button b : buttons) {
            if(b.getKey().equals(option)) {
                b.stopLoading();
            }
        }
    }

    @Override
    public void applyStyle(Style style) {
        text.setFill(style.getTextNormal());

        buttons.forEach(button ->
                button.applyStyle(style, isSelected.apply(button.getKey())));
    }

    private static class OptionButton extends Button {
        private final ColorIcon colorIcon;
        private final OverlayOption option;
        public OptionButton(Context owner, OverlayOption option, Runnable onClick) {
            super(owner, option.text());
            this.option = option;
            colorIcon = new ColorIcon(owner, option.icon(), 24);

            setFont(new Font(18));
            setTransformationMethod(new Capitalize());
            setOnClick(onClick);

            addPostLabel(SpacerUtils.spacer(owner, Orientation.HORIZONTAL));
            addPostLabel(colorIcon);
        }

        public void applyStyle(Style style, boolean selected) {
            if(selected) {
                setFill(style.getAccent());
                setTextFill(Color.WHITE);
                if(option.colored())
                    colorIcon.setFill(Color.WHITE);
            }else {
                setFill(style.getBackgroundPrimary());
                setTextFill(style.getTextNormal());
                if(option.colored())
                    colorIcon.setFill(style.getTextSecondary());
            }
        }
    }
}
