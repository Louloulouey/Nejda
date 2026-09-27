package com.sdidsa.nejda.model;

import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.controll.abs.Api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.List;

public class Bean {
    public Bean(JSONObject data) {
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field : fields) {
            String key = field.getName();
            if(data.has(key)) {
                try {
                    String setterName = setterName(key);
                    Class<?> type = field.getType();
                    if(Bean.class.isAssignableFrom(type)) {
                        JSONObject obj = data.getJSONObject(key);
                        this.getClass().getMethod(setterName, type).invoke(this,
                                type.getConstructor(JSONObject.class).newInstance(obj));
                    } else {
                        if(BeanParser.isSupported(type)) {
                            Object o = BeanParser.parse(type, String.valueOf(data.get(key)));
                            this.getClass().getMethod(setterName, type).invoke(this, o);
                        } else {
                            if(key.equals("attachments") || key.equals("messages")) {
                                if(!data.isNull(key)) {
                                    JSONArray obj = data.getJSONArray(key);
                                    Class<?> elementType = key.equals("attachments") ? 
                                            MessageAttachment.class : Message.class;
                                    List<?> list = Api.parseList(obj, elementType);
                                    this.getClass().getMethod(setterName, type)
                                            .invoke(this, list);
                                }
                            } else {
                                throw new ParseException(type.getSimpleName() + " is not supported " +
                                        "for " + key, -1);
                            }
                        }
                    }
                }catch(Exception x) {
                    ErrorHandler.handle(x, "parsing " + key);
                }
            }
        }
    }

    public JSONObject toJson() {
        JSONObject res = new JSONObject();
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field : fields) {
            String key = field.getName();
            try {
                String getterName = getterName(key);
                Object o = this.getClass().getMethod(getterName).invoke(this);
                if(o instanceof Bean bean) {
                    JSONObject obj = bean.toJson();
                    res.put(key, obj);
                } else {
                    res.put(key, o);
                }
            }catch(Exception x) {
                ErrorHandler.handle(x, "parsing " + key);
            }
        }
        return res;
    }

    private String setterName(String key) {
        char c0 = Character.toUpperCase(key.charAt(0));
        return "set" + c0 + key.substring(1);
    }

    private String getterName(String key) {
        char c0 = Character.toUpperCase(key.charAt(0));
        return "get" + c0 + key.substring(1);
    }
}
