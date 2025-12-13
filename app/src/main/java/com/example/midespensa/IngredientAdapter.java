package com.example.midespensa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    public interface IngredientListener {
        void onDelete(String ingredient);
    }

    private final List<String> ingredientes;
    private final IngredientListener listener;

    public IngredientAdapter(List<String> ingredientes, IngredientListener listener) {
        this.ingredientes = ingredientes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ingrediente = ingredientes.get(position);
        holder.nombre.setText(ingrediente);
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(ingrediente);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredientes.size();
    }

    public void updateData(List<String> nuevosIngredientes) {
        List<String> snapshot = new ArrayList<>(nuevosIngredientes);
        ingredientes.clear();
        ingredientes.addAll(snapshot);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nombre;
        final ImageButton deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.ingredientName);
            deleteButton = itemView.findViewById(R.id.deleteIngredient);
        }
    }
}
