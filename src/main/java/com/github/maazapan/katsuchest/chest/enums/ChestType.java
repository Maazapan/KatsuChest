package com.github.maazapan.katsuchest.chest.enums;

public enum ChestType {

    KEY_CHEST ("Key Chest"),
    PANEL_CHEST("Panel Chest"),
    FRIEND_CHEST("Friend Chest");

    private final String name;

    ChestType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ChestType getByName(String name) {
        for (ChestType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
