package com.sdidsa.nejda.abs.components.controls.text.font;

import android.graphics.Typeface;
import android.graphics.fonts.FontFamily;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.sdidsa.nejda.abs.App;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Font {

    public static final String DEFAULT_FAMILY = "Outfit";
    public static final String DEFAULT_FAMILY_ARABIC = "IBM Plex Sans Arabic";
    public static final FontWeight DEFAULT_WEIGHT = FontWeight.NORMAL;
    public static final float DEFAULT_SIZE = 14;

    public static final Font DEFAULT = new Font();
    private static final ConcurrentHashMap<String, Typeface> base = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Font, Typeface> cache = new ConcurrentHashMap<>();

    private final String family;
    private final float size;
    private final FontWeight weight;

    public Font(String family, float size, FontWeight weight) {
        this.family = family;
        this.size = size;
        this.weight = weight;
    }

    public Font(float size) {
        this(DEFAULT_FAMILY, size, DEFAULT_WEIGHT);
    }

    public Font(float size, FontWeight weight) {
        this(DEFAULT_FAMILY, size, weight);
    }

    public Font() {
        this(DEFAULT_FAMILY, DEFAULT_SIZE, DEFAULT_WEIGHT);
    }

    public static void init(App owner) {
        loadFont(owner);
    }

    private static void loadFont(App owner) {
        for (FontVar var : FontVar.values()) {
            try {
                String path = "fonts/" +
                        DEFAULT_FAMILY.replace(" ", "") + "-" + var.name.replace(" ", "") + ".ttf";

                Typeface typeface;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    android.graphics.fonts.Font font = new android.graphics.fonts.Font.Builder(owner.getAssets(), path).build();
                    typeface = getTypeface(owner, var, font);
                } else {
                    typeface = Typeface.createFromAsset(owner.getAssets(), path);
                }
                base.put(DEFAULT_FAMILY + " " + var.name, typeface);
            } catch (Exception x) {
                //ErrorHandler.handle(x, "loading font ".concat(name).concat(" ").concat(var.name));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static @NonNull Typeface getTypeface(App owner, FontVar var, android.graphics.fonts.Font font) throws IOException {
        FontFamily family = new FontFamily.Builder(font).build();

        String fallBack = "fonts/" + DEFAULT_FAMILY_ARABIC.replace(" ", "") + "-" + var.name.replace(" ", "") + ".ttf";
        android.graphics.fonts.Font fallbackFont = new android.graphics.fonts.Font.Builder(owner.getAssets(), fallBack).build();
        FontFamily fallbackFamily = new FontFamily.Builder(fallbackFont).build();
        return new Typeface.CustomFallbackBuilder(family)
                .addCustomFallback(fallbackFamily)
                .setSystemFallback(DEFAULT_FAMILY_ARABIC)
                .build();
    }

    @Override
    public int hashCode() {
        return Objects.hash(family, size, weight);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Font other = (Font) obj;
        return Objects.equals(family, other.family)
                && Float.floatToIntBits(size) == Float.floatToIntBits(other.size) && weight == other.weight;
    }

    public Typeface getFont() {
        Typeface found = cache.get(this);

        if (found == null) {
            found = Typeface.create(base.get(family + " " + FontVar.getVar(weight)), Typeface.NORMAL);
            cache.put(this, found);
        }

        return found;
    }

    public float getSize() {
        return size * SizeUtils.scale;
    }

    @NonNull
    @Override
    public String toString() {
        return "Font [family=" + family + ", size=" + size + ", weight=" + weight + "]";
    }

    private enum FontVar {
        LIGHT("Light", FontWeight.LIGHT),
        REGULAR("Regular", FontWeight.NORMAL),
        MEDIUM("Medium", FontWeight.MEDIUM),
        SEMIBOLD("SemiBold", FontWeight.SEMIBOLD),
        BOLD("Bold", FontWeight.BOLD);

        final String name;
        final FontWeight weight;

        FontVar(String name, FontWeight weight) {
            this.name = name;
            this.weight = weight;
        }

        static String getVar(FontWeight weight) {
            for (FontVar var : values()) {
                if (weight == var.weight) return var.name;
            }

            return REGULAR.name;
        }
    }
}