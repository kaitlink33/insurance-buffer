package com.insurancebuffer.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ConversationLog - Represents a single conversation turn or full session log.
 * Demonstrates: OOP encapsulation, inner class (MessageEntry), List usage.
 * CIS200 Honors - Java Backend
 */
public class ConversationLog {

    private String sessionId;
    private List<MessageEntry> messages;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean completed;

    /**
     * Inner class representing a single message in the conversation.
     * Demonstrates: nested classes in Java OOP.
     */
    public static class MessageEntry {
        private String role;     // "agent" or "user"
        private String content;
        private LocalDateTime timestamp;

        public MessageEntry(String role, String content) {
            this.role = role;
            this.content = content;
            this.timestamp = LocalDateTime.now();
        }

        public String getRole() { return role; }
        public String getContent() { return content; }
        public LocalDateTime getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return "[" + timestamp + "] " + role.toUpperCase() + ": " + content;
        }
    }

    public ConversationLog(String sessionId) {
        this.sessionId = sessionId;
        this.messages = new ArrayList<>();
        this.startTime = LocalDateTime.now();
        this.completed = false;
    }

    public void addMessage(String role, String content) {
        messages.add(new MessageEntry(role, content));
    }

    public void complete() {
        this.completed = true;
        this.endTime = LocalDateTime.now();
    }

    public String getFullTranscript() {
        StringBuilder sb = new StringBuilder();
        for (MessageEntry entry : messages) {
            sb.append(entry.toString()).append("\n");
        }
        return sb.toString();
    }

    // Getters
    public String getSessionId() { return sessionId; }
    public List<MessageEntry> getMessages() { return messages; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public boolean isCompleted() { return completed; }
}
