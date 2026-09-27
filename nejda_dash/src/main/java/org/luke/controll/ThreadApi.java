package org.luke.controll;

import org.luke.controll.abs.Api;
import org.luke.controll.abs.ApiRequest;
import org.luke.model.Thread;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ThreadApi extends Api {
    
    public static void getAllThreads(Consumer<List<Thread>> onResponse) {
        getAllThreads(3, onResponse);
    }
    
    public static void getAllThreads(int recentMessagesCount, Consumer<List<Thread>> onResponse) {
        new ApiRequest("/threads/all", "POST")
            .addQueryParam("recentMessagesCount", String.valueOf(recentMessagesCount))
            .setOnResponse(resp -> {
                List<Thread> threads = parseList(resp, Thread.class);
                threads.forEach(t -> Collections.reverse(t.getMessages()));
                onResponse.accept(threads);
            })
            .execute();
    }

    public static void getThreadById(Long id, Consumer<Thread> onResponse) {
        new ApiRequest("/threads/" + id, "GET")
            .setOnResponse(resp -> {
                onResponse.accept(new Thread(resp));
            })
            .execute();
    }

    public static void createThread(String title, Consumer<JSONObject> onResponse) {
        new ApiRequest("/threads/create", "POST")
            .addBodyParam("title", title)
            .setOnResponse(onResponse)
            .execute();
    }

    public static void updateThread(Long id, String title, Consumer<JSONObject> onResponse) {
        new ApiRequest("/threads/" + id, "PUT")
            .addBodyParam("title", title)
            .setOnResponse(onResponse)
            .execute();
    }

    public static void deleteThread(Long id, Consumer<JSONObject> onResponse) {
        new ApiRequest("/threads/" + id, "DELETE")
            .setOnResponse(onResponse)
            .execute();
    }
} 