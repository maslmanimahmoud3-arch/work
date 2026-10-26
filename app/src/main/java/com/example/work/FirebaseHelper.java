package com.example.work;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private static final String DB_URL_US = "https://work-4451a-default-rtdb.firebaseio.com";
    private static final String DB_URL_EU = "https://work-4451a-default-rtdb.europe-west1.firebasedatabase.app";
    private static final String DB_URL_ASIA = "https://work-4451a-default-rtdb.asia-southeast1.firebasedatabase.app";
    private static final String DB_URL_ALT = "https://work-4451a.firebaseio.com";

    private static FirebaseDatabase databaseInstance;

    public interface OnAuthCompleteListener {
        void onAuthSuccess();
    }

    public static synchronized FirebaseDatabase getDatabase() {
        if (databaseInstance == null) {
            String[] urls = new String[]{DB_URL_US, DB_URL_EU, DB_URL_ASIA, DB_URL_ALT};
            for (String url : urls) {
                try {
                    FirebaseDatabase db = FirebaseDatabase.getInstance(url);
                    if (db != null) {
                        databaseInstance = db;
                        break;
                    }
                } catch (Exception ignored) {
                }
            }

            if (databaseInstance == null) {
                try {
                    databaseInstance = FirebaseDatabase.getInstance();
                } catch (Exception e) {
                    Log.e(TAG, "Error getting default FirebaseDatabase instance: " + e.getMessage());
                }
            }

            if (databaseInstance != null) {
                try {
                    databaseInstance.setPersistenceEnabled(true);
                } catch (Exception ignored) {
                }
            }
        }
        return databaseInstance;
    }

    public static void ensureAuthenticated(OnAuthCompleteListener listener) {
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                if (listener != null) listener.onAuthSuccess();
                return;
            }

            auth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Signed in anonymously to Firebase Auth successfully!");
                } else {
                    Log.e(TAG, "Anonymous sign in failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown"));
                }
                if (listener != null) listener.onAuthSuccess();
            });
        } catch (Exception e) {
            Log.e(TAG, "Auth error: " + e.getMessage());
            if (listener != null) listener.onAuthSuccess();
        }
    }
}
