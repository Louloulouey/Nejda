package org.luke.controll.ai;

public enum ChatRole {
    MODEL("model"),
    USER("user");

    private final String name;

    private ChatRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
