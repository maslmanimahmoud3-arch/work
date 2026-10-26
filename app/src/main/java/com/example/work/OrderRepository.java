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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class OrderRepository {

    public interface OnOrdersChangeListener {
        void onOrdersChanged();
    }

    public static final String STATUS_PENDING = "في انتظار موافقة مقدم الخدمة ⏳";
    public static final String STATUS_ACCEPTED = "تم قبول الطلب - قيد التنفيذ ⚙️";
    public static final String STATUS_REJECTED = "تم رفض الطلب ❌";
    public static final String STATUS_COMPLETED = "خدمة مكتملة بنجاح ✅";

    private static final String TAG = "OrderRepository";

    private static OrderRepository instance;
    private final Map<String, OrderItem> ordersMap = new LinkedHashMap<>();
    private final List<OnOrdersChangeListener> listeners = new ArrayList<>();
    private DatabaseReference databaseRef;
    private Context appContext;

    private OrderRepository() {
    }

    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
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
                    databaseRef = db.getReference("orders");
                    attachFirebaseListener();
                }
            } catch (Exception e) {
                Log.e(TAG, "Firebase init error: " + e.getMessage());
            }
        });
    }

    public void addOnOrdersChangeListener(OnOrdersChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnOrdersChangeListener(OnOrdersChangeListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    private void notifyListeners() {
        for (OnOrdersChangeListener listener : new ArrayList<>(listeners)) {
            if (listener != null) {
                listener.onOrdersChanged();
            }
        }
    }

    private void attachFirebaseListener() {
        if (databaseRef == null) return;

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: received " + snapshot.getChildrenCount() + " orders from Firebase");
                synchronized (OrderRepository.this) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        OrderItem item = child.getValue(OrderItem.class);
                        if (item != null && item.getOrderId() != null) {
                            ordersMap.put(item.getOrderId(), item);
                        }
                    }
                }
                notifyListeners();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase cancelled/error: " + error.getMessage());
                if (appContext != null) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(appContext, "خطأ الفايربيس: " + error.getMessage(), Toast.LENGTH_LONG).show()
                    );
                }
            }
        });
    }

    public synchronized List<OrderItem> getAllOrders() {
        return new ArrayList<>(ordersMap.values());
    }

    public void placeOrder(ServiceItem service, String clientName, String clientEmail) {
        placeOrder(service, clientName, clientEmail, null);
    }

    public void placeOrder(ServiceItem service, String clientName, String clientEmail, String clientPhone) {
        String name = clientName != null && !clientName.trim().isEmpty() ? clientName.trim() : "عميل مسجل";
        String email = clientEmail != null && !clientEmail.trim().isEmpty() ? clientEmail.trim() : "client@example.com";
        String phone = clientPhone != null && !clientPhone.trim().isEmpty() ? clientPhone.trim() : "";

        OrderItem order = new OrderItem(
                UUID.randomUUID().toString(),
                service,
                name,
                email,
                phone,
                getCurrentFormattedDate(),
                STATUS_PENDING
        );

        synchronized (this) {
            ordersMap.put(order.getOrderId(), order);
        }
        notifyListeners();

        FirebaseHelper.ensureAuthenticated(() -> {
            try {
                if (databaseRef == null) {
                    FirebaseDatabase db = FirebaseHelper.getDatabase();
                    if (db != null) {
                        databaseRef = db.getReference("orders");
                    }
                }
                if (databaseRef != null) {
                    databaseRef.child(order.getOrderId()).setValue(order, (error, ref) -> {
                        if (error != null) {
                            Log.e(TAG, "Firebase setValue order failed: " + error.getMessage());
                            if (appContext != null) {
                                new Handler(Looper.getMainLooper()).post(() ->
                                        Toast.makeText(appContext, "تنبيه الفايربيس: " + error.getMessage(), Toast.LENGTH_LONG).show()
                                );
                            }
                        } else {
                            Log.d(TAG, "Firebase setValue order succeeded!");
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed pushing order to Firebase: " + e.getMessage());
            }
        });
    }

    public void updateOrderStatus(String orderId, String newStatus) {
        if (orderId == null || newStatus == null) return;

        synchronized (this) {
            OrderItem order = ordersMap.get(orderId);
            if (order != null) {
                order.setStatus(newStatus);
            }
        }
        notifyListeners();

        FirebaseHelper.ensureAuthenticated(() -> {
            try {
                if (databaseRef == null) {
                    FirebaseDatabase db = FirebaseHelper.getDatabase();
                    if (db != null) {
                        databaseRef = db.getReference("orders");
                    }
                }
                if (databaseRef != null) {
                    databaseRef.child(orderId).child("status").setValue(newStatus, (error, ref) -> {
                        if (error != null) {
                            Log.e(TAG, "Firebase updateOrderStatus failed: " + error.getMessage());
                            if (appContext != null) {
                                new Handler(Looper.getMainLooper()).post(() ->
                                        Toast.makeText(appContext, "تنبيه الفايربيس: " + error.getMessage(), Toast.LENGTH_LONG).show()
                                );
                            }
                        } else {
                            Log.d(TAG, "Firebase updateOrderStatus succeeded to: " + newStatus);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed updating order status in Firebase: " + e.getMessage());
            }
        });
    }

    private String getCurrentFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
