package com.example.textguard2;

import java.util.List;
import java.util.Map;

public class Logs {
    private Map<String, List<LogEntry>> logs;

    public Logs(Map<String, List<LogEntry>> logs) {
        this.logs = logs;
    }

    public Map<String, List<LogEntry>> getLogs() {
        return logs;
    }

    public void setLogs(Map<String, List<LogEntry>> logs) {
        this.logs = logs;
    }
}
