package com.example.midespensa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IngredientAdapter.IngredientListener {

    private IngredientAdapter adapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.topBar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_recipes) {
                startActivity(new Intent(MainActivity.this, RecetasActivity.class));
                return true;
            }
            return false;
        });

        RecyclerView recyclerView = findViewById(R.id.ingredientsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IngredientAdapter(databaseHelper.getIngredients(), this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addIngredient = findViewById(R.id.addIngredientFab);
        addIngredient.setOnClickListener(v -> showAddDialog());
    }

    private void showAddDialog() {
        TextInputLayout inputLayout = new TextInputLayout(this, null, com.google.android.material.R.attr.textInputOutlinedStyle);
        inputLayout.setHint(getString(R.string.new_ingredient_hint));
        inputLayout.setPadding(getResources().getDimensionPixelSize(R.dimen.dialog_padding),
                getResources().getDimensionPixelSize(R.dimen.dialog_padding_small),
                getResources().getDimensionPixelSize(R.dimen.dialog_padding),
                getResources().getDimensionPixelSize(R.dimen.dialog_padding_small));

        TextInputEditText input = new TextInputEditText(inputLayout.getContext());
        inputLayout.addView(input);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.add_ingredient_title)
                .setView(inputLayout)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String nombre = input.getText() != null ? input.getText().toString().trim() : "";
                    boolean inserted = databaseHelper.addIngredient(nombre);
                    if (inserted) {
                        refreshIngredientes();
                        Toast.makeText(this, R.string.ingredient_added, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, R.string.ingredient_error, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void refreshIngredientes() {
        List<String> nuevosIngredientes = new ArrayList<>(databaseHelper.getIngredients());
        adapter.updateData(nuevosIngredientes);
    }

    @Override
    public void onDelete(String ingredient) {
        if (databaseHelper.deleteIngredient(ingredient)) {
            refreshIngredientes();
            Toast.makeText(this, R.string.ingredient_deleted, Toast.LENGTH_SHORT).show();
        }
    }
}
