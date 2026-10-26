package com.example.work;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServiceRepository {

    public interface OnDataChangeListener {
        void onDataChanged();
    }

    private static final String TAG = "ServiceRepository";

    private static ServiceRepository instance;
    private final Map<String, ServiceItem> servicesMap = new LinkedHashMap<>();
    private final List<OnDataChangeListener> listeners = new ArrayList<>();
    private DatabaseReference databaseRef;
    private Context appContext;

    private ServiceRepository() {
    }

    public static synchronized ServiceRepository getInstance() {
        if (instance == null) {
            instance = new ServiceRepository();
        }
        return instance;
    }

    public void init(Context context) {
        if (context != null) {
            this.appContext = context.getApplicationContext();
        }
        FirebaseHelper.ensureAuthenticated(() -> {
            try {
                FirebaseDatabase db = FirebaseHelper.getDatabase();
                if (db != null) {
                    databaseRef = db.getReference("services");
                    attachFirebaseListener();
                }
            } catch (Exception e) {
                Log.e(TAG, "Firebase init error: " + e.getMessage());
            }
        });
    }

    public void addOnDataChangeListener(OnDataChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnDataChangeListener(OnDataChangeListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    private void notifyListeners() {
        for (OnDataChangeListener listener : new ArrayList<>(listeners)) {
            if (listener != null) {
                listener.onDataChanged();
            }
        }
    }

    private void attachFirebaseListener() {
        if (databaseRef == null) return;

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: received " + snapshot.getChildrenCount() + " items from Firebase");
                synchronized (ServiceRepository.this) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        ServiceItem item = child.getValue(ServiceItem.class);
                        if (item != null && item.getId() != null) {
                            servicesMap.put(item.getId(), item);
                        }
                    }
                }
                notifyListeners();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase cancelled/error: " + error.getMessage() + " (code: " + error.getCode() + ")");
                if (appContext != null) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(appContext, "خطأ الفايربيس: " + error.getMessage(), Toast.LENGTH_LONG).show()
                    );
                }
            }
        });
    }

    public synchronized List<ServiceItem> getAllServices() {
        return new ArrayList<>(servicesMap.values());
    }

    public void addService(ServiceItem service) {
        if (service == null || service.getId() == null) return;

        // Immediately store in local memory for instant UI display
        synchronized (this) {
            servicesMap.put(service.getId(), service);
        }
        notifyListeners();

        // Push to Firebase Realtime Database
        FirebaseHelper.ensureAuthenticated(() -> {
            try {
                if (databaseRef == null) {
                    FirebaseDatabase db = FirebaseHelper.getDatabase();
                    if (db != null) {
                        databaseRef = db.getReference("services");
                    }
                }
                if (databaseRef != null) {
                    databaseRef.child(service.getId()).setValue(service, (error, ref) -> {
                        if (error != null) {
                            Log.e(TAG, "Firebase setValue failed: " + error.getMessage() + " (code " + error.getCode() + ")");
                            if (appContext != null) {
                                new Handler(Looper.getMainLooper()).post(() ->
                                        Toast.makeText(appContext, "تنبيه الفايربيس: " + error.getMessage(), Toast.LENGTH_LONG).show()
                                );
                            }
                        } else {
                            Log.d(TAG, "Firebase setValue succeeded for service: " + service.getTitle());
                            if (appContext != null) {
                                new Handler(Looper.getMainLooper()).post(() ->
                                        Toast.makeText(appContext, "تم حفظ الخدمة بنجاح في السحابة ☁️", Toast.LENGTH_SHORT).show()
                                );
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed pushing service to Firebase: " + e.getMessage());
            }
        });
    }

    public synchronized List<ServiceItem> searchServices(String query, double targetPrice, String category) {
        List<ServiceItem> filtered = new ArrayList<>();
        String q = query != null ? query.trim().toLowerCase() : "";

        for (ServiceItem item : servicesMap.values()) {
            boolean matchesQuery = q.isEmpty() ||
                    (item.getTitle() != null && item.getTitle().toLowerCase().contains(q)) ||
                    (item.getDescription() != null && item.getDescription().toLowerCase().contains(q)) ||
                    (item.getCategory() != null && item.getCategory().toLowerCase().contains(q));

            boolean matchesCategory = category == null ||
                    category.isEmpty() ||
                    category.equalsIgnoreCase("الكل") ||
                    (item.getCategory() != null && item.getCategory().equalsIgnoreCase(category));

            boolean matchesPriceRange;
            if (targetPrice > 0) {
                double diff = Math.abs(item.getPrice() - targetPrice);
                double maxAllowedDiff = Math.max(25.0, targetPrice * 0.40);
                matchesPriceRange = diff <= maxAllowedDiff;
            } else {
                matchesPriceRange = true;
            }

            if (matchesQuery && matchesCategory && matchesPriceRange) {
                filtered.add(item);
            }
        }

        if (targetPrice > 0) {
            filtered.sort((o1, o2) -> {
                double diff1 = Math.abs(o1.getPrice() - targetPrice);
                double diff2 = Math.abs(o2.getPrice() - targetPrice);
                return Double.compare(diff1, diff2);
            });
        }

        return filtered;
    }
}
