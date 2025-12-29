package com.example.midespensa;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ShoppingListAdapter adapter;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        databaseHelper = new DatabaseHelper(this);
        emptyMessage = findViewById(R.id.emptyShoppingMessage);

        MaterialToolbar toolbar = findViewById(R.id.shoppingToolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.shoppingRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShoppingListAdapter();
        recyclerView.setAdapter(adapter);

        MaterialButton clearButton = findViewById(R.id.clearShoppingButton);
        clearButton.setOnClickListener(this::clearShoppingList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        List<String> items = databaseHelper.getShoppingListItems();
        adapter.submitList(items);
        emptyMessage.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void clearShoppingList(View view) {
        databaseHelper.clearShoppingList();
        loadItems();
        Snackbar.make(view, R.string.shopping_list_cleared, Snackbar.LENGTH_SHORT).show();
    }
}
