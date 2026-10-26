package com.example.work;

import org.json.JSONObject;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private String orderId;
    private ServiceItem serviceItem;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String orderDate;
    private String status;

    public OrderItem() {
    }

    public OrderItem(String orderId, ServiceItem serviceItem, String clientName, String clientEmail, String clientPhone, String orderDate, String status) {
        this.orderId = orderId;
        this.serviceItem = serviceItem;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.orderDate = orderDate;
        this.status = status;
    }

    public OrderItem(String orderId, ServiceItem serviceItem, String clientName, String clientEmail, String orderDate, String status) {
        this(orderId, serviceItem, clientName, clientEmail, null, orderDate, status);
    }

    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("orderId", orderId);
            if (serviceItem != null) {
                json.put("serviceItem", serviceItem.toJsonObject());
            }
            json.put("clientName", clientName);
            json.put("clientEmail", clientEmail);
            json.put("clientPhone", clientPhone);
            json.put("orderDate", orderDate);
            json.put("status", status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static OrderItem fromJsonObject(JSONObject json) {
        if (json == null) return null;
        ServiceItem service = null;
        if (json.has("serviceItem")) {
            service = ServiceItem.fromJsonObject(json.optJSONObject("serviceItem"));
        }
        return new OrderItem(
                json.optString("orderId", ""),
                service,
                json.optString("clientName", ""),
                json.optString("clientEmail", ""),
                json.optString("clientPhone", ""),
                json.optString("orderDate", ""),
                json.optString("status", "")
        );
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public ServiceItem getServiceItem() {
        return serviceItem;
    }

    public void setServiceItem(ServiceItem serviceItem) {
        this.serviceItem = serviceItem;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
