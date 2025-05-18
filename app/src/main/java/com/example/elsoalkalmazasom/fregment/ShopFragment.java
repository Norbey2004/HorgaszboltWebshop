package com.example.elsoalkalmazasom.fregment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elsoalkalmazasom.R;
import com.example.elsoalkalmazasom.ProductAdapter;
import com.example.elsoalkalmazasom.model.Termek;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Termek> termekList;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        termekList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), termekList,false);
        recyclerView.setAdapter(productAdapter);

        firestore = FirebaseFirestore.getInstance();

        loadProducts();

        return view;
    }


    private void loadProducts() {

        firestore.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    termekList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Termek termek = doc.toObject(Termek.class);
                        termek.setId(doc.getId()); // 🔴 EZ HIÁNYZIK – e nélkül nem fog menni a törlés!
                        termekList.add(termek);
                    }

                    productAdapter.notifyDataSetChanged();
                });
    }
    @Override
    public void onResume() {
        super.onResume();
        loadProducts(); // újratölti a listát minden alkalommal
    }


}
