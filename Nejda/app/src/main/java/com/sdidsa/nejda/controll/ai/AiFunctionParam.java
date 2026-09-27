package com.sdidsa.nejda.controll.ai;

import org.json.JSONException;
import org.json.JSONObject;

public class AiFunctionParam<T> {
    public static final AiFunctionParam<Integer> ALERT_SEVERITY = new AiFunctionParam<>("severity",
            ParamType.INTEGER,
            "The severity level of the emergency (1, 2, or 3, where 3 is most severe)",
            true);
    public static final AiFunctionParam<String> ALERT_LOCATION = new AiFunctionParam<>("location",
            ParamType.STRING,
            "The location of the emergency.",
            true);
    public static final AiFunctionParam<String> ALERT_TYPE = new AiFunctionParam<>("type",
            ParamType.STRING,
            "The type alert (eg: fire, flood, earthquake)",
            true);

    private final String name;
    private final ParamType<T> type;
    private final String description;
    private final boolean required;

    private AiFunctionParam(String name, ParamType<T> type, String description, boolean required) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public T get(JSONObject obj) throws JSONException {
        return type.get(obj, name);
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }

    public void put(JSONObject obj) throws JSONException {
        obj.put(name, new JSONObject()
                .put("type", type.getName())
                .put("description", description));
    }

    @SuppressWarnings("unchecked")
    public static <T> AiFunctionParam<T> byName(String name) {
        return switch (name) {
            case "severity" -> (AiFunctionParam<T>) ALERT_SEVERITY;
            case "location" -> (AiFunctionParam<T>) ALERT_LOCATION;
            default -> null;
        };
    }
}
