package com.example.work;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ClientMainActivity extends AppCompatActivity {

    private ChipGroup chipGroupCategories;
    private HorizontalScrollView hsvCategories;
    private RecyclerView rvServices;
    private RecyclerView rvOrders;
    private TextView tvEmptyState;
    private TextView tvWelcomeHeader;
    private MaterialButton btnTabBrowseServices;
    private MaterialButton btnTabMyOrders;

    private ServiceAdapter serviceAdapter;
    private OrderAdapter orderAdapter;
    private final ServiceRepository repository = ServiceRepository.getInstance();
    private final OrderRepository orderRepository = OrderRepository.getInstance();

    private String selectedCategory = "الكل";
    private boolean isMyOrdersTabActive = false;

    private final ServiceRepository.OnDataChangeListener dataChangeListener = () -> runOnUiThread(this::loadCategoryServices);
    private final OrderRepository.OnOrdersChangeListener ordersChangeListener = () -> runOnUiThread(this::loadMyOrders);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);

        repository.init(this);
        orderRepository.init(this);

        initViews();
        setupUserHeader();
        setupRecyclerViews();
        setupListeners();

        repository.addOnDataChangeListener(dataChangeListener);
        orderRepository.addOnOrdersChangeListener(ordersChangeListener);

        loadCategoryServices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        repository.removeOnDataChangeListener(dataChangeListener);
        orderRepository.removeOnOrdersChangeListener(ordersChangeListener);
    }

    private void initViews() {
        tvWelcomeHeader = findViewById(R.id.tvWelcomeHeader);
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        hsvCategories = findViewById(R.id.hsvCategories);
        rvServices = findViewById(R.id.rvServices);
        rvOrders = findViewById(R.id.rvOrders);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnTabBrowseServices = findViewById(R.id.btnTabBrowseServices);
        btnTabMyOrders = findViewById(R.id.btnTabMyOrders);

        MaterialCardView cardOpenSearchScreen = findViewById(R.id.cardOpenSearchScreen);
        MaterialButton btnSwitchRole = findViewById(R.id.btnSwitchRole);
        FloatingActionButton fabSearch = findViewById(R.id.fabSearch);

        btnSwitchRole.setOnClickListener(v -> {
            Intent intent = new Intent(ClientMainActivity.this, RoleSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        View.OnClickListener openSearchListener = v -> {
            Intent intent = new Intent(ClientMainActivity.this, SearchActivity.class);
            startActivity(intent);
        };

        if (cardOpenSearchScreen != null) {
            cardOpenSearchScreen.setOnClickListener(openSearchListener);
        }

        if (fabSearch != null) {
            fabSearch.setOnClickListener(openSearchListener);
        }
    }

    private void setupUserHeader() {
        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName != null && !userName.trim().isEmpty()) {
            tvWelcomeHeader.setText(String.format("أهلاً بك %s 👋", userName.trim()));
        }
    }

    private void setupRecyclerViews() {
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        serviceAdapter = new ServiceAdapter(service -> {
            Intent intent = new Intent(ClientMainActivity.this, ServiceDetailActivity.class);
            intent.putExtra("SERVICE_ITEM", service);
            startActivity(intent);
        });
        rvServices.setAdapter(serviceAdapter);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(false, new OrderAdapter.OnOrderActionListener() {
            @Override
            public void onOrderContactClick(OrderItem order) {
                if (order.getServiceItem() != null && order.getServiceItem().getProviderPhone() != null && !order.getServiceItem().getProviderPhone().trim().isEmpty()) {
                    String phone = order.getServiceItem().getProviderPhone().trim();
                    String digitsOnly = phone.replaceAll("[^0-9]", "");
                    String cleanedPhone = digitsOnly.length() >= 7 ? digitsOnly : phone;

                    try {
                        String url = "https://api.whatsapp.com/send?phone=" + cleanedPhone +
                                "&text=" + Uri.encode("مرحباً، أود المتابعة بشأن طلبي لخدمتك: " + order.getServiceItem().getTitle());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        try {
                            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                            startActivity(dialIntent);
                        } catch (Exception ex) {
                            Toast.makeText(ClientMainActivity.this, "تعذر التواصل مع مقدم الخدمة", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (order.getServiceItem() != null) {
                    Intent intent = new Intent(ClientMainActivity.this, ServiceDetailActivity.class);
                    intent.putExtra("SERVICE_ITEM", order.getServiceItem());
                    startActivity(intent);
                } else {
                    Toast.makeText(ClientMainActivity.this, "تفاصيل الخدمة غير متوفرة", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onInAppChatClick(OrderItem order) {
                Intent intent = new Intent(ClientMainActivity.this, ChatActivity.class);
                intent.putExtra("CHAT_ID", order.getOrderId());
                intent.putExtra("RECEIVER_NAME", order.getServiceItem() != null ? order.getServiceItem().getProviderName() : "مقدم الخدمة");
                intent.putExtra("SERVICE_TITLE", order.getServiceItem() != null ? order.getServiceItem().getTitle() : "طلب خدمة");
                intent.putExtra("CURRENT_ROLE", "client");
                intent.putExtra("CURRENT_USER_NAME", order.getClientName() != null ? order.getClientName() : "العميل");
                startActivity(intent);
            }

            @Override
            public void onAcceptOrder(OrderItem order) {}

            @Override
            public void onRejectOrder(OrderItem order) {}

            @Override
            public void onCompleteOrder(OrderItem order) {}
        });
        rvOrders.setAdapter(orderAdapter);
    }

    private void setupListeners() {
        btnTabBrowseServices.setOnClickListener(v -> activateBrowseTab());
        btnTabMyOrders.setOnClickListener(v -> activateMyOrdersTab());

        chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                selectedCategory = "الكل";
            } else {
                int checkedId = checkedIds.get(0);
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    selectedCategory = chip.getText().toString();
                } else {
                    selectedCategory = "الكل";
                }
            }
            loadCategoryServices();
        });
    }

    private void activateBrowseTab() {
        isMyOrdersTabActive = false;
        btnTabBrowseServices.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.brand_primary)));
        btnTabBrowseServices.setTextColor(getColor(R.color.white));

        btnTabMyOrders.setBackgroundTintList(ColorStateList.valueOf(getColor(android.R.color.transparent)));
        btnTabMyOrders.setTextColor(getColor(R.color.text_secondary));

        hsvCategories.setVisibility(View.VISIBLE);
        rvServices.setVisibility(View.VISIBLE);
        rvOrders.setVisibility(View.GONE);

        loadCategoryServices();
    }

    private void activateMyOrdersTab() {
        isMyOrdersTabActive = true;
        btnTabMyOrders.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.brand_primary)));
        btnTabMyOrders.setTextColor(getColor(R.color.white));

        btnTabBrowseServices.setBackgroundTintList(ColorStateList.valueOf(getColor(android.R.color.transparent)));
        btnTabBrowseServices.setTextColor(getColor(R.color.text_secondary));

        hsvCategories.setVisibility(View.GONE);
        rvServices.setVisibility(View.GONE);
        rvOrders.setVisibility(View.VISIBLE);

        loadMyOrders();
    }

    private void loadCategoryServices() {
        if (isMyOrdersTabActive) return;

        List<ServiceItem> results = repository.searchServices("", -1, selectedCategory);

        if (results.isEmpty()) {
            tvEmptyState.setText(R.string.no_services_found);
            tvEmptyState.setVisibility(View.VISIBLE);
            rvServices.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvServices.setVisibility(View.VISIBLE);
            serviceAdapter.setServices(results, -1);
        }
    }

    private void loadMyOrders() {
        List<OrderItem> orders = orderRepository.getAllOrders();

        if (orders.isEmpty()) {
            tvEmptyState.setText("لم تقم بطلب أو استخدام أي خدمة بعد. ابحث عن خدمة واطلبها لتظهر هنا!");
            tvEmptyState.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            orderAdapter.setOrders(orders);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMyOrdersTabActive) {
            loadMyOrders();
        } else {
            loadCategoryServices();
        }
    }
}
