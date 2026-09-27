package com.sdidsa.nejda.abs.style;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.ColorInt;

import org.json.JSONException;
import org.json.JSONObject;

import com.sdidsa.nejda.abs.utils.Assets;
import com.sdidsa.nejda.abs.utils.ErrorHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Style {
    public static final String THEME_SYSTEM = "theme_system";
    public static final String THEME_DARK = "theme_dark";
    public static final String THEME_LIGHT = "theme_light";

    public static final StyleToColor BACK_PRI = Style::getBackgroundPrimary;
    public static final StyleToColor BACK_SEC = Style::getBackgroundSecondary;
    public static final StyleToColor BACK_TER = Style::getBackgroundTertiary;
    public static final StyleToColor TEXT_NORM = Style::getTextNormal;
    public static final StyleToColor TEXT_SEC = Style::getTextSecondary;
    public static final StyleToColor TEXT_MUT = Style::getTextMuted;
    public static final StyleToColor TEXT_ERR = Style::getTextError;
    public static final StyleToColor TEXT_POS = Style::getTextPositive;
    public static final StyleToColor ACCENT = Style::getAccent;
    public static final StyleToColor EMPTY = s -> Color.TRANSPARENT;
    public static final StyleToColor ONACCENT = s -> Color.WHITE;

    private final ConcurrentHashMap<String, Integer> colors;
    private final boolean dark;

    private Style(boolean dark) {
        colors = new ConcurrentHashMap<>();
        this.dark = dark;
    }

    private Style(Context owner, String styleName, boolean dark) {
        this.dark = dark;
        colors = new ConcurrentHashMap<>();
        try {
            JSONObject data;
            if (styleName.startsWith("#")) {
                data = new JSONObject(ColorPalette.generatePalette(styleName, dark).toString());
            } else {
                data = new JSONObject(Objects.requireNonNull(
                        Assets.readAsset(owner, "themes/" + styleName + ".json")));
            }
            Iterator<String> keys = data.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                colors.put(key, Color.parseColor(data.getString(key)));
            }
        } catch (JSONException e) {
            ErrorHandler.handle(e, "create Style object");
        }
    }

    public static Style interpolateColors(Style from, Style to, float progress) {
        Style result = new Style(to.dark);

        from.colors.forEach((key, value) -> {
            Integer toColor;
            if ((toColor = to.colors.get(key)) != null) {
                result.colors.put(key,
                        interpolateColor(value, toColor, progress));
            }
        });

        return result;
    }

    public static @ColorInt int interpolateColor(@ColorInt int from, @ColorInt int to, float progress) {
        int red = (int) (Color.red(from) + progress * (Color.red(to) - Color.red(from)));
        int green = (int) (Color.green(from) + progress * (Color.green(to) - Color.green(from)));
        int blue = (int) (Color.blue(from) + progress * (Color.blue(to) - Color.blue(from)));
        int alpha = (int) (Color.alpha(from) + progress * (Color.alpha(to) - Color.alpha(from)));

        red = clamp(red);
        green = clamp(green);
        blue = clamp(blue);
        alpha = clamp(alpha);

        return Color.argb(alpha, red, green, blue);
    }

    private static final HashMap<StyleIdentifier, Style> cache = new HashMap<>();
    public static Style get(Context owner, boolean dark, String accent) {
        StyleIdentifier id = new StyleIdentifier(dark, accent);
        return cache.computeIfAbsent(id, k -> new Style(owner, accent, dark));
    }

    private static int clamp(int v) {
        return v < 0 ? 0 : Math.min(v, 255);
    }

    public boolean isDark() {
        return dark;
    }

    public boolean isLight() {
        return !dark;
    }

    @ColorInt
    public int getAccent() {
        return getAccent(1);
    }

    @ColorInt
    @SuppressWarnings("ConstantConditions")
    public int getAccent(int num) {
        return colors.get("accent_" + num);
    }

    @ColorInt
    @SuppressWarnings("ConstantConditions")
    public int getTextNormal() {
        return colors.get("text_primary");
    }

    @ColorInt
    @SuppressWarnings("ConstantConditions")
    public int getTextSecondary() {
        return colors.get("text_secondary");
    }

    @ColorInt
    @SuppressWarnings("ConstantConditions")
    public int getTextMuted() {
        return colors.get("text_muted");
    }

    @ColorInt
    @SuppressWarnings("ConstantConditions")
    public int getTextPositive() {
        return colors.get("text_positive");
    }

    @ColorInt
    @SuppressWarnings("ConstantConditions")
    public int getTextError() {
        return colors.get("text_error");
    }

    @ColorInt
    @SuppressWarnings("ConstantConditions")
    public int getBackgroundPrimary() {
        return colors.get("background_primary");
    }

    @ColorInt
    @SuppressWarnings("ConstantConditions")
    public int getBackgroundSecondary() {
        return colors.get("background_secondary");
    }

    @ColorInt
    @SuppressWarnings("ConstantConditions")
    public int getBackgroundTertiary() {
        return colors.get("background_tertiary");
    }
}
