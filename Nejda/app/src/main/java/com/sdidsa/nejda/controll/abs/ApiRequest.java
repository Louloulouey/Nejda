package com.sdidsa.nejda.controll.abs;

import com.sdidsa.nejda.BuildConfig;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.model.Bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ApiRequest {

    private static final String BASE_URL = "http://" + BuildConfig.LOCAL_IP + ":8080/api";
    private final String path;
    private String method;
    private JSONObject body;
    private final Map<String, String> queryParams;

    private Consumer<JSONObject> onResponse;

    public ApiRequest(String path) {
        this(path, "POST");
    }

    public ApiRequest(String path, Consumer<JSONObject> onResponse) {
        this(path, "POST", new JSONObject(), onResponse);
    }

    public ApiRequest(String path, String method) {
        this(path, method, new JSONObject());
    }

    public ApiRequest(String path, String method, JSONObject body) {
        this(path, method, body, null);
    }

    public ApiRequest(String path, String method, JSONObject body, Consumer<JSONObject> onResponse) {
        this.path = path;
        this.method = method;
        this.body = body;
        this.queryParams = new HashMap<>();
        this.onResponse = onResponse;
    }

    public ApiRequest setMethod(String method) {
        this.method = method;
        return this;
    }

    public ApiRequest setBody(JSONObject body) {
        this.body = body;
        return this;
    }

    public ApiRequest setBody(Bean body) {
        this.body = body.toJson();
        return this;
    }

    public ApiRequest setOnResponse(Consumer<JSONObject> onResponse) {
        this.onResponse = onResponse;
        return this;
    }

    public ApiRequest addBodyParam(String key, Object value) {
        try {
            body.put(key, value);
        } catch (JSONException e) {
            ErrorHandler.handle(e, "add body param to " + path);
        }
        return this;
    }

    public ApiRequest addBodyParam(BodyParam param) {
        return addBodyParam(param.getKey(), param.getValue());
    }

    public ApiRequest addBodyParams(BodyParam...params) {
        for(BodyParam param: params) {
            addBodyParam(param);
        }
        return this;
    }
    
    public ApiRequest addQueryParam(String key, String value) {
        queryParams.put(key, value);
        return this;
    }

    public void execute() {
        execute(BASE_URL);
    }

    public void execute(String baseUrl) {
        Platform.runBack(() -> {
            try {
                StringBuilder urlString = new StringBuilder(baseUrl + path);
                if (!queryParams.isEmpty()) {
                    urlString.append("?");
                    boolean first = true;
                    for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                        if (!first) {
                            urlString.append("&");
                        }
                        urlString.append(entry.getKey()).append("=").append(entry.getValue());
                        first = false;
                    }
                }
                
                URL url = new URI(urlString.toString()).toURL();
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod(method);
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                con.setDoInput(true);
                String json = body.toString();

                try (OutputStream os = con.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }
                try (InputStream is = con.getInputStream()) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = br.readLine()) != null) {
                        response.append(line).append(System.lineSeparator());
                    }

                    if(onResponse != null) {
                        Platform.sleep(1000);
                        Platform.runLater(() -> {
                            try {
                                String resp = response.toString().trim();
                                System.out.println(resp);
                                if(resp.startsWith("[")) {
                                    JSONObject data = new JSONObject();
                                    data.put("array", new JSONArray(resp));
                                    onResponse.accept(data);
                                } else if(resp.startsWith("{")) {
                                    JSONObject obj = new JSONObject(response.toString());
                                    onResponse.accept(obj);
                                } else {
                                    onResponse.accept(new JSONObject());
                                }
                            }catch(Exception x) {
                                ErrorHandler.handle(x, "parse response to json");
                                onResponse.accept(new JSONObject());
                            }
                        });
                    }
                }
            }catch(Exception x) {
                ErrorHandler.handle(x, "send http request to server");
            }
        });
    }

}
