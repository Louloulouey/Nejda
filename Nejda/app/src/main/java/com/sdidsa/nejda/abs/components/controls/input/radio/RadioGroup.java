package com.sdidsa.nejda.abs.components.controls.input.radio;

import com.sdidsa.nejda.abs.data.property.Property;

import java.util.ArrayList;
import java.util.Arrays;

public class RadioGroup {
    private final ArrayList<Radio> radios;
    private final Property<Radio> selected;

    public RadioGroup() {
        radios = new ArrayList<>();
        selected = new Property<>(null);
    }

    public RadioGroup(Radio... radios) {
        this();
        addRadios(radios);
    }

    public RadioGroup(LabeledRadio...radios) {
        this(Arrays.stream(radios).map(LabeledRadio::getRadio).toArray(Radio[]::new));
    }

    public void addRadio(Radio r) {
        addRadios(r);
    }

    public Property<Radio> selected() {
        return selected;
    }

    public void addRadios(Radio... rs) {
        for(Radio r : rs) {
           radios.add(r);

            r.checkedProperty().addListener((ov, nv) -> {
                if(!ov && nv) {
                    radios.stream()
                            .filter(radio -> radio != r)
                            .forEach(radio -> radio.setChecked(false));
                    selected.set(r);
                }
            });
        }
    }
}
