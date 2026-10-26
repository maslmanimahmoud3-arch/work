package com.example.work;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class ServiceDetailActivity extends AppCompatActivity {

    private ImageView ivDetailWorkPhoto;
    private TextView tvDetailCategory;
    private TextView tvDetailRating;
    private TextView tvDetailTitle;
    private TextView tvDetailPrice;
    private TextView tvDetailDescription;
    private TextView tvDetailProviderName;
    private TextView tvDetailProviderPhone;

    private ServiceItem serviceItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            serviceItem = getIntent().getSerializableExtra("SERVICE_ITEM", ServiceItem.class);
        } else {
            @SuppressWarnings("deprecation")
            ServiceItem item = (ServiceItem) getIntent().getSerializableExtra("SERVICE_ITEM");
            serviceItem = item;
        }

        if (serviceItem == null) {
            Toast.makeText(this, "تعذر تحميل بيانات الخدمة", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        populateData();
    }

    private void initViews() {
        ivDetailWorkPhoto = findViewById(R.id.ivDetailWorkPhoto);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailRating = findViewById(R.id.tvDetailRating);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvDetailProviderName = findViewById(R.id.tvDetailProviderName);
        tvDetailProviderPhone = findViewById(R.id.tvDetailProviderPhone);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        MaterialButton btnContactWhatsApp = findViewById(R.id.btnContactWhatsApp);
        MaterialButton btnContactPhone = findViewById(R.id.btnContactPhone);
        MaterialButton btnSendDirectMessage = findViewById(R.id.btnSendDirectMessage);
        MaterialButton btnOrderNow = findViewById(R.id.btnOrderNow);

        btnContactWhatsApp.setOnClickListener(v -> openWhatsApp());
        btnContactPhone.setOnClickListener(v -> makePhoneCall());
        btnSendDirectMessage.setOnClickListener(v -> openInAppChat());
        btnOrderNow.setOnClickListener(v -> showOrderConfirmation());
    }

    private void populateData() {
        if (ivDetailWorkPhoto != null) {
            if (serviceItem.getImageBase64() != null && !serviceItem.getImageBase64().isEmpty()) {
                try {
                    byte[] decodedBytes = Base64.decode(serviceItem.getImageBase64(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    if (bitmap != null) {
                        ivDetailWorkPhoto.setImageBitmap(bitmap);
                    } else {
                        ivDetailWorkPhoto.setImageResource(R.drawable.img_work_code);
                    }
                } catch (Exception e) {
                    ivDetailWorkPhoto.setImageResource(R.drawable.img_work_code);
                }
            } else if (serviceItem.getImageUri() != null && !serviceItem.getImageUri().isEmpty()) {
                try {
                    ivDetailWorkPhoto.setImageURI(Uri.parse(serviceItem.getImageUri()));
                } catch (Exception e) {
                    ivDetailWorkPhoto.setImageResource(R.drawable.img_work_code);
                }
            } else if (serviceItem.getImageResId() != 0) {
                ivDetailWorkPhoto.setImageResource(serviceItem.getImageResId());
            } else {
                ivDetailWorkPhoto.setImageResource(R.drawable.img_work_code);
            }
        }

        tvDetailCategory.setText(serviceItem.getCategory());
        tvDetailRating.setText(String.format(Locale.getDefault(), "%.1f", serviceItem.getRating()));
        tvDetailTitle.setText(serviceItem.getTitle());
        tvDetailPrice.setText(String.format(Locale.getDefault(), "$%.0f", serviceItem.getPrice()));
        tvDetailDescription.setText(serviceItem.getDescription());
        tvDetailProviderName.setText(String.format("مقدم الخدمة: %s", serviceItem.getProviderName()));
        tvDetailProviderPhone.setText(String.format("وسيلة التواصل: %s", serviceItem.getProviderPhone()));
    }

    private void openWhatsApp() {
        String phone = serviceItem.getProviderPhone();
        if (phone == null || phone.trim().isEmpty()) {
            Toast.makeText(this, "رقم التواصل غير متوفر", Toast.LENGTH_SHORT).show();
            return;
        }

        String digitsOnly = phone.replaceAll("[^0-9]", "");
        String cleanedPhone = digitsOnly.length() >= 7 ? digitsOnly : "962791234567";

        try {
            String url = "https://api.whatsapp.com/send?phone=" + cleanedPhone +
                    "&text=" + Uri.encode("مرحباً، أود الاستفسار عن خدمتك: " + serviceItem.getTitle());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "تعذر فتح تطبيق الواتساب مباشرة", Toast.LENGTH_SHORT).show();
        }
    }

    private void makePhoneCall() {
        String phone = serviceItem.getProviderPhone();
        if (phone != null && !phone.trim().isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone.trim()));
            startActivity(intent);
        } else {
            Toast.makeText(this, "رقم الاتصال غير متوفر", Toast.LENGTH_SHORT).show();
        }
    }

    private void openInAppChat() {
        Intent intent = new Intent(ServiceDetailActivity.this, ChatActivity.class);
        intent.putExtra("CHAT_ID", serviceItem.getId());
        intent.putExtra("RECEIVER_NAME", serviceItem.getProviderName());
        intent.putExtra("SERVICE_TITLE", serviceItem.getTitle());
        intent.putExtra("CURRENT_ROLE", "client");
        intent.putExtra("CURRENT_USER_NAME", "عميل مهتم");
        startActivity(intent);
    }

    private void showOrderConfirmation() {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 24);

        final EditText inputName = new EditText(this);
        inputName.setHint("ادخل اسمك الكامل (مثال: أحمد)");
        inputName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        layout.addView(inputName);

        final EditText inputPhone = new EditText(this);
        inputPhone.setHint("ادخل رقم هاتفك/الواتساب (مثال: 0791234567)");
        inputPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        inputPhone.setPadding(0, 32, 0, 0);
        layout.addView(inputPhone);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("تأكيد طلب الخدمة 🚀")
                .setMessage(String.format(Locale.getDefault(), "خدمة: %s\nالسعر: $%.0f دولار\n\nيرجى إدخال اسمك ورقم هاتفك للبدء والتواصل مع %s:",
                        serviceItem.getTitle(),
                        serviceItem.getPrice(),
                        serviceItem.getProviderName()))
                .setView(layout)
                .setPositiveButton("إرسال الطلب 🚀", null)
                .setNegativeButton("إلغاء", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String clientName = inputName.getText().toString().trim();
                String clientPhone = inputPhone.getText().toString().trim();

                if (clientName.isEmpty()) {
                    inputName.setError("يرجى إدخال الاسم الكامل");
                    return;
                }

                if (clientPhone.isEmpty() || clientPhone.length() < 7) {
                    inputPhone.setError("يرجى إدخال رقم هاتف صحيح");
                    return;
                }

                OrderRepository.getInstance().placeOrder(serviceItem, clientName, "", clientPhone);
                Toast.makeText(ServiceDetailActivity.this, "تم إرسال طلبك بنجاح! سيصل لمقدم الخدمة ليقوم بالتواصل معك.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            });
        });

        dialog.show();
    }
}
