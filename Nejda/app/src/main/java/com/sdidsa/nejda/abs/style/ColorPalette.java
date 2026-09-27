package com.sdidsa.nejda.abs.style;

import android.graphics.Color;

import androidx.annotation.NonNull;

public class ColorPalette {
    public String background_primary;
    public String background_secondary;
    public String background_tertiary;
    public String text_primary;
    public String text_secondary;
    public String text_muted;
    public String text_error;
    public String text_positive;
    public String accent_1;

    @NonNull
    @Override
    public String toString() {
        return String.format("""
                        {
                          "background_primary": "%s",
                          "background_secondary": "%s",
                          "background_tertiary": "%s",
                          "text_primary": "%s",
                          "text_secondary": "%s",
                          "text_muted": "%s",
                          "text_error": "%s",
                          "text_positive": "%s",
                          "accent_1": "%s"
                        }""",
                background_primary, background_secondary, background_tertiary,
                text_primary, text_secondary, text_muted, text_error, text_positive,
                accent_1);
    }


    public static String colorToHex(int color) {
        return String.format("#%08X", color);
    }

    public static String HSLToHex(float...hsl) {
        return colorToHex(HSLToColor(hsl));
    }

    public static float[] colorToHSL(int color, float[] hsl) {
        if (hsl == null) hsl = new float[3];
        float r = Color.red(color) / 255f;
        float g = Color.green(color) / 255f;
        float b = Color.blue(color) / 255f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float h, s, l;
        l = (max + min) / 2;

        if (max == min) {
            h = s = 0;
        } else {
            float d = max - min;
            s = l > 0.5f ? d / (2f - max - min) : d / (max + min);

            if (max == r) {
                h = (g - b) / d + (g < b ? 6 : 0);
            } else if (max == g) {
                h = (b - r) / d + 2;
            } else {
                h = (r - g) / d + 4;
            }
            h *= 60;
        }

        hsl[0] = h;
        hsl[1] = s;
        hsl[2] = l;

        return hsl;
    }

    public static int HSLToColor(float... hsl) {
        float h = hsl[0];
        float s = hsl[1];
        float l = hsl[2];

        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = l - c / 2;

        float r, g, b;

        if (h < 60) {
            r = c;
            g = x;
            b = 0;
        } else if (h < 120) {
            r = x;
            g = c;
            b = 0;
        } else if (h < 180) {
            r = 0;
            g = c;
            b = x;
        } else if (h < 240) {
            r = 0;
            g = x;
            b = c;
        } else if (h < 300) {
            r = x;
            g = 0;
            b = c;
        } else {
            r = c;
            g = 0;
            b = x;
        }

        return Color.argb(255,
                Math.round((r + m) * 255),
                Math.round((g + m) * 255),
                Math.round((b + m) * 255));
    }

    private static int createColorWithHue(int color, float saturation, float lightness) {
        float[] hsl = new float[3];
        colorToHSL(color, hsl);
        hsl[0] = hsl[0] % 360f; // Ensure hue is within [0, 360)
        hsl[1] = saturation;
        hsl[2] = lightness;
        return HSLToColor(hsl);
    }

    private static float contrastRatio(int color1, int color2) {
        float l1 = relativeLuminance(color1);
        float l2 = relativeLuminance(color2);
        return (Math.max(l1, l2) + 0.05f) / (Math.min(l1, l2) + 0.05f);
    }

    private static float relativeLuminance(int color) {
        float r = Color.red(color) / 255f;
        float g = Color.green(color) / 255f;
        float b = Color.blue(color) / 255f;

        r = (r <= 0.03928f) ? r / 12.92f : (float) Math.pow((r + 0.055f) / 1.055f, 2.4f);
        g = (g <= 0.03928f) ? g / 12.92f : (float) Math.pow((g + 0.055f) / 1.055f, 2.4f);
        b = (b <= 0.03928f) ? b / 12.92f : (float) Math.pow((b + 0.055f) / 1.055f, 2.4f);

        return 0.2126f * r + 0.7152f * g + 0.0722f * b;
    }

