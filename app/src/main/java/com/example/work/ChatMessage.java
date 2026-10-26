package com.example.work;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private String messageId;
    private String chatId;
    private String senderName;
    private String senderRole; // "client" or "provider"
    private String text;
    private long timestamp;
    private String timeFormatted;

    public ChatMessage() {
    }

    public ChatMessage(String messageId, String chatId, String senderName, String senderRole, String text, long timestamp, String timeFormatted) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.text = text;
        this.timestamp = timestamp;
        this.timeFormatted = timeFormatted;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeFormatted() {
        return timeFormatted;
    }

    public void setTimeFormatted(String timeFormatted) {
        this.timeFormatted = timeFormatted;
    }
}
