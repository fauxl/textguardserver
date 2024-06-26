package com.example.textguard2;

public class LogEntry {
    private String timestamp;
    private String app;
    private String title;
    private String content;
    private String priority;

    public LogEntry(String timestamp, String app, String title, String content, String priority) {
        this.timestamp = timestamp;
        this.app = app;
        this.title = title;
        this.content = content;
        this.priority = priority;
    }

    // Getters e setters
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
