package com.github.maazapan.katsuchest.chest.enums;

public enum ChestType {

    KEY_CHEST,
    PANEL_CHEST,
    CUSTOM;


    public static ChestType getByName(String name) {
        for (ChestType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
