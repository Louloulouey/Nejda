package com.sdidsa.nejda.controll.abs;

import com.sdidsa.nejda.model.Bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Api {

    protected static void sendRequest(String path, Consumer<JSONObject> onResponse,
                                    BodyParam...params) {
        new ApiRequest(path, onResponse).addBodyParams(params).execute();
    }

    protected static void sendRequest(String path, Consumer<JSONObject> onResponse,
                                      Bean body) {
        sendRequest(path, onResponse, body.toJson());
    }

    protected static void sendRequest(String path, Consumer<JSONObject> onResponse,
                                      JSONObject body) {
        new ApiRequest(path, onResponse).setBody(body).execute();
    }

    public static <T> List<T> parseList(JSONObject obj, Class<T> type) {
        try {
            if(obj.has("array")) {
                JSONArray arr = obj.getJSONArray("array");
                return parseList(arr, type);
            } else {
                return new ArrayList<>();
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> parseList(JSONArray arr, Class<T> type) {
        ArrayList<T> dat = new ArrayList<>();
        try {
            for(int i = 0; i < arr.length(); i++) {
                dat.add(type.getConstructor(JSONObject.class).newInstance(arr.getJSONObject(i)));
            }
        } catch (JSONException | NoSuchMethodException | IllegalAccessException |
                 InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return dat;
    }

}
