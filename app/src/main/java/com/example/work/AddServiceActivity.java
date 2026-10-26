package com.example.work;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

public class AddServiceActivity extends AppCompatActivity {

    private TextInputEditText etTitle;
    private AutoCompleteTextView actvCategory;
    private TextInputEditText etPrice;
    private TextInputEditText etDescription;
    private TextInputEditText etProviderName;
    private TextInputEditText etProviderPhone;
    private ImageView ivSelectedServiceImage;
    private View llImagePlaceholder;

    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivSelectedServiceImage.setImageURI(uri);
                    llImagePlaceholder.setVisibility(View.GONE);
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception ignored) {
                    }
                }
            });

    private final String[] categories = new String[]{"برمجة", "تصميم", "ترجمة", "تسويق", "صيانة"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        ServiceRepository.getInstance().init(this);

        initViews();
        setupCategoryDropdown();
        setupTouchToHideKeyboard();
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        actvCategory = findViewById(R.id.actvCategory);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        etProviderName = findViewById(R.id.etProviderName);
        etProviderPhone = findViewById(R.id.etProviderPhone);
        ivSelectedServiceImage = findViewById(R.id.ivSelectedServiceImage);
        llImagePlaceholder = findViewById(R.id.llImagePlaceholder);

        MaterialCardView cardSelectServiceImage = findViewById(R.id.cardSelectServiceImage);
        cardSelectServiceImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        MaterialButton btnPublish = findViewById(R.id.btnPublish);
        btnPublish.setOnClickListener(v -> publishService());
    }

    private void setupCategoryDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(adapter);
    }

    private void setupTouchToHideKeyboard() {
        View root = findViewById(R.id.rootAddServiceLayout);
        View scrollView = findViewById(R.id.scrollViewContainer);

        View.OnClickListener clickListener = v -> hideSoftKeyboard();

        if (root != null) root.setOnClickListener(clickListener);
        if (scrollView != null) scrollView.setOnClickListener(clickListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    hideSoftKeyboard();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private boolean isValidContact(String contact) {
        if (contact == null || contact.trim().isEmpty()) {
            return false;
        }
        String trimmed = contact.trim();
        boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(trimmed).matches();
        boolean isPhone = trimmed.replaceAll("[^0-9]", "").length() >= 7;
        return isEmail || isPhone;
    }

    private int getCategorySampleImage(String category) {
        if (category.contains("تصميم")) {
            return R.drawable.img_work_design;
        } else if (category.contains("برمجة")) {
            return R.drawable.img_work_code;
        } else if (category.contains("تسويق")) {
            return R.drawable.img_work_marketing;
        } else if (category.contains("ترجمة")) {
            return R.drawable.img_work_translation;
        } else if (category.contains("صيانة")) {
            return R.drawable.img_work_maintenance;
        } else {
            return R.drawable.img_work_code;
        }
    }

    private String uriToBase64(Uri uri) {
        if (uri == null) return null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) inputStream.close();
            if (bitmap == null) return null;

            int maxDim = 500;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > maxDim || height > maxDim) {
                float ratio = Math.min((float) maxDim / width, (float) maxDim / height);
                width = Math.round(width * ratio);
                height = Math.round(height * ratio);
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] bytes = baos.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    private void publishService() {
        hideSoftKeyboard();

        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String category = actvCategory.getText() != null ? actvCategory.getText().toString().trim() : "";
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String providerName = etProviderName.getText() != null ? etProviderName.getText().toString().trim() : "";
        String providerPhone = etProviderPhone.getText() != null ? etProviderPhone.getText().toString().trim() : "";

        if (title.isEmpty() || category.isEmpty() || priceStr.isEmpty() || description.isEmpty() || providerName.isEmpty() || providerPhone.isEmpty()) {
            Toast.makeText(this, R.string.err_fill_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidContact(providerPhone)) {
            Toast.makeText(this, R.string.err_invalid_contact, Toast.LENGTH_LONG).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "يرجى كتابة رقم سعر صحيح", Toast.LENGTH_SHORT).show();
            return;
        }

        int imageResId = getCategorySampleImage(category);
        String imageUriStr = selectedImageUri != null ? selectedImageUri.toString() : null;
        String imageBase64Str = selectedImageUri != null ? uriToBase64(selectedImageUri) : null;

        ServiceItem newService = new ServiceItem(
                UUID.randomUUID().toString(),
                title,
                category,
                description,
                price,
                providerName,
                providerPhone,
                5.0f,
                1,
                imageResId,
                imageUriStr,
                imageBase64Str
        );

        ServiceRepository.getInstance().addService(newService);
        Toast.makeText(this, R.string.service_added_success, Toast.LENGTH_SHORT).show();
        finish();
    }
}
