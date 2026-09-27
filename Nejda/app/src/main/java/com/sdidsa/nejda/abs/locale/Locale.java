package com.sdidsa.nejda.abs.locale;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import com.sdidsa.nejda.abs.utils.Assets;
import com.sdidsa.nejda.abs.utils.ErrorHandler;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class Locale {
	private final static ConcurrentHashMap<String, Locale> cache = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> values;
	private final String name;
	private final boolean rtl;
	public Locale(Context owner, String name) {
		this(owner, name, false);
	}

	public Locale(Context owner, String name, boolean rtl) {
		this.name = name.toLowerCase();
		this.rtl = rtl;
		cache.put(name.toLowerCase(), this);
		values = new ConcurrentHashMap<>();
		String file = Assets.readAsset(owner, "locales/".concat(name).concat(".json"));
		try {
			assert file != null;
			JSONObject obj = new JSONObject(file);

			Iterator<String> keys = obj.keys();
			while(keys.hasNext()) {
				String key = keys.next();
				values.put(key, obj.getString(key));
			}
		}catch(JSONException x) {
			ErrorHandler.handle(x, "reading locale".concat(name));
		}
	}

	public String get(String key) {
		if(key.isEmpty()) return key;
		String found = values.get(key);

		if (found == null) {
			found = key;
			//ErrorHandler.handle(new RuntimeException("Missing Key From Locale"), "getting value of key [" + key + "] for locale [" + name + "]");
		}

		return found;
	}

	public String getName() {
		return name;
	}

	public String getLang() {
		return name.split("_")[0];
	}

	public boolean isRtl() {
		return rtl;
	}

	public int getDirection() {
		return isRtl() ? -1 : 1;
	}

	public static Locale forName(Context context, String name) {
		Locale found = cache.get(name.toLowerCase());
		if(found == null) {
			String[] parts = name.split("_");
			found = new Locale(context, parts[0].toLowerCase() + "_" +
					parts[1].toUpperCase());
		}
		return found;
	}
}
