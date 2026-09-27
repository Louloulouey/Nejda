package com.sdidsa.nejda.controll.ai;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AiResponse {
    private final ArrayList<String> texts;
    private final ArrayList<AiFunction> functions;

    private final JSONObject arguments;

    public AiResponse() {
        this.arguments = new JSONObject();
        this.texts = new ArrayList<>();
        this.functions = new ArrayList<>();
    }

    public void addText(String text) {
        this.texts.add(text);
    }

    public void addFunction(String function) {
        AiFunction func = AiFunction.byName(function);
        if (func != null)
            this.functions.add(func);
    }

    public JSONObject getArguments() {
        return arguments;
    }

    public void putArg(String name, Object value) throws JSONException {
        this.arguments.put(name, value);
    }

    public ArrayList<String> getTexts() {
        return this.texts;
    }

    public ArrayList<AiFunction> getFunctions() {
        return this.functions;
    }
}
