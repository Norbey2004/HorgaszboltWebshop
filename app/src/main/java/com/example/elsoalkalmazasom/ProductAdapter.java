package com.example.elsoalkalmazasom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elsoalkalmazasom.model.Termek;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Termek> termekList;
    private boolean isCartMode;

    public ProductAdapter(Context context, List<Termek> termekList, boolean isCartMode) {
        this.context = context;
        this.termekList = termekList;
        this.isCartMode = isCartMode;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Termek termek = termekList.get(position);

        holder.textViewName.setText(termek.getNev());
        holder.textViewDescription.setText(termek.getLeiras());
        holder.textViewPrice.setText(termek.getAr() + " Ft");

        if (isCartMode) {
            holder.buttonDelete.setVisibility(View.GONE);
            holder.buttonEdit.setVisibility(View.GONE);
            holder.buttonAddToCart.setVisibility(View.GONE);
            holder.buttonRemoveFromCart.setVisibility(View.VISIBLE);
        } else {
            holder.buttonDelete.setVisibility(View.VISIBLE);
            holder.buttonEdit.setVisibility(View.VISIBLE);
            holder.buttonAddToCart.setVisibility(View.VISIBLE);
            holder.buttonRemoveFromCart.setVisibility(View.GONE);
        }

        holder.buttonDelete.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("products")
                    .document(termek.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        termekList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Törölve", Toast.LENGTH_SHORT).show();
                    });
        });

        holder.buttonEdit.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).openEditProduct(termek);
            }
        });

        holder.buttonAddToCart.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Győződjünk meg róla, hogy az ID nem null
            if (termek.getId() == null || termek.getId().isEmpty()) {
                termek.setId(FirebaseFirestore.getInstance().collection("products").document().getId());
            }

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("cart")
                    .document(termek.getId())
                    .set(termek)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(context, "Hozzáadva a kosárhoz", Toast.LENGTH_SHORT).show()
                    );
        });



        holder.buttonRemoveFromCart.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            Termek currentTermek = termekList.get(currentPosition);

            Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
            holder.itemView.startAnimation(fadeOut);

            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(userId)
                            .collection("cart")
                            .document(currentTermek.getId())
                            .delete()
                            .addOnSuccessListener(unused -> {
                                termekList.remove(currentPosition);
                                notifyItemRemoved(currentPosition);
                                Toast.makeText(context, "Eltávolítva a kosárból", Toast.LENGTH_SHORT).show();
                                if (cartUpdateListener != null) {
                                    cartUpdateListener.onCartUpdated();
                                }
                            });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });



    }

    @Override
    public int getItemCount() {
        return termekList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewDescription, textViewPrice;
        Button buttonDelete, buttonEdit, buttonAddToCart, buttonRemoveFromCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
            buttonRemoveFromCart = itemView.findViewById(R.id.buttonRemoveFromCart);
        }
    }
    private CartUpdateListener cartUpdateListener;

    public ProductAdapter(Context context, List<Termek> termekList, boolean isCartMode, CartUpdateListener listener) {
        this.context = context;
        this.termekList = termekList;
        this.isCartMode = isCartMode;
        this.cartUpdateListener = listener;
    }

}
