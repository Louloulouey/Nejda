package com.sdidsa.nejda.controll.ai;

import android.content.Context;

import com.sdidsa.nejda.abs.components.Page;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.view.app_content.session.contacts.ContactsPage;
import com.sdidsa.nejda.view.app_content.session.home.Home;
import com.sdidsa.nejda.view.app_content.session.report.ReportEmergency;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiConsumer;

public enum AiFunction {
    CALL_FOR_HELP("call_for_help",
            "Call for help",
            "Displays an emergency call button to the user. Use this function when a " +
                    "student requests immediate help, or describes a situation clearly requiring " +
                    "intervention from safety personnel. " +
                    "don't ask to use this function, just use it when convenient while explaining what it does",
            (owner, obj) -> ContextUtils.loadPage(owner, ContactsPage.class)),
    DISPLAY_MAPS("display_maps",
            "Display maps",
            "Displays a maps button that allows the user to open a map of the area and get " +
                    "evacuation routes and how to get out or to difference places in the campus area. " +
                    "don't ask to use this function, just use it when convenient while explaining what it does",
            (owner, obj) -> {
                Home home = Page.getInstance(owner, Home.class);
                home.getNavBar().selectMaps();
            }),
    REPORT_EMERGENCY("report_emergency",
            "Report Emergency",
            "Displays a report emergency button that allows the user to report an incident " +
                    "to the safety staff. " +
                    "don't ask to use this function, just use it when convenient while explaining what it does",
            (owner, args) -> {
                try {
                    int severity = AiFunctionParam.ALERT_SEVERITY.get(args);
                    String type = AiFunctionParam.ALERT_TYPE.get(args);
                    String location = AiFunctionParam.ALERT_LOCATION.get(args);
                    ReportEmergency.load(owner, severity, type, location);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                ContextUtils.loadPage(owner, ReportEmergency.class);
            },
            AiFunctionParam.ALERT_SEVERITY, AiFunctionParam.ALERT_TYPE, AiFunctionParam.ALERT_LOCATION);

    private final String name;
    private final String displayName;
    private final String description;
    private final BiConsumer<Context, JSONObject> action;
    private final ArrayList<AiFunctionParam<?>> params;

    AiFunction(String name, String displayName, String description, BiConsumer<Context, JSONObject> action,
               AiFunctionParam<?>...params) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.action = action;
        this.params = new ArrayList<>();
        Collections.addAll(this.params, params);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public BiConsumer<Context, JSONObject> getAction() {
        return action;
    }

    public ArrayList<AiFunctionParam<?>> getParams() {
        return params;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject properties = new JSONObject();
        JSONArray required = new JSONArray();
        for (AiFunctionParam<?> param : params) {
            param.put(properties);
            if(param.isRequired()) {
                required.put(param.getName());
            }
        }

        return new JSONObject().put("name", name).put("description", description)
                .put("parameters", new JSONObject()
                        .put("type", "OBJECT")
                        .put("properties", properties)
                        .put("required", required));
    }

    public static JSONArray toJsonArray() throws JSONException {
        JSONArray arr = new JSONArray();

        for (AiFunction func : AiFunction.values()) {
            arr.put(func.toJson());
        }

        return arr;
    }

    public static AiFunction byName(String name) {
        for (AiFunction func : AiFunction.values()) {
            if (func.getName().equalsIgnoreCase(name)) {
                return func;
            }
        }
        return null;
    }
}
