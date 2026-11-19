
package com.example.mobile_comp_prj;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class delete_dialogue extends DialogFragment {

    private static final String ARG_PRODUCT_NAME = "product_name";

    private DeleteProductListener listener;

    public interface DeleteProductListener {
        void onDeleteConfirmed(String productName);
    }

    public static delete_dialogue newInstance(String productName) {

        delete_dialogue fragment = new delete_dialogue();

        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_NAME, productName);

        fragment.setArguments(args);

        return fragment;
    }

    public void setListener(DeleteProductListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        String productName = getArguments() != null
                ? getArguments().getString(ARG_PRODUCT_NAME)
                : "";

        return new AlertDialog.Builder(requireContext())
                .setTitle("Delete Product")
                .setMessage(
                        "Are you sure you want to delete "
                                + productName + "?"
                )
                .setPositiveButton("Yes, Delete", (dialog, which) -> {

                    if (listener != null) {
                        listener.onDeleteConfirmed(productName);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                    Toast.makeText(
                            requireContext(),
                            "Deletion cancelled",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .create();
    }
}
