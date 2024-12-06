package com.stomas.conectamobile;

public class Message {
    private String userId;
    private String email; // Añadir el correo electrónico del usuario
    private String message;
    private long timestamp;

    // Constructor vacío requerido por Firestore
    public Message() {}

    public Message(String userId, String email, String message, long timestamp) {
        this.userId = userId;
        this.email = email;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters y Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
