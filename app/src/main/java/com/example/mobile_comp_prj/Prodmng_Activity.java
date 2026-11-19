
package com.example.mobile_comp_prj;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Prodmng_Activity extends AppCompatActivity {

    EditText productNameInput;
    EditText productDescriptionInput;
    EditText productCategoryInput;
    EditText productPriceInput;
    EditText productSearchInput;

    Button addProductButton;
    Button showAllProductsButton;

    View addProductHeader;
    View addProductForm;
    TextView addProductArrow;

    RecyclerView managementProductList;

    database databaseHelper;
    prodmngAdapter adapter;

    ArrayList<product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_prodmng);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            16 + systemBars.left,
                            16 + systemBars.top,
                            16 + systemBars.right,
                            16 + systemBars.bottom
                    );

                    return insets;
                });

        // Connect XML elements
        productNameInput = findViewById(R.id.productNameInput);

        productDescriptionInput =
                findViewById(R.id.productDescriptionInput);

        productCategoryInput =
                findViewById(R.id.productCategoryInput);

        productPriceInput =
                findViewById(R.id.productPriceInput);

        productSearchInput =
                findViewById(R.id.productSearchInput);

        addProductButton =
                findViewById(R.id.addProductButton);

        showAllProductsButton =
                findViewById(R.id.showAllProductsButton);

        addProductHeader =
                findViewById(R.id.addProductHeader);

        addProductForm =
                findViewById(R.id.addProductForm);

        addProductArrow =
                findViewById(R.id.addProductArrow);

        managementProductList =
                findViewById(R.id.managementProductList);

        // Start with the Add Product form collapsed
        addProductForm.setVisibility(View.GONE);
        addProductArrow.setText("▼");

        // Toggle Add Product form
        addProductHeader.setOnClickListener(v -> toggleAddProductForm());

        // Initialize database
        databaseHelper = new database(this);

        // Initialize RecyclerView
        managementProductList.setLayoutManager(
                new LinearLayoutManager(this));

        // Add existing catalogue products if database is empty
        seedDefaultProducts();

        // Display all products
        refreshProducts();

        // Add product
        addProductButton.setOnClickListener(v -> addProduct());

        // Search products
        productSearchInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                refreshProducts();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Show all products
        showAllProductsButton.setOnClickListener(v -> {
            productSearchInput.setText("");
            refreshProducts();
        });
    }

    // Expand or collapse Add Product form
    private void toggleAddProductForm() {

        if (addProductForm.getVisibility() == View.VISIBLE) {

            addProductForm.setVisibility(View.GONE);
            addProductArrow.setText("▼");

        } else {

            addProductForm.setVisibility(View.VISIBLE);
            addProductArrow.setText("▲");
        }
    }

    private void addProduct() {

        String name = productNameInput.getText().toString().trim();

        String description =
                productDescriptionInput.getText().toString().trim();

        String category =
                productCategoryInput.getText().toString().trim();

        String price =
                productPriceInput.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty()
                || category.isEmpty() || price.isEmpty()) {

            Toast.makeText(this,
                    R.string.registration_empty_fields,
                    Toast.LENGTH_SHORT).show();

            return;
        }

        long result = databaseHelper.insertProduct(
                name, description, category, price);

        if (result != -1) {

            Toast.makeText(this,
                    R.string.product_added,
                    Toast.LENGTH_SHORT).show();

            productNameInput.setText("");
            productDescriptionInput.setText("");
            productCategoryInput.setText("");
            productPriceInput.setText("");

            productSearchInput.setText("");

            refreshProducts();

            // Collapse form after successful addition
            addProductForm.setVisibility(View.GONE);
            addProductArrow.setText("▼");

        } else {

            Toast.makeText(this,
                    R.string.product_add_failed,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshProducts() {

        String searchText =
                productSearchInput.getText().toString().trim();

        if (searchText.isEmpty()) {
            productList = databaseHelper.getAllProducts();
        } else {
            productList = databaseHelper.searchProducts(searchText);
        }

        adapter = new prodmngAdapter(
                productList,
                databaseHelper,
                this::refreshProducts
        );

        managementProductList.setAdapter(adapter);
    }

    private void seedDefaultProducts() {

        if (!databaseHelper.getAllProducts().isEmpty()) {
            return;
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
