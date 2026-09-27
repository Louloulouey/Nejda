package com.sdidsa.nejda.abs.components.controls.input;

import android.content.Context;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.style.Style;

public class PasswordField extends InputField {

    private boolean hidden = true;
    private final ColoredIcon eye;

    public PasswordField(Context owner) {
        this(owner, "Prompt text");
    }

    public PasswordField(Context owner, String promptText) {
        super(owner, promptText);

        eye = addPostIcon(R.drawable.eye, () -> setHidden(!hidden));

        setHidden(true);
    }

    @Override
    public void setHidden(boolean hidden) {
        int caret = input.getSelectionEnd();
        super.setHidden(hidden);
        this.hidden = hidden;
        input.setSelection(caret);
        eye.setImageResource(hidden ? R.drawable.eye: R.drawable.eye_off);
        eye.setFill(hidden ? Style.TEXT_SEC : Style.TEXT_NORM);
    }
}
