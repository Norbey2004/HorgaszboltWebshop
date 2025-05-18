package com.example.elsoalkalmazasom;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elsoalkalmazasom.model.Termek;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProductActivity extends AppCompatActivity {

    private EditText editTextName, editTextDescription, editTextPrice;
    private Button buttonUpdate;

    private FirebaseFirestore firestore;
    private String termekId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextPrice = findViewById(R.id.editTextPrice);
        buttonUpdate = findViewById(R.id.buttonSave);

        firestore = FirebaseFirestore.getInstance();

        // 🔹 Lekérjük az Intenttel kapott adatokat
        termekId = getIntent().getStringExtra("termekId");
        String nev = getIntent().getStringExtra("termekNev");
        String leiras = getIntent().getStringExtra("termekLeiras");
        int ar = getIntent().getIntExtra("termekAr", 0);

        if (termekId == null || nev == null || leiras == null) {
            Toast.makeText(this, "Hiba: hiányzó termékadatok", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editTextName.setText(nev);
        editTextDescription.setText(leiras);
        editTextPrice.setText(String.valueOf(ar));

        buttonUpdate.setOnClickListener(v -> {
            String updatedName = editTextName.getText().toString().trim();
            String updatedDescription = editTextDescription.getText().toString().trim();
            int updatedPrice;

            try {
                updatedPrice = Integer.parseInt(editTextPrice.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Az árnak számnak kell lennie", Toast.LENGTH_SHORT).show();
                return;
            }

            if (updatedName.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(this, "Minden mezőt ki kell tölteni", Toast.LENGTH_SHORT).show();
                return;
            }

            Termek updatedTermek = new Termek(termekId, updatedName, updatedDescription, updatedPrice);

            firestore.collection("products")
                    .document(termekId)
                    .set(updatedTermek)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Sikeres frissítés", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Hiba a frissítés során", Toast.LENGTH_SHORT).show());
        });
    }
}
