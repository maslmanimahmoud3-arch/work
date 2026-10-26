package com.example.work;

import org.json.JSONObject;

import java.io.Serializable;

public class ServiceItem implements Serializable {
    private String id;
    private String title;
    private String category;
    private String description;
    private double price;
    private String providerName;
    private String providerPhone;
    private float rating;
    private int reviewsCount;
    private int imageResId;
    private String imageUri;
    private String imageBase64;

    // Default constructor required for Firebase Realtime Database
    public ServiceItem() {
    }

    public ServiceItem(String id, String title, String category, String description, double price, String providerName, String providerPhone, float rating, int reviewsCount, int imageResId, String imageUri, String imageBase64) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
        this.providerName = providerName;
        this.providerPhone = providerPhone;
        this.rating = rating;
        this.reviewsCount = reviewsCount;
        this.imageResId = imageResId;
        this.imageUri = imageUri;
        this.imageBase64 = imageBase64;
    }

    public ServiceItem(String id, String title, String category, String description, double price, String providerName, String providerPhone, float rating, int reviewsCount, int imageResId, String imageUri) {
        this(id, title, category, description, price, providerName, providerPhone, rating, reviewsCount, imageResId, imageUri, null);
    }

    public ServiceItem(String id, String title, String category, String description, double price, String providerName, String providerPhone, float rating, int reviewsCount, int imageResId) {
        this(id, title, category, description, price, providerName, providerPhone, rating, reviewsCount, imageResId, null, null);
    }

    public ServiceItem(String id, String title, String category, String description, double price, String providerName, String providerPhone, float rating, int reviewsCount) {
        this(id, title, category, description, price, providerName, providerPhone, rating, reviewsCount, R.drawable.img_work_code, null, null);
    }

    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("title", title);
            json.put("category", category);
            json.put("description", description);
            json.put("price", price);
            json.put("providerName", providerName);
            json.put("providerPhone", providerPhone);
            json.put("rating", rating);
            json.put("reviewsCount", reviewsCount);
            json.put("imageResId", imageResId);
            if (imageUri != null) json.put("imageUri", imageUri);
            if (imageBase64 != null) json.put("imageBase64", imageBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static ServiceItem fromJsonObject(JSONObject json) {
        if (json == null) return null;
        return new ServiceItem(
                json.optString("id", ""),
                json.optString("title", ""),
                json.optString("category", ""),
                json.optString("description", ""),
                json.optDouble("price", 0.0),
                json.optString("providerName", ""),
                json.optString("providerPhone", ""),
                (float) json.optDouble("rating", 5.0),
                json.optInt("reviewsCount", 1),
                json.optInt("imageResId", R.drawable.img_work_code),
                json.has("imageUri") ? json.optString("imageUri", null) : null,
                json.has("imageBase64") ? json.optString("imageBase64", null) : null
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(String providerPhone) {
        this.providerPhone = providerPhone;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
