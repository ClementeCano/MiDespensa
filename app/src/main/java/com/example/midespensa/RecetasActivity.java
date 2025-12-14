package com.example.midespensa;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class RecetasActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener {

    private DatabaseHelper databaseHelper;
    private List<Recipe> recipes = new ArrayList<>();
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recetas);

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.seedDefaultRecipes();
        recipes = databaseHelper.getRecipes();

        MaterialToolbar toolbar = findViewById(R.id.recipesToolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recipesRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(recipes, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        new AlertDialog.Builder(this)
                .setTitle(recipe.getNombre())
                .setMessage(getString(R.string.ingredients_required, String.join(", ", recipe.getIngredientes())))
                .setPositiveButton(R.string.view_missing, (dialog, which) -> openMissing(recipe))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void openMissing(Recipe recipe) {
        Intent intent = new Intent(this, FaltantesActivity.class);
        intent.putExtra(FaltantesActivity.EXTRA_RECIPE_NAME, recipe.getNombre());
        intent.putStringArrayListExtra(FaltantesActivity.EXTRA_RECIPE_INGREDIENTS, new ArrayList<>(recipe.getIngredientes()));
        startActivity(intent);
    }
}
