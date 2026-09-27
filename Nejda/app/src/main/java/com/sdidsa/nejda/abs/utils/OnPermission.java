package com.sdidsa.nejda.abs.utils;

public class OnPermission {
    private final Runnable onPermission;
    private final boolean or;

    public OnPermission(Runnable onPermission, boolean or) {
        this.onPermission = onPermission;
        this.or = or;
    }

    public OnPermission(Runnable onPermission) {
        this(onPermission, false);
    }

    public boolean isOr() {
        return or;
    }

    public Runnable getOnPermission() {
        return onPermission;
    }
}
