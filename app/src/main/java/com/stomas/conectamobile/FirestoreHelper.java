package com.stomas.conectamobile;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreHelper {
    private static FirebaseFirestore db;

    // Método para obtener una instancia única de Firestore
    public static FirebaseFirestore getFirestoreInstance() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }
}
