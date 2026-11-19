
package com.example.mobile_comp_prj;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class homepg extends AppCompatActivity {

    Button logoutButton;
    Button manageProductsButton;
    EditText searchInput;
    TextView homeWelcome;
    RecyclerView productList;

    ArrayList<product> allProducts;
    prodAdapter adapter;

    database databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepg);

        logoutButton = findViewById(R.id.logoutButton);
        manageProductsButton = findViewById(R.id.manageProductsButton);
        searchInput = findViewById(R.id.searchInput);
        homeWelcome = findViewById(R.id.homeWelcome);
        productList = findViewById(R.id.productList);

        // Receive the user's email from loginActivity
        String userEmail = getIntent().getStringExtra("USER_EMAIL");

        if (userEmail != null && !userEmail.isEmpty()) {
            homeWelcome.setText("Welcome, " + userEmail);
        }

        // Initialize database
        databaseHelper = new database(this);

        // Set up the product grid
        productList.setLayoutManager(new GridLayoutManager(this, 2));

        // Add default products if the database is empty
        seedDefaultProducts();

        // Display products
        refreshProducts("");

        // Search functionality
        searchInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                refreshProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Open Product Management
        manageProductsButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    homepg.this,
                    Prodmng_Activity.class);

            startActivity(intent);
        });

        // Logout functionality
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    homepg.this,
                    MainActivity.class);

            startActivity(intent);
            finish();
        });
    }

    // Load products from SQLite
    private void refreshProducts(String searchText) {

        if (searchText.trim().isEmpty()) {
            allProducts = databaseHelper.getAllProducts();
        } else {
            allProducts = databaseHelper.searchProducts(searchText);
        }

        adapter = new prodAdapter(new ArrayList<>(allProducts));
        productList.setAdapter(adapter);
    }

    // Add the original catalogue products if none exist
    private void seedDefaultProducts() {

        if (!databaseHelper.getAllProducts().isEmpty()) {
            return;
        }

        // Bags
        databaseHelper.insertProduct(
                getString(R.string.product_mini_bag),
                getString(R.string.desc_mini_bag),
                getString(R.string.category_bags),
                getString(R.string.price_mini_bag)
        );

        databaseHelper.insertProduct(
                getString(R.string.product_tote_bag),
                getString(R.string.desc_tote_bag),
                getString(R.string.category_bags),
                getString(R.string.price_tote_bag)
        );

        // Dresses
        databaseHelper.insertProduct(
                getString(R.string.product_floral_dress),
                getString(R.string.desc_floral_dress),
                getString(R.string.category_dresses),
                getString(R.string.price_floral_dress)
        );

        databaseHelper.insertProduct(
                getString(R.string.product_midi_dress),
                getString(R.string.desc_midi_dress),
                getString(R.string.category_dresses),
                getString(R.string.price_midi_dress)
        );

        // Accessories
        databaseHelper.insertProduct(
                getString(R.string.product_necklace),
                getString(R.string.desc_necklace),
                getString(R.string.category_accessories),
                getString(R.string.price_necklace)
        );

        databaseHelper.insertProduct(
                getString(R.string.product_sunglasses),
                getString(R.string.desc_sunglasses),
                getString(R.string.category_accessories),
                getString(R.string.price_sunglasses)
        );
    }

    // Refresh catalogue when returning from Product Management
    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && searchInput != null) {
            refreshProducts(searchInput.getText().toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
