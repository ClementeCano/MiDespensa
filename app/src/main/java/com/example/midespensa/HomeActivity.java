package com.example.midespensa;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MaterialButton openPantry = findViewById(R.id.openPantryButton);
        MaterialButton openShopping = findViewById(R.id.openShoppingButton);
        MaterialButton openRecipes = findViewById(R.id.openRecipesButton);

        openPantry.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        openShopping.setOnClickListener(v -> startActivity(new Intent(this, ShoppingListActivity.class)));
        openRecipes.setOnClickListener(v -> startActivity(new Intent(this, RecetasActivity.class)));
    }
}
