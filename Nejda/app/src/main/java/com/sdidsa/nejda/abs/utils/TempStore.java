package com.sdidsa.nejda.abs.utils;

import com.sdidsa.nejda.model.Thread;
import com.sdidsa.nejda.model.User;

import java.util.HashMap;
import java.util.Objects;

public class TempStore {
    public static final String USER = "logged_in_user";
    public static final String THREAD = "active_thread";

    public static HashMap<String, Object> data = new HashMap<>();

    public static void put(String key, Object value) {
        data.put(key, value);
    }

    public static Object get(String key) {
        return data.get(key);
    }

    public static void putUser(User user) {
        put(USER, user);
    }

    public static User getUser() {
        return (User) get(USER);
    }

    public static void putThread(Thread thread) {
        put(THREAD, thread);
    }

    public static Thread getThread() {
        return (Thread) get(THREAD);
    }
}
