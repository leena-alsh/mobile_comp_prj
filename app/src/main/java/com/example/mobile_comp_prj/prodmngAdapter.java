
package com.example.mobile_comp_prj;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class prodmngAdapter extends RecyclerView.Adapter<prodmngAdapter.ProductViewHolder> {

    private ArrayList<product> productList;
    private database databaseHelper;
    private Runnable refreshList;

    public prodmngAdapter(
            ArrayList<product> productList,
            database databaseHelper,
            Runnable refreshList) {

        this.productList = productList;
        this.databaseHelper = databaseHelper;
        this.refreshList = refreshList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.prodmng_item, parent, false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ProductViewHolder holder, int position) {

        product currentProduct = productList.get(position);

        holder.productName.setText(currentProduct.getName());
        holder.productDescription.setText(
                currentProduct.getDescription());
        holder.productCategory.setText(
                currentProduct.getCategory());
        holder.productPrice.setText(
                currentProduct.getPrice());

        holder.deleteButton.setOnClickListener(v -> {

            FragmentActivity activity =
                    (FragmentActivity) holder.itemView.getContext();

            delete_dialogue dialog =
                    delete_dialogue.newInstance(
                            currentProduct.getName());

            dialog.setListener(productName -> {

                int result = databaseHelper.deleteProduct(productName);

                if (result > 0) {

                    Toast.makeText(
                            activity,
                            "Product deleted successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    refreshList.run();

                } else {

                    Toast.makeText(
                            activity,
                            "Could not delete product",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });

            dialog.show(
                    activity.getSupportFragmentManager(),
                    "DeleteProductDialog"
            );
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder
            extends RecyclerView.ViewHolder {

        TextView productName;
        TextView productDescription;
        TextView productCategory;
        TextView productPrice;
        Button deleteButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(
                    R.id.managementProductName);

            productDescription = itemView.findViewById(
                    R.id.managementProductDescription);

            productCategory = itemView.findViewById(
                    R.id.managementProductCategory);

            productPrice = itemView.findViewById(
                    R.id.managementProductPrice);

            deleteButton = itemView.findViewById(
                    R.id.deleteProductButton);
        }
    }
}
