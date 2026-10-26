package com.example.work;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RoleSelectionActivity extends AppCompatActivity {

    private MaterialCardView cardRoleClient;
    private MaterialCardView cardRoleProvider;
    private RadioButton radioClient;
    private RadioButton radioProvider;
    private TextInputLayout tilUserName;
    private TextInputLayout tilUserEmail;
    private TextInputEditText etUserName;
    private TextInputEditText etUserEmail;

    private boolean isClientSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        initViews();
        setupRoleToggle();
    }

    private void initViews() {
        cardRoleClient = findViewById(R.id.cardRoleClient);
        cardRoleProvider = findViewById(R.id.cardRoleProvider);
        radioClient = findViewById(R.id.radioClient);
        radioProvider = findViewById(R.id.radioProvider);
        tilUserName = findViewById(R.id.tilUserName);
        tilUserEmail = findViewById(R.id.tilUserEmail);
        etUserName = findViewById(R.id.etUserName);
        etUserEmail = findViewById(R.id.etUserEmail);
        MaterialButton btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v -> validateAndProceed());
    }

    private void setupRoleToggle() {
        cardRoleClient.setOnClickListener(v -> selectClientRole());
        cardRoleProvider.setOnClickListener(v -> selectProviderRole());
    }

    private void selectClientRole() {
        isClientSelected = true;
        radioClient.setChecked(true);
        radioProvider.setChecked(false);
        cardRoleClient.setStrokeColor(getColor(R.color.orange_primary));
        cardRoleClient.setStrokeWidth(dpToPx(2));
        cardRoleProvider.setStrokeColor(getColor(R.color.card_stroke));
        cardRoleProvider.setStrokeWidth(dpToPx(1));
    }

    private void selectProviderRole() {
        isClientSelected = false;
        radioClient.setChecked(false);
        radioProvider.setChecked(true);
        cardRoleProvider.setStrokeColor(getColor(R.color.orange_primary));
        cardRoleProvider.setStrokeWidth(dpToPx(2));
        cardRoleClient.setStrokeColor(getColor(R.color.card_stroke));
        cardRoleClient.setStrokeWidth(dpToPx(1));
    }

    private void validateAndProceed() {
        tilUserName.setError(null);
        tilUserEmail.setError(null);

        String name = etUserName.getText() != null ? etUserName.getText().toString().trim() : "";
        String email = etUserEmail.getText() != null ? etUserEmail.getText().toString().trim() : "";

        boolean nameValid = !name.isEmpty();
        boolean emailValid = !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if (!nameValid) {
            tilUserName.setError(getString(R.string.err_empty_name));
        }

        if (!emailValid) {
            tilUserEmail.setError(getString(R.string.err_invalid_email));
        }

        if (!nameValid || !emailValid) {
            Toast.makeText(this, "يرجى كتابة اسم وبريد إلكتروني صحيحين لتسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent;
        if (isClientSelected) {
            intent = new Intent(RoleSelectionActivity.this, ClientMainActivity.class);
        } else {
            intent = new Intent(RoleSelectionActivity.this, ProviderMainActivity.class);
        }

        intent.putExtra("USER_NAME", name);
        intent.putExtra("USER_EMAIL", email);
        startActivity(intent);
        finish();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
