package com.example.elsoalkalmazasom.fregment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.elsoalkalmazasom.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddProductFragment extends Fragment {

    private EditText editTextName, editTextDescription, editTextPrice;
    private Button buttonAddProduct;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        editTextName = view.findViewById(R.id.editTextProductName);
        editTextDescription = view.findViewById(R.id.editTextProductDescription);
        editTextPrice = view.findViewById(R.id.editTextProductPrice);
        buttonAddProduct = view.findViewById(R.id.buttonAddProduct);
        firestore = FirebaseFirestore.getInstance();

        buttonAddProduct.setOnClickListener(v -> addProduct());

        return view;
    }

    private void addProduct() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priceText = editTextPrice.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceText)) {
            Toast.makeText(getContext(), "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Érvénytelen ár!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> product = new HashMap<>();
        product.put("nev", name);
        product.put("leiras", description);
        product.put("ar", price);

        firestore.collection("products")
                .add(product)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(getContext(), "Termék sikeresen hozzáadva!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Hiba történt: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
