package com.sdidsa.nejda.abs.components.layout;

import android.graphics.drawable.GradientDrawable;

import androidx.annotation.ColorInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gradient {
    private final ArrayList<GradientStop> stops;
    private final GradientDrawable.Orientation orientation;

    public Gradient(GradientDrawable.Orientation orientation,
                    GradientStop...stops) {
        this.stops = new ArrayList<>();
        this.stops.addAll(Arrays.asList(stops));

        this.orientation = orientation;
    }

    public Gradient(@ColorInt int fill) {
        this(GradientDrawable.Orientation.LEFT_RIGHT,
                new GradientStop(fill, 0),
                new GradientStop(fill, 1));
    }

    public List<GradientStop> getStops() {
        return stops;
    }

    public GradientDrawable.Orientation getOrientation() {
        return orientation;
    }
}
