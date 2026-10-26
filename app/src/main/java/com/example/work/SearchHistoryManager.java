package com.example.work;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchHistoryManager {

    private static final String PREF_NAME = "work_search_history";
    private static final String KEY_HISTORY = "history_items";

    public static void addSearchQuery(Context context, String query, double budget) {
        String entry;
        String trimmed = query != null ? query.trim() : "";
        if (trimmed.isEmpty()) {
            if (budget <= 0) return;
            entry = "ميزانية بـ $" + ((int) budget);
        } else if (budget > 0) {
            entry = trimmed + " (بحدود $" + ((int) budget) + ")";
        } else {
            entry = trimmed;
        }

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(KEY_HISTORY, new HashSet<>());
        Set<String> newSet = new HashSet<>(set);
        newSet.add(entry);

        prefs.edit().putStringSet(KEY_HISTORY, newSet).apply();
    }

    public static List<String> getSearchHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(KEY_HISTORY, new HashSet<>());
        return new ArrayList<>(set);
    }

    public static void clearHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_HISTORY).apply();
    }
}
