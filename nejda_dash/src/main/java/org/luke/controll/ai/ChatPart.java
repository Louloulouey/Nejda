package org.luke.controll.ai;


import org.json.JSONObject;
import org.luke.gui.exception.ErrorHandler;

public class ChatPart {
    private final ChatRole role;
    private final String content;

    public ChatRole getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public ChatPart(ChatRole role, String content) {
        this.role = role;
        this.content = content;
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("role", role.toString());
            obj.put("content", content);
        }catch (Exception x) {
            ErrorHandler.handle(x, "save chat part");
        }
        return obj;
    }

    public static ChatPart fromJson(JSONObject obj) {
        try {
            return new ChatPart(ChatRole.byName(obj.getString("role")), obj.getString("content"));
        }catch (Exception x) {
            ErrorHandler.handle(x, "load chat part");
            return null;
        }
    }
}
