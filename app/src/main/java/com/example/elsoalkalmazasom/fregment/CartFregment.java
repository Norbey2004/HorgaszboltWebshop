package com.example.elsoalkalmazasom.fregment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elsoalkalmazasom.ProductAdapter;
import com.example.elsoalkalmazasom.R;
import com.example.elsoalkalmazasom.model.Termek;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CartFregment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textViewEmpty;
    private Button buttonPurchase;
    private ProductAdapter adapter;
    private List<Termek> termekList;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fregment_cart, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCart);
        textViewEmpty = view.findViewById(R.id.textViewEmptyCart);
        buttonPurchase = view.findViewById(R.id.buttonPurchase);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        termekList = new ArrayList<>();
        adapter = new ProductAdapter(getContext(), termekList, true);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        buttonPurchase.setOnClickListener(v -> vasarlas());

        loadCartItems();
        return view;
    }

    private void vasarlas() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestore.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(getContext(), "A kosarad üres!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 🔄 Gomb animáció
                    buttonPurchase.animate()
                            .rotationBy(360f)
                            .setDuration(800)
                            .withEndAction(() -> {
                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    Termek termek = doc.toObject(Termek.class);
                                    if (termek != null && termek.getId() != null) {
                                        firestore.collection("products").document(termek.getId()).delete();
                                    }
                                    doc.getReference().delete();
                                }

                                Toast.makeText(getContext(), "🎉 Vásárlás sikeres!", Toast.LENGTH_SHORT).show();
                                loadCartItems();
                            }).start();
                });
    }

    private void loadCartItems() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestore.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    termekList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Termek termek = doc.toObject(Termek.class);
                        if (termek != null) {
                            termek.setId(doc.getId());
                            termekList.add(termek);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    // Láthatóság beállítása
                    if (termekList.isEmpty()) {
                        textViewEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        getView().findViewById(R.id.buttonPurchase).setVisibility(View.GONE); // 🔴 ELTŰNTETJÜK A GOMBOT
                    } else {
                        textViewEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        getView().findViewById(R.id.buttonPurchase).setVisibility(View.VISIBLE); // ✅ MEGJELENÍTJÜK A GOMBOT
                    }

                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems();
    }
}
