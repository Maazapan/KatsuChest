package com.github.maazapan.katsuchest.utils.file.enums;

public enum FileType {

    CONFIG("config.yml"),
    MESSAGES("messages.yml");

    private final String fileName;

    FileType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
