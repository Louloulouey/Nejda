package com.sdidsa.nejda.controll.ai;

import com.sdidsa.nejda.view.app_content.session.home.ai.chat.AiMessageRole;

public enum ChatRole {
    MODEL("model", AiMessageRole.MODEL),
    USER("user", AiMessageRole.USER);

    private final String name;
    private final AiMessageRole role;

    private ChatRole(String name, AiMessageRole role) {
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public AiMessageRole getRole() {
        return role;
    }

    public static ChatRole byName(String name) {
        for (ChatRole role : values()) {
            if (role.getName().equalsIgnoreCase(name)) {
                return role;
            }
        }
        return null;
    }
}
