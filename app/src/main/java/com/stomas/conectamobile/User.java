package com.stomas.conectamobile;


public class User {
    private String userId;
    private String email;

    public User() {
        // Constructor vacío requerido por Firestore
    }

    public User(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}

