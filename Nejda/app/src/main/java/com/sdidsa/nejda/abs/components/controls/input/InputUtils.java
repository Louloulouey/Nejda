package com.sdidsa.nejda.abs.components.controls.input;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.data.property.Property;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InputUtils {
    public static void setChangeListener(EditText input, Consumer<String> onChange) {
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence ov, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence nv, int i, int i1, int i2) {
                try {
                    onChange.accept(String.valueOf(nv));
                } catch (Exception x) {
                    ErrorHandler.handle(x, "set change listener on input");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    public static void bindToProperty(EditText input, Property<String> property) {
        setChangeListener(input, property::set);
    }

    public static void applyErrors(ViewGroup parent, String errorString) {
        try {
            List<InputField> fields = getFields(parent);
            JSONObject obj = new JSONObject(errorString);
            if(obj.has("errors")) {
                JSONArray errors = obj.getJSONArray("errors");

                for(int i = 0; i < errors.length(); i++) {
                    JSONObject error = errors.getJSONObject(i);
                    if(error.getString("type").equals("field") &&
                            error.getString("location").equals("body")) {
                        String key = error.getString("path");
                        String message = error.getString("msg");

                        InputField f = forKey(key, fields);
                        if(f != null) {
                            f.setError(message);
                        }
                    }
                }

            }

        } catch (JSONException e) {
            ErrorHandler.handle(e, "parsing error object");
        }
    }

    public static void clearErrors(ViewGroup root) {
        for(InputField field : getFields(root)) {
            field.hideError();
        }
    }

    public static void clearInputs(ViewGroup root) {
        for(InputField field : getFields(root)) {
            field.setValue("");
        }
    }

    private static InputField forKey(String key, List<InputField> all) {
        for(InputField field : all) {
            if(field.getKey().equalsIgnoreCase(key)) return field;
        }

        return null;
    }
    private static List<InputField> getFields(ViewGroup root) {
        ArrayList<InputField> res = new ArrayList<>();

        if(root instanceof InputField field) {
            res.add(field);
            return res;
        }

        for(int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if(child instanceof ViewGroup parent) {
                res.addAll(getFields(parent));
            }
        }
        return res;
    }
}
