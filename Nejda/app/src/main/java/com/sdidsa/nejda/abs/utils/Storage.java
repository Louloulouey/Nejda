package com.sdidsa.nejda.abs.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {
    private static final String PREFS_NAME = "nejda_prefs";
    private static final String USER_ID_KEY = "current_user_id";
    private static final String TOKEN_KEY = "auth_token";
    
    // Get SharedPreferences instance
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    // Save user ID
    public static void saveUserId(Context context, Long userId) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putLong(USER_ID_KEY, userId);
        editor.apply();
    }
    
    // Get user ID
    public static Long getUserId(Context context) {
        long userId = getPrefs(context).getLong(USER_ID_KEY, -1);
        return userId != -1 ? userId : null;
    }
    
    // Save auth token
    public static void saveToken(Context context, String token) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }
    
    // Get auth token
    public static String getToken(Context context) {
        return getPrefs(context).getString(TOKEN_KEY, null);
    }
    
    // Clear all stored data
    public static void clearAll(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.clear();
        editor.apply();
    }
} 