package com.sdidsa.nejda.abs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.controll.ai.AiConversation;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

public class Store {
    private static SharedPreferences settingsD;

    //AI CACHE
    private static final String AI_CACHE = "ai_cache";

    //DISPLAY SETTINGS
    private static final String THEME = "theme";
    private static final String COLOR = "color";
    private static final String ANIMATIONS = "animations";
    private static final String LANGUAGE = "language";
    private static final String SCALE = "scale";

    //USER DATA
    private static final String FMT = "firebase_token";
    private static final String USER_ID = "user_id";
    private static final String REMEMBER_USERNAME = "rememberUsername";

    public static void init(Context owner) {
        if (settingsD == null)
            settingsD = owner.getSharedPreferences("global", Context.MODE_PRIVATE);
    }

    private static final Semaphore mutex = new Semaphore(1);

    private static String getSetting(String key, String def) {
        mutex.acquireUninterruptibly();
        try {
            String val = settingsD.getString(key, def);
            mutex.release();
            return val;
        } catch (Exception x) {
            mutex.release();
            return def;
        }
    }

    private static void setSetting(String key, String value, Consumer<String> onSuccess) {
        Platform.runBack(() -> {
            mutex.acquireUninterruptibly();
            SharedPreferences.Editor editor = settingsD.edit();
            editor.putString(key, value);
            boolean success = editor.commit();
            mutex.release();
            if (success && onSuccess != null)
                Platform.runLater(() -> {
                    try {
                        onSuccess.accept(value);
                    } catch (Exception e) {
                        ErrorHandler.handle(e, "storing data at " + key);
                    }
                });
        });
    }

    public static void saveChatHistory(String history, Consumer<String> onSuccess) {
        setSetting(AI_CACHE, history, onSuccess);
    }

    public static void saveChatHistory(HashMap<Long, AiConversation> conversations, Consumer<String> onSuccess) {
        JSONArray array = new JSONArray();
        for (AiConversation conversation : conversations.values()) {
            array.put(conversation.toJson());
        }
        saveChatHistory(array.toString(), onSuccess);
    }

    public static HashMap<Long, AiConversation> getChatHistory() {
        String history = getSetting(AI_CACHE, "[]");
        HashMap<Long, AiConversation> result = new HashMap<>();
        try {
            JSONArray arr = new JSONArray(history);
            for (int i = 0; i < arr.length(); i++) {
                AiConversation conv = AiConversation.fromJson(arr.getJSONObject(i));
                assert conv != null;
                result.put(conv.getId(), conv);
            }
        } catch (Exception e) {
            ErrorHandler.handle(e, "getting chat history");
        }
        return result;
    }

    public static List<AiConversation> chatHistory() {
        ArrayList<AiConversation> result = new ArrayList<>(getChatHistory().values());
        result.sort(Comparator.comparingLong(AiConversation::getId).reversed());
        return result;
    }

    public static void deleteConversation(AiConversation conv) {
        HashMap<Long, AiConversation> conversations = getChatHistory();
        conversations.remove(conv.getId());
        saveChatHistory(conversations, null);
    }

    public static long saveConversation(AiConversation conversation, Consumer<String> onSuccess) {
        if(conversation.getId() == -1) {
            conversation.setId(System.currentTimeMillis());
        }

        if(conversation.getChatParts().isEmpty()) {
            return -1;
        }

        HashMap<Long, AiConversation> conversations = getChatHistory();
        conversations.put(conversation.getId(), conversation);
        saveChatHistory(conversations, onSuccess);
        return conversation.getId();
    }

    public static String getTheme() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return getSetting(THEME, Style.THEME_SYSTEM);
        } else {
            return getSetting(THEME, Style.THEME_DARK);
        }
    }

    public static void setTheme(String theme, Consumer<String> onSuccess) {
        setSetting(THEME, theme, onSuccess);
    }

    public static String getColor() {
        return getSetting(COLOR, "#009688");
    }

    public static void setColor(String color, Consumer<String> onSuccess) {
        setSetting(COLOR, color, onSuccess);
    }

    public static String getFirebaseToken() {
        return getSetting(FMT, "");
    }

    public static void setFirebaseToken(String token, Consumer<String> onSuccess) {
        setSetting(FMT, token, onSuccess);
    }

    public static String getRememberUsername() {
        return getSetting(REMEMBER_USERNAME, "");
    }

    public static void setRememberUsername(String username, Consumer<String> onSuccess) {
        setSetting(REMEMBER_USERNAME, username, onSuccess);
    }

    public static String getAnimations() {
        return getSetting(ANIMATIONS, Animation.DEFAULT);
    }

    public static void setAnimations(String animations, Consumer<String> onSuccess) {
        setSetting(ANIMATIONS, animations, onSuccess);
    }

    public static String getLanguage() {
        return getSetting(LANGUAGE, "en_US");
    }

    public static void setLanguage(String value, Consumer<String> onSuccess) {
        setSetting(LANGUAGE, value, onSuccess);
    }

    public static UiScale getScale() {
        return UiScale.forText(getSetting(SCALE, UiScale.AUTO.getText()));
    }

    public static void setScale(String value, Consumer<String> onSuccess) {
        setSetting(SCALE, value, onSuccess);
    }

    public static String getUserId() {
        return getSetting(USER_ID, "-1");
    }

    public static void setUserId(String value, Consumer<String> onSuccess) {
        setSetting(USER_ID, value, onSuccess);
    }
}
