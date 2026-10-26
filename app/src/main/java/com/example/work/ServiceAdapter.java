package com.example.work;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    public interface OnServiceClickListener {
        void onServiceClick(ServiceItem service);
    }

    private List<ServiceItem> services = new ArrayList<>();
    private final OnServiceClickListener listener;
    private double currentTargetPrice = -1;

    public ServiceAdapter(OnServiceClickListener listener) {
        this.listener = listener;
    }

    public void setServices(List<ServiceItem> newServices, double targetPrice) {
        this.services = newServices != null ? newServices : new ArrayList<>();
        this.currentTargetPrice = targetPrice;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceItem item = services.get(position);
        holder.bind(item, currentTargetPrice, listener);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivServiceThumbnail;
        private final TextView tvCategoryBadge;
        private final TextView tvRating;
        private final TextView tvReviewsCount;
        private final TextView tvServiceTitle;
        private final TextView tvProviderName;
        private final TextView tvPriceValue;
        private final TextView tvPriceSimilarityBadge;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivServiceThumbnail = itemView.findViewById(R.id.ivServiceThumbnail);
            tvCategoryBadge = itemView.findViewById(R.id.tvCategoryBadge);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvReviewsCount = itemView.findViewById(R.id.tvReviewsCount);
            tvServiceTitle = itemView.findViewById(R.id.tvServiceTitle);
            tvProviderName = itemView.findViewById(R.id.tvProviderName);
            tvPriceValue = itemView.findViewById(R.id.tvPriceValue);
            tvPriceSimilarityBadge = itemView.findViewById(R.id.tvPriceSimilarityBadge);
        }

        public void bind(ServiceItem item, double targetPrice, OnServiceClickListener listener) {
            if (ivServiceThumbnail != null) {
                if (item.getImageBase64() != null && !item.getImageBase64().isEmpty()) {
                    try {
                        byte[] decodedBytes = Base64.decode(item.getImageBase64(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        if (bitmap != null) {
                            ivServiceThumbnail.setImageBitmap(bitmap);
                        } else {
                            ivServiceThumbnail.setImageResource(R.drawable.img_work_code);
                        }
                    } catch (Exception e) {
                        ivServiceThumbnail.setImageResource(R.drawable.img_work_code);
                    }
                } else if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
                    try {
                        ivServiceThumbnail.setImageURI(Uri.parse(item.getImageUri()));
                    } catch (Exception e) {
                        ivServiceThumbnail.setImageResource(R.drawable.img_work_code);
                    }
                } else if (item.getImageResId() != 0) {
                    ivServiceThumbnail.setImageResource(item.getImageResId());
                } else {
                    ivServiceThumbnail.setImageResource(R.drawable.img_work_code);
                }
            }

            tvCategoryBadge.setText(item.getCategory());
            tvRating.setText(String.format(Locale.getDefault(), "%.1f", item.getRating()));
            tvReviewsCount.setText(String.format(Locale.getDefault(), "(%d)", item.getReviewsCount()));
            tvServiceTitle.setText(item.getTitle());
            tvProviderName.setText(String.format("بواسطة: %s", item.getProviderName()));
            tvPriceValue.setText(String.format(Locale.getDefault(), "%.0f", item.getPrice()));

            // Display price similarity indicator if user specified a budget
            if (targetPrice > 0) {
                tvPriceSimilarityBadge.setVisibility(View.VISIBLE);
                double diff = item.getPrice() - targetPrice;
                if (Math.abs(diff) < 0.01) {
                    tvPriceSimilarityBadge.setText("سعر مطابق لميزانيتك بالضبط! 🎯");
                } else if (diff < 0) {
                    tvPriceSimilarityBadge.setText(String.format(Locale.getDefault(), "أقل من ميزانيتك بـ $%.0f 💡", Math.abs(diff)));
                } else {
                    tvPriceSimilarityBadge.setText(String.format(Locale.getDefault(), "أعلى من ميزانيتك بـ $%.0f", diff));
                }
            } else {
                tvPriceSimilarityBadge.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onServiceClick(item);
                }
            });
        }
    }
}
