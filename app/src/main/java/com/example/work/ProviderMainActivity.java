package com.example.work;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class ProviderMainActivity extends AppCompatActivity {

    private RecyclerView rvProviderServices;
    private RecyclerView rvIncomingOrders;
    private TextView tvEmptyPosted;
    private TextView tvProviderHeader;
    private TextView tvPostedServicesCount;
    private TextView tvSectionTitle;
    private MaterialButton btnTabMyServices;
    private MaterialButton btnTabIncomingOrders;

    private ServiceAdapter serviceAdapter;
    private OrderAdapter orderAdapter;
    private final ServiceRepository repository = ServiceRepository.getInstance();
    private final OrderRepository orderRepository = OrderRepository.getInstance();

    private boolean isOrdersTabActive = false;

    private final ServiceRepository.OnDataChangeListener dataChangeListener = () -> runOnUiThread(this::loadData);
    private final OrderRepository.OnOrdersChangeListener ordersChangeListener = () -> runOnUiThread(this::loadData);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_main);

        repository.init(this);
        orderRepository.init(this);

        initViews();
        setupUserHeader();
        setupRecyclerViews();

        repository.addOnDataChangeListener(dataChangeListener);
        orderRepository.addOnOrdersChangeListener(ordersChangeListener);

        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        repository.removeOnDataChangeListener(dataChangeListener);
        orderRepository.removeOnOrdersChangeListener(ordersChangeListener);
    }

    private void initViews() {
        tvProviderHeader = findViewById(R.id.tvProviderHeader);
        tvPostedServicesCount = findViewById(R.id.tvPostedServicesCount);
        tvSectionTitle = findViewById(R.id.tvSectionTitle);
        rvProviderServices = findViewById(R.id.rvProviderServices);
        rvIncomingOrders = findViewById(R.id.rvIncomingOrders);
        tvEmptyPosted = findViewById(R.id.tvEmptyPosted);

        btnTabMyServices = findViewById(R.id.btnTabMyServices);
        btnTabIncomingOrders = findViewById(R.id.btnTabIncomingOrders);

        MaterialButton btnSwitchRole = findViewById(R.id.btnSwitchRole);
        MaterialButton btnAddService = findViewById(R.id.btnAddService);

        btnSwitchRole.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderMainActivity.this, RoleSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnAddService.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderMainActivity.this, AddServiceActivity.class);
            startActivity(intent);
        });

        btnTabMyServices.setOnClickListener(v -> activateServicesTab());
        btnTabIncomingOrders.setOnClickListener(v -> activateOrdersTab());
    }

    private void activateServicesTab() {
        isOrdersTabActive = false;
        btnTabMyServices.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.brand_primary)));
        btnTabMyServices.setTextColor(getColor(R.color.white));

        btnTabIncomingOrders.setBackgroundTintList(ColorStateList.valueOf(getColor(android.R.color.transparent)));
        btnTabIncomingOrders.setTextColor(getColor(R.color.text_secondary));

        if (tvSectionTitle != null) tvSectionTitle.setText("كافة الخدمات المطروحة بالسوق 🌐");
        rvProviderServices.setVisibility(View.VISIBLE);
        rvIncomingOrders.setVisibility(View.GONE);

        loadData();
    }

    private void activateOrdersTab() {
        isOrdersTabActive = true;
        btnTabIncomingOrders.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.brand_primary)));
        btnTabIncomingOrders.setTextColor(getColor(R.color.white));

        btnTabMyServices.setBackgroundTintList(ColorStateList.valueOf(getColor(android.R.color.transparent)));
        btnTabMyServices.setTextColor(getColor(R.color.text_secondary));

        if (tvSectionTitle != null) tvSectionTitle.setText("الطلبات الواردة لخدماتي 📥");
        rvProviderServices.setVisibility(View.GONE);
        rvIncomingOrders.setVisibility(View.VISIBLE);

        loadData();
    }

    private void setupUserHeader() {
        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName != null && !userName.trim().isEmpty()) {
            tvProviderHeader.setText(String.format("لوحة %s 🛠️", userName.trim()));
        }
    }

    private void setupRecyclerViews() {
        rvProviderServices.setLayoutManager(new LinearLayoutManager(this));
        serviceAdapter = new ServiceAdapter(service -> {
            Intent intent = new Intent(ProviderMainActivity.this, ServiceDetailActivity.class);
            intent.putExtra("SERVICE_ITEM", service);
            startActivity(intent);
        });
        rvProviderServices.setAdapter(serviceAdapter);

        rvIncomingOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(true, new OrderAdapter.OnOrderActionListener() {
            @Override
            public void onOrderContactClick(OrderItem order) {
                String phone = order.getClientPhone();
                if (phone != null && !phone.trim().isEmpty()) {
                    openWhatsAppOrDial(order, phone.trim());
                } else {
                    final android.widget.EditText input = new android.widget.EditText(ProviderMainActivity.this);
                    input.setHint("ادخل رقم هاتف العميل (مثال: 0791234567)");
                    input.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
                    input.setPadding(32, 24, 32, 24);

                    new androidx.appcompat.app.AlertDialog.Builder(ProviderMainActivity.this)
                            .setTitle("التواصل مع العميل: " + (order.getClientName() != null ? order.getClientName() : "عميل"))
                            .setMessage("أدخل رقم هاتف العميل لمراسلته عبر الواتساب مباشرة:")
                            .setView(input)
                            .setPositiveButton("مراسلة عبر الواتساب 💬", (dialog, which) -> {
                                String enteredPhone = input.getText().toString().trim();
                                if (!enteredPhone.isEmpty()) {
                                    order.setClientPhone(enteredPhone);
                                    orderRepository.updateOrderStatus(order.getOrderId(), order.getStatus() != null ? order.getStatus() : OrderRepository.STATUS_PENDING);
                                    openWhatsAppOrDial(order, enteredPhone);
                                } else {
                                    Toast.makeText(ProviderMainActivity.this, "يرجى كتابة رقم هاتف صحيح للمراسلة", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("إلغاء", null)
                            .show();
                }
            }

            @Override
            public void onInAppChatClick(OrderItem order) {
                Intent intent = new Intent(ProviderMainActivity.this, ChatActivity.class);
                intent.putExtra("CHAT_ID", order.getOrderId());
                intent.putExtra("RECEIVER_NAME", order.getClientName() != null ? order.getClientName() : "العميل");
                intent.putExtra("SERVICE_TITLE", order.getServiceItem() != null ? order.getServiceItem().getTitle() : "طلب خدمة");
                intent.putExtra("CURRENT_ROLE", "provider");
                intent.putExtra("CURRENT_USER_NAME", order.getServiceItem() != null ? order.getServiceItem().getProviderName() : "مقدم الخدمة");
                startActivity(intent);
            }

            @Override
            public void onAcceptOrder(OrderItem order) {
                orderRepository.updateOrderStatus(order.getOrderId(), OrderRepository.STATUS_ACCEPTED);
                Toast.makeText(ProviderMainActivity.this, "تم قبول الطلب بنجاح! جاري البدء بالتنفيذ ⚙️", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRejectOrder(OrderItem order) {
                orderRepository.updateOrderStatus(order.getOrderId(), OrderRepository.STATUS_REJECTED);
                Toast.makeText(ProviderMainActivity.this, "تم رفض الطلب ❌", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompleteOrder(OrderItem order) {
                orderRepository.updateOrderStatus(order.getOrderId(), OrderRepository.STATUS_COMPLETED);
                Toast.makeText(ProviderMainActivity.this, "تم إكمال وتسليم الخدمة بنجاح! 🏁", Toast.LENGTH_SHORT).show();
            }
        });
        rvIncomingOrders.setAdapter(orderAdapter);
    }

    private void loadData() {
        List<ServiceItem> services = repository.getAllServices();
        List<OrderItem> orders = orderRepository.getAllOrders();

        int completedOrdersCount = 0;
        for (OrderItem o : orders) {
            if (o.getStatus() != null && o.getStatus().contains("مكتملة")) {
                completedOrdersCount++;
            }
        }

        if (tvPostedServicesCount != null) {
            if (isOrdersTabActive) {
                tvPostedServicesCount.setText(String.format(Locale.getDefault(), "%d طلبات واردة (%d مكتملة)", orders.size(), completedOrdersCount));
            } else {
                tvPostedServicesCount.setText(String.format(Locale.getDefault(), "%d خدمة متاحة بالسوق | %d طلب مكتمل", services.size(), completedOrdersCount));
            }
        }

        if (isOrdersTabActive) {
            if (orders.isEmpty()) {
                tvEmptyPosted.setText("لا توجد طلبات واردة من العملاء حتى الآن 📥");
                tvEmptyPosted.setVisibility(View.VISIBLE);
                rvIncomingOrders.setVisibility(View.GONE);
            } else {
                tvEmptyPosted.setVisibility(View.GONE);
                rvIncomingOrders.setVisibility(View.VISIBLE);
                orderAdapter.setOrders(orders);
            }
        } else {
            if (services.isEmpty()) {
                tvEmptyPosted.setText(R.string.no_posted_services);
                tvEmptyPosted.setVisibility(View.VISIBLE);
                rvProviderServices.setVisibility(View.GONE);
            } else {
                tvEmptyPosted.setVisibility(View.GONE);
                rvProviderServices.setVisibility(View.VISIBLE);
                serviceAdapter.setServices(services, -1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void openWhatsAppOrDial(OrderItem order, String phone) {
        if (phone == null || phone.trim().isEmpty()) return;

        String digitsOnly = phone.replaceAll("[^0-9]", "");
        String cleanedPhone = digitsOnly.length() >= 7 ? digitsOnly : phone.trim();

        try {
            String url = "https://api.whatsapp.com/send?phone=" + cleanedPhone +
                    "&text=" + Uri.encode("مرحباً " + (order.getClientName() != null ? order.getClientName() : "") + "، أنا " + (order.getServiceItem() != null ? order.getServiceItem().getProviderName() : "مقدم الخدمة") + " بشأن طلبك لخدمة: " + (order.getServiceItem() != null ? order.getServiceItem().getTitle() : ""));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            try {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.trim()));
                startActivity(dialIntent);
            } catch (Exception ex) {
                Toast.makeText(ProviderMainActivity.this, "تعذر فتح الواتساب أو الاتصال بالعميل: " + order.getClientName(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
