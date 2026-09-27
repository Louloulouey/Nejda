package com.sdidsa.nejda.abs.utils.view;

import android.content.Context;
import android.view.View;

import com.sdidsa.nejda.abs.App;
import com.sdidsa.nejda.abs.data.property.Property;
import com.sdidsa.nejda.abs.locale.Locale;
import com.sdidsa.nejda.abs.utils.Store;

public class LocaleUtils {


    public static int getLocaleDirection(View view) {
        return getLocaleDirection(view.getContext());
    }

    public static int getLocaleDirection(Context context) {
        return getLocale(context).get().getDirection();
    }

    public static String getLang(Context context) {
        return getLocale(context).get().getLang();
    }

    public static boolean isRtl(Context context) {
        return getLocale(context).get().isRtl();
    }

    public static boolean isRtl(View context) {
        return isRtl(context.getContext());
    }

    private static Property<Locale> locale;
    public static Property<Locale> getLocale(Context context) {
        if(context instanceof App app) {
            return app.getLocale();
        }else {
            if(locale == null) {
                locale = new Property<>(readLocale(context));
            }
            return locale;
        }
    }

    public static void localeChanged(Context context) {
        getLocale(context).set(readLocale(context));
    }

    private static Locale readLocale(Context context) {
        return Locale.forName(context, Store.getLanguage());
    }

    public static void setLocale(Context context, Locale locale) {
        getLocale(context).set(locale);
    }
}
