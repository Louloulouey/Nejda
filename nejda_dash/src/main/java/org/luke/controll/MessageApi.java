package org.luke.controll;

import org.luke.controll.abs.Api;
import org.luke.controll.abs.ApiRequest;
import org.luke.model.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.function.Consumer;

public class MessageApi extends Api {
    
    public static void getMessagesByThread(Long threadId, Consumer<List<Message>> onResponse) {
        new ApiRequest("/messages/all/" + threadId, "POST")
            .setOnResponse(response -> {
                onResponse.accept(parseList(response, Message.class));
            })
            .execute();
    }

    public static void getMessageById(Long id, Consumer<Message> onResponse) {
        new ApiRequest("/messages/" + id, "GET")
            .setOnResponse(r -> onResponse.accept(new Message(r)))
            .execute();
    }

    public static void createMessage(Message message, Consumer<Message> onResponse) {
        new ApiRequest("/messages/create/" + message.getThread().getId(), "POST")
                .setBody(message)
            .setOnResponse(r -> onResponse.accept(new Message(r)))
            .execute();
    }

    public static void updateMessage(Long id, String text, Consumer<JSONObject> onResponse) {
        new ApiRequest("/messages/" + id, "PUT")
            .addBodyParam("text", text)
            .setOnResponse(onResponse)
            .execute();
    }

    public static void deleteMessage(Long id, Consumer<JSONObject> onResponse) {
        new ApiRequest("/messages/" + id, "DELETE")
            .setOnResponse(onResponse)
            .execute();
    }
} 