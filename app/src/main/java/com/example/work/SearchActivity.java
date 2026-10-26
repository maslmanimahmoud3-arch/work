package com.example.work;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private TextInputEditText etSearchQuery;
    private TextInputEditText etTargetPrice;
    private ChipGroup cgSearchHistory;
    private LinearLayout llSearchHistorySection;
    private LinearLayout llSearchResultsHeader;
    private TextView tvResultsHeaderTitle;
    private RecyclerView rvSearchResults;
    private TextView tvSearchEmptyState;

    private ServiceAdapter adapter;
    private final ServiceRepository repository = ServiceRepository.getInstance();

    private final ServiceRepository.OnDataChangeListener dataChangeListener = () -> runOnUiThread(this::executeSearch);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        repository.init(this);
        repository.addOnDataChangeListener(dataChangeListener);

        initViews();
        setupRecyclerView();
        loadSearchHistory();
        autoFocusKeyboard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        repository.removeOnDataChangeListener(dataChangeListener);
    }

    private void initViews() {
        etSearchQuery = findViewById(R.id.etSearchQuery);
        etTargetPrice = findViewById(R.id.etTargetPrice);
        cgSearchHistory = findViewById(R.id.cgSearchHistory);
        llSearchHistorySection = findViewById(R.id.llSearchHistorySection);
        llSearchResultsHeader = findViewById(R.id.llSearchResultsHeader);
        tvResultsHeaderTitle = findViewById(R.id.tvResultsHeaderTitle);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        tvSearchEmptyState = findViewById(R.id.tvSearchEmptyState);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        TextView tvClearHistory = findViewById(R.id.tvClearHistory);
        MaterialButton btnDoSearch = findViewById(R.id.btnDoSearch);

        btnDoSearch.setOnClickListener(v -> executeSearch());

        tvClearHistory.setOnClickListener(v -> {
            SearchHistoryManager.clearHistory(SearchActivity.this);
            loadSearchHistory();
        });
    }

    private void setupRecyclerView() {
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ServiceAdapter(service -> {
            Intent intent = new Intent(SearchActivity.this, ServiceDetailActivity.class);
            intent.putExtra("SERVICE_ITEM", service);
            startActivity(intent);
        });
        rvSearchResults.setAdapter(adapter);
    }

    private void autoFocusKeyboard() {
        etSearchQuery.requestFocus();
        etSearchQuery.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etSearchQuery, 0);
            }
        }, 200);
    }

    private void loadSearchHistory() {
        cgSearchHistory.removeAllViews();
        List<String> history = SearchHistoryManager.getSearchHistory(this);

        if (history.isEmpty()) {
            llSearchHistorySection.setVisibility(View.GONE);
            return;
        }

        llSearchHistorySection.setVisibility(View.VISIBLE);
        for (String item : history) {
            Chip chip = new Chip(this);
            chip.setText(item);
            chip.setClickable(true);
            chip.setFocusable(true);
            chip.setChipBackgroundColorResource(R.color.orange_chip_bg);
            chip.setTextColor(getColor(R.color.orange_primary));

            chip.setOnClickListener(v -> {
                if (item.contains(" (بحدود $")) {
                    String[] parts = item.split(" \\(بحدود \\$");
                    etSearchQuery.setText(parts[0].trim());
                    if (parts.length > 1) {
                        String budgetStr = parts[1].replace(")", "").trim();
                        etTargetPrice.setText(budgetStr);
                    }
                } else if (item.startsWith("ميزانية بـ $")) {
                    etSearchQuery.setText("");
                    etTargetPrice.setText(item.replace("ميزانية بـ $", "").trim());
                } else {
                    etSearchQuery.setText(item.trim());
                    etTargetPrice.setText("");
                }
                executeSearch();
            });

            cgSearchHistory.addView(chip);
        }
    }

    private double getTargetPriceValue(String targetPriceStr) {
        if (!targetPriceStr.isEmpty()) {
            try {
                return Double.parseDouble(targetPriceStr);
            } catch (NumberFormatException ignored) {
            }
        }
        return -1;
    }

    private void executeSearch() {
        String query = etSearchQuery.getText() != null ? etSearchQuery.getText().toString().trim() : "";
        String targetPriceStr = etTargetPrice.getText() != null ? etTargetPrice.getText().toString().trim() : "";

        double targetPrice = getTargetPriceValue(targetPriceStr);

        if (query.isEmpty() && targetPrice <= 0) {
            tvSearchEmptyState.setText("يرجى كتابة اسم الخدمة أو السعر المطلوب للبحث");
            tvSearchEmptyState.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
            llSearchResultsHeader.setVisibility(View.GONE);
            return;
        }

        // Save to search history
        SearchHistoryManager.addSearchQuery(this, query, targetPrice);
        loadSearchHistory();

        // Perform search
        List<ServiceItem> results = repository.searchServices(query, targetPrice, "الكل");

        if (results.isEmpty()) {
            tvSearchEmptyState.setText("لم يتم العثور على خدمات مطابقة للبحث والميزانية المحددة. جرب البحث بسعر أو اسم مختلف!");
            tvSearchEmptyState.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
            llSearchResultsHeader.setVisibility(View.VISIBLE);
            String headerText = String.format(Locale.getDefault(), "نتائج البحث لـ: %s",
                    query.isEmpty() ? String.format(Locale.getDefault(), "ميزانية $%.0f", targetPrice) : query);
            tvResultsHeaderTitle.setText(headerText);
        } else {
            tvSearchEmptyState.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.VISIBLE);
            llSearchResultsHeader.setVisibility(View.VISIBLE);
            String headerText = String.format(Locale.getDefault(), "نتائج البحث (%d خدمة مطابقة وسعر مقارب):", results.size());
            tvResultsHeaderTitle.setText(headerText);
            adapter.setServices(results, targetPrice);
        }

        // Hide keyboard after search execution
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etSearchQuery.getWindowToken(), 0);
        }
    }
}
