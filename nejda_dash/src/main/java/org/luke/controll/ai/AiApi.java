package org.luke.controll.ai;


import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.luke.gui.exception.ErrorHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class AiApi {
    public static void sendGeminiRequest(ArrayList<ChatPart> conversation,
                                         Consumer<AiResponse> onResponse) {
        sendGeminiRequest(conversation, onResponse, null);
    }

    public static void sendGeminiRequest(ArrayList<ChatPart> conversation,
                                         Consumer<AiResponse> onResponse,
                                         Consumer<String> onError) {
        long startTime = System.currentTimeMillis();
        long timeout = 5000;
        AtomicBoolean success = new AtomicBoolean(false);
        AtomicBoolean canceled = new AtomicBoolean(false);
        new Thread(() -> {
            try {
                Thread.sleep(500);
                URL url = new URL("https://generativelanguage.googleapis.com/" +
                        "v1beta/models/gemini-2.0-flash:generateContent" +
                        "?key=" + System.getenv("GEMINI_API_KEY"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONArray contents = new JSONArray();
                contents.put(make(ChatRole.USER,
                        "You are 'NejdAi', a helpful AI safety assistant for students at " +
                                "University constantine 2 - Computer science department, " +
                                "You were made by Kerouaz mohamed louay as part of the " +
                                "Nejda app for Campus safety and emergency prevention, " +
                                "Your primary goal is to provide safety help " +
                                "and tips concerning emergencies or natural catastrophes, " +
                                "Answer the student's question regarding safety " +
                                "procedures or provide relevant physical safety " +
                                "tips. When suggesting a safety action or tool, always provide a brief, " +
                                "helpful text message explaining why the action is being suggested, " +
                                "followed by the relevant tool call. Do not just return a tool call " +
                                "without any accompanying text."));
                for (int i = 0; i < conversation.size(); i++) {
                    ChatPart part = conversation.get(i);
                    contents.put(make(part.getRole(), part.getContent()));
                }
                JSONArray tools = new JSONArray().put(new JSONObject()
                        .put("functionDeclarations", AiFunction.toJsonArray()));
                JSONObject body = new JSONObject()
                        .put("tools", tools)
                        .put("contents", contents);


                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body.toString().getBytes(StandardCharsets.UTF_8));
                }

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,
                        StandardCharsets.UTF_8));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject data = new JSONObject(result.toString());
                JSONArray parts = data
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts");

                AiResponse response = new AiResponse();
                for (int i = 0; i < parts.length(); i++) {
                    JSONObject part = parts.getJSONObject(i);
                    if (part.has("text")) {
                        response.addText(part.getString("text").trim());
                    }

                    if (part.has("functionCall")) {
                        JSONObject functionCall = part.getJSONObject("functionCall");
                        JSONObject args = functionCall.getJSONObject("args");
                        response.addFunction(
                                functionCall
                                        .getString("name").trim());

                        args.keys().forEachRemaining(key -> {
                            try {
                                Object value = args.get(key);
                                response.putArg(key, value);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                }

                if (!canceled.get()) {
                    Platform.runLater(() -> onResponse.accept(response));
                    success.set(true);
                }

            } catch (Exception e) {
                ErrorHandler.handle(e, "send direct Gemini request");
                if (onError != null && !canceled.get()) {
                    Platform.runLater(() -> onError.accept(e.getMessage()));
                    canceled.set(true);
                }
            }
        }).start();
        new Thread(() -> {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime < timeout) {
                try {
                    Thread.sleep(timeout - elapsedTime);
                } catch (InterruptedException e) {
                    ErrorHandler.handle(e, "send direct Gemini request");
                }
            }
            if (!success.get() && !canceled.get() && onError != null) {
                canceled.set(true);
                Platform.runLater(() -> onError.accept("Request timed out"));
            }
        }).start();
    }

    public static void estimateChatTitle(ArrayList<ChatPart> conversation,
                                         Consumer<String> onTitle) {
        ArrayList<ChatPart> clone = new ArrayList<>(conversation);
        clone.add(new ChatPart(ChatRole.USER, "suggest a title for this conversation, " +
                "don't include any other text or explanation, just one suggested title if applicable"));

        sendGeminiRequest(clone, response -> {
            if(!response.getTexts().isEmpty()) {
                onTitle.accept(response.getTexts().get(0));
            }
        });
    }

    public static JSONObject make(ChatRole role, String text) throws JSONException {
        return new JSONObject()
                .put("role", role.getName())
                .put("parts", new JSONArray()
                        .put(new JSONObject().put("text", text)));
    }
}
