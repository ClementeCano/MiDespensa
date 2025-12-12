package com.example.midespensa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IngredientAdapter.IngredientListener {

    private IngredientAdapter adapter;
    private DatabaseHelper databaseHelper;
    private final List<String> ingredientes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        RecyclerView recyclerView = findViewById(R.id.ingredientsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ingredientes.addAll(databaseHelper.getIngredients());
        adapter = new IngredientAdapter(ingredientes, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addIngredient = findViewById(R.id.addIngredientFab);
        addIngredient.setOnClickListener(v -> showAddDialog());

        MaterialButton recipesButton = findViewById(R.id.openRecipesButton);
        recipesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecetasActivity.class);
            startActivity(intent);
        });
    }

    private void showAddDialog() {
        EditText input = new EditText(this);
        input.setHint(R.string.new_ingredient_hint);
        new AlertDialog.Builder(this)
                .setTitle(R.string.add_ingredient_title)
                .setView(input)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String nombre = input.getText().toString();
                    boolean inserted = databaseHelper.addIngredient(nombre);
                    if (inserted) {
                        refreshIngredientes();
                        Toast.makeText(this, R.string.ingredient_added, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, R.string.ingredient_error, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void refreshIngredientes() {
        ingredientes.clear();
        ingredientes.addAll(databaseHelper.getIngredients());
        adapter.updateData(ingredientes);
    }

    @Override
    public void onDelete(String ingredient) {
        if (databaseHelper.deleteIngredient(ingredient)) {
            refreshIngredientes();
            Toast.makeText(this, R.string.ingredient_deleted, Toast.LENGTH_SHORT).show();
        }
    }
}
