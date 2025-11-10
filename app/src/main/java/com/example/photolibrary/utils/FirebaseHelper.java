package com.example.photolibrary.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Helper class for Firebase Database configuration
 */
public class FirebaseHelper {
    
    // Database URL for your Realtime Database
    private static final String DATABASE_URL = "https://photo-library-347ce-default-rtdb.firebaseio.com/";
    
    private static FirebaseDatabase database;
    
    /**
     * Get Firebase Database instance with the correct URL
     * @return FirebaseDatabase instance
     */
    public static FirebaseDatabase getDatabase() {
        if (database == null) {
            // Use the explicit database URL
            database = FirebaseDatabase.getInstance(DATABASE_URL);
            // Note: Offline persistence can be enabled if needed
            // database.setPersistenceEnabled(true);
        }
        return database;
    }
    
    /**
     * Get reference to the root of the database
     * @return DatabaseReference to root
     */
    public static DatabaseReference getRootReference() {
        return getDatabase().getReference();
    }
    
    /**
     * Get reference to users node
     * @return DatabaseReference to users
     */
    public static DatabaseReference getUsersReference() {
        return getDatabase().getReference("users");
    }
    
    /**
     * Get reference to photos node
     * @return DatabaseReference to photos
     */
    public static DatabaseReference getPhotosReference() {
        return getDatabase().getReference("photos");
    }
}

