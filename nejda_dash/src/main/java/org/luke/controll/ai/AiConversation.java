package org.luke.controll.ai;

import org.json.JSONArray;
import org.json.JSONObject;
import org.luke.gui.exception.ErrorHandler;

import java.util.ArrayList;

public class AiConversation {
    private long id;
    private final String title;
    private final ArrayList<ChatPart> chatParts;

    public AiConversation(long id, String title, ArrayList<ChatPart> chatParts) {
        this.id = id;
        this.title = title;
        this.chatParts = chatParts;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<ChatPart> getChatParts() {
        return chatParts;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void addChatPart(ChatPart cp) {
        chatParts.add(cp);
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("id", id);
            obj.put("title", title);
            JSONArray arr = new JSONArray();

            for (ChatPart cp : chatParts) {
                arr.put(cp.toJson());
            }

            obj.put("chatParts", arr);
        }catch (Exception x) {
            ErrorHandler.handle(x, "save ai conversation");
        }

        return obj;
    }

    public static AiConversation fromJson(JSONObject obj) {
        try {
            long id = obj.getLong("id");
            String title = null;
            if(obj.has("title")) {
                title = obj.getString("title");
            }
            JSONArray arr = obj.getJSONArray("chatParts");
            ArrayList<ChatPart> chatParts = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                chatParts.add(ChatPart.fromJson(arr.getJSONObject(i)));
            }
            return new AiConversation(id, title, chatParts);
        }catch (Exception x) {
            ErrorHandler.handle(x, "load ai conversation");
            return null;
        }
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
