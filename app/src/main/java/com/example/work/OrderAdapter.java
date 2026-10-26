package com.example.work;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnOrderActionListener {
        void onOrderContactClick(OrderItem order);
        void onInAppChatClick(OrderItem order);
        void onAcceptOrder(OrderItem order);
        void onRejectOrder(OrderItem order);
        void onCompleteOrder(OrderItem order);
    }

    private List<OrderItem> orders = new ArrayList<>();
    private final OnOrderActionListener listener;
    private boolean isProviderMode = false;

    public OrderAdapter(OnOrderActionListener listener) {
        this.listener = listener;
    }

    public OrderAdapter(boolean isProviderMode, OnOrderActionListener listener) {
        this.isProviderMode = isProviderMode;
        this.listener = listener;
    }

    public void setOrders(List<OrderItem> newOrders) {
        this.orders = newOrders != null ? newOrders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem item = orders.get(position);
        holder.bind(item, isProviderMode, listener);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOrderStatus;
        private final TextView tvOrderDate;
        private final TextView tvOrderServiceTitle;
        private final TextView tvOrderProviderName;
        private final TextView tvOrderClientName;
        private final TextView tvOrderPrice;
        private final View llProviderActions;
        private final MaterialButton btnAcceptOrder;
        private final MaterialButton btnRejectOrder;
        private final MaterialButton btnCompleteOrder;
        private final MaterialButton btnInAppChat;
        private final MaterialButton btnOrderContactProvider;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderServiceTitle = itemView.findViewById(R.id.tvOrderServiceTitle);
            tvOrderProviderName = itemView.findViewById(R.id.tvOrderProviderName);
            tvOrderClientName = itemView.findViewById(R.id.tvOrderClientName);
            tvOrderPrice = itemView.findViewById(R.id.tvOrderPrice);
            llProviderActions = itemView.findViewById(R.id.llProviderActions);
            btnAcceptOrder = itemView.findViewById(R.id.btnAcceptOrder);
            btnRejectOrder = itemView.findViewById(R.id.btnRejectOrder);
            btnCompleteOrder = itemView.findViewById(R.id.btnCompleteOrder);
            btnInAppChat = itemView.findViewById(R.id.btnInAppChat);
            btnOrderContactProvider = itemView.findViewById(R.id.btnOrderContactProvider);
        }

        public void bind(OrderItem order, boolean isProviderMode, OnOrderActionListener listener) {
            ServiceItem service = order.getServiceItem();

            tvOrderStatus.setText(order.getStatus() != null ? order.getStatus() : OrderRepository.STATUS_PENDING);
            tvOrderDate.setText(String.format("تاريخ الطلب: %s", order.getOrderDate()));

            if (service != null) {
                tvOrderServiceTitle.setText(service.getTitle());
                tvOrderProviderName.setText(String.format("المزود: %s", service.getProviderName()));
                tvOrderPrice.setText(String.format(Locale.getDefault(), "$%.0f", service.getPrice()));
            }

            if (isProviderMode) {
                if (tvOrderClientName != null) {
                    tvOrderClientName.setVisibility(View.VISIBLE);
                    String clientInfo = String.format("العميل الطالب: %s (%s)",
                            order.getClientName() != null ? order.getClientName() : "عميل",
                            order.getClientEmail() != null ? order.getClientEmail() : "");
                    tvOrderClientName.setText(clientInfo);
                }

                String status = order.getStatus();
                if (status == null || status.contains("في انتظار")) {
                    llProviderActions.setVisibility(View.VISIBLE);
                    btnCompleteOrder.setVisibility(View.GONE);
                } else if (status.contains("تم قبول") || status.contains("قيد التنفيذ")) {
                    llProviderActions.setVisibility(View.GONE);
                    btnCompleteOrder.setVisibility(View.VISIBLE);
                } else {
                    llProviderActions.setVisibility(View.GONE);
                    btnCompleteOrder.setVisibility(View.GONE);
                }
            } else {
                if (tvOrderClientName != null) {
                    tvOrderClientName.setVisibility(View.GONE);
                }
                llProviderActions.setVisibility(View.GONE);
                btnCompleteOrder.setVisibility(View.GONE);
            }

            btnAcceptOrder.setOnClickListener(v -> {
                if (listener != null) listener.onAcceptOrder(order);
            });

            btnRejectOrder.setOnClickListener(v -> {
                if (listener != null) listener.onRejectOrder(order);
            });

            btnCompleteOrder.setOnClickListener(v -> {
                if (listener != null) listener.onCompleteOrder(order);
            });

            btnOrderContactProvider.setOnClickListener(v -> {
                if (listener != null) listener.onOrderContactClick(order);
            });

            btnInAppChat.setOnClickListener(v -> {
                if (listener != null) listener.onInAppChatClick(order);
            });
        }
    }
}
