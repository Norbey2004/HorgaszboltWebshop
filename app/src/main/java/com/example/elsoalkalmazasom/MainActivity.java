package com.example.elsoalkalmazasom;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.elsoalkalmazasom.fregment.AddProductFragment;
import com.example.elsoalkalmazasom.fregment.CartFregment;
import com.example.elsoalkalmazasom.fregment.ShopFragment;
import com.example.elsoalkalmazasom.model.Termek;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.menu_shop) {
                selectedFragment = new ShopFragment();
            } else if (itemId == R.id.menu_cart) {
                selectedFragment = new CartFregment();
            }else if (itemId == R.id.menu_add) {
                selectedFragment = new AddProductFragment(); // EZ HIÁNYZOTT
            }
            else if (itemId == R.id.menu_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.menu_shop);
    }

    public void openEditProduct(Termek termek) {
        Intent intent = new Intent(this, EditProductActivity.class);
        intent.putExtra("termekId", termek.getId());
        intent.putExtra("termekNev", termek.getNev());
        intent.putExtra("termekLeiras", termek.getLeiras());
        intent.putExtra("termekAr", termek.getAr());
        startActivity(intent);
    }


}