    private static int generateAccessibleAccentColor(float hue, float saturation) {
        final int MAX_ITERATIONS = 200;
        saturation *= 0.75f;
        float lightness = .5f; // Start from middle values

        int bestColor = 0;
        float lastScore = -1;
        float bestScore = -1;

        boolean flip_direction = false;

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            int color = HSLToColor(new float[]{hue, saturation, lightness});

            float contrastWithWhite = contrastRatio(Color.WHITE, color);
            float contrastWithBlack = contrastRatio(Color.BLACK, color);

            float score = (contrastWithWhite + contrastWithBlack) - Math.abs(contrastWithWhite - contrastWithBlack);

            if (score > bestScore) {
                bestScore = score;
                bestColor = color;
            }

            if (score < lastScore) {
                flip_direction = !flip_direction;
            }

            lastScore = score;

            if (flip_direction) {
                if (contrastWithBlack < 4.5f) {
                    lightness += 0.005f;
                } else if (contrastWithWhite < 4.5f) {
                    lightness -= 0.005f;
                } else {
                    break;
                }
            } else {
                if (contrastWithWhite < 4.5f) {
                    lightness -= 0.005f;
                } else if (contrastWithBlack < 4.5f) {
                    lightness += 0.005f;
                } else {
                    break;
                }
            }

            lightness = Math.max(0.2f, Math.min(0.8f, lightness));
        }

        return bestColor;
    }

    private static int createStatusColor(int baseHueColor, boolean isDarkTheme) {
        float[] baseHsl = new float[3];
        colorToHSL(baseHueColor, baseHsl);

        float saturation = 0.8f;
        float lightness = isDarkTheme ? 0.65f : 0.4f;

        int color = createColorWithHue(baseHueColor, saturation, lightness);

        int bgColor = isDarkTheme ? Color.BLACK : Color.WHITE;
        while (contrastRatio(color, bgColor) < 4.5f) {
            float[] hsl = new float[3];
            colorToHSL(color, hsl);
            hsl[1] = Math.max(0.7f, hsl[1] - 0.02f);
            hsl[2] += isDarkTheme ? 0.02f : -0.02f;
            color = HSLToColor(hsl);
        }

        return color;
    }

    public static ColorPalette generateDarkPalette(String accentColorHex) {
        ColorPalette palette = new ColorPalette();

        int accentColor = Color.parseColor(accentColorHex);
        float[] accHsl = colorToHSL(accentColor, null);
        accentColor = generateAccessibleAccentColor(accHsl[0], accHsl[1]);

        int bgPrimary = createColorWithHue(accentColor, accHsl[1] * 0.07f, 0.14f);
        int bgSecondary = createColorWithHue(accentColor, accHsl[1] * 0.07f, 0.20f);
        int bgTertiary = createColorWithHue(accentColor, accHsl[1] * 0.13f, 0.10f);

        int textPrimary = Color.WHITE;
        int textSecondary = Color.rgb(194, 182, 182);
        int textMuted = Color.rgb(153, 153, 153);

        int errorColor = createStatusColor(Color.RED, true);
        int positiveColor = createStatusColor(Color.GREEN, true);

        palette.background_primary = colorToHex(bgPrimary);
        palette.background_secondary = colorToHex(bgSecondary);
        palette.background_tertiary = colorToHex(bgTertiary);

        palette.text_primary = colorToHex(textPrimary);
        palette.text_secondary = colorToHex(textSecondary);
        palette.text_muted = colorToHex(textMuted);

        palette.text_error = colorToHex(errorColor);
        palette.text_positive = colorToHex(positiveColor);

        palette.accent_1 = colorToHex(accentColor);

        return palette;
    }

    public static ColorPalette generateLightPalette(String accentColorHex) {
        ColorPalette palette = new ColorPalette();

        int accentColor = Color.parseColor(accentColorHex);
        float[] accHsl = colorToHSL(accentColor, null);
        accentColor = generateAccessibleAccentColor(accHsl[0], accHsl[1]);

        int bgPrimary = createColorWithHue(accentColor, accHsl[1] * 0.05f, 0.97f);
        int bgSecondary = createColorWithHue(accentColor, accHsl[1] * 0.07f, 0.93f);
        int bgTertiary = createColorWithHue(accentColor, accHsl[1] * 0.10f, 0.88f);

        int textPrimary = Color.rgb(32, 32, 32);
        int textSecondary = Color.rgb(90, 90, 90);
        int textMuted = Color.rgb(130, 130, 130);

        int errorColor = createStatusColor(Color.RED, false);
        int positiveColor = createStatusColor(Color.GREEN, false);

        palette.background_primary = colorToHex(bgPrimary);
        palette.background_secondary = colorToHex(bgSecondary);
        palette.background_tertiary = colorToHex(bgTertiary);

        palette.text_primary = colorToHex(textPrimary);
        palette.text_secondary = colorToHex(textSecondary);
        palette.text_muted = colorToHex(textMuted);

        palette.text_error = colorToHex(errorColor);
        palette.text_positive = colorToHex(positiveColor);

        palette.accent_1 = colorToHex(accentColor);

        return palette;
    }

    public static ColorPalette generatePalette(String accent, boolean dark) {
        if (dark) {
            return generateDarkPalette(accent);
        } else {
            return generateLightPalette(accent);
        }
    }
}