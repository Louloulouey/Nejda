package org.luke.controll.ai;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.BiFunction;

public class ParamType<T> {
    public static final ParamType<String> STRING = new ParamType<>("STRING", (obj, name) -> {
        try {
            return obj.getString(name);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    });
    public static final ParamType<Integer> INTEGER = new ParamType<>("INTEGER", (obj, name) -> {
        try {
            return obj.getInt(name);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    });

    public final String name;
    public final BiFunction<JSONObject, String, T> getter;

    public ParamType(String name, BiFunction<JSONObject, String, T> getter) {
        this.name = name;
        this.getter = getter;
    }

    public String getName() {
        return name;
    }

    public T get(JSONObject obj, String name) throws JSONException {
        return getter.apply(obj, name);
    }
}
