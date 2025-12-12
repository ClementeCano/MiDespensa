package com.example.midespensa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    public interface RecipeClickListener {
        void onRecipeSelected(Recipe recipe);
    }

    private final List<Recipe> recipes;
    private final RecipeClickListener listener;

    public RecipeAdapter(List<Recipe> recipes, RecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.nombre.setText(recipe.getNombre());
        holder.dificultad.setText(recipe.getDificultad());
        holder.imagen.setImageResource(R.drawable.ic_recipe_placeholder);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeSelected(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nombre;
        final TextView dificultad;
        final ImageView imagen;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.recipeName);
            dificultad = itemView.findViewById(R.id.recipeDifficulty);
            imagen = itemView.findViewById(R.id.recipeImage);
        }
    }
}
