package com.example.midespensa;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class FaltantesActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_NAME = "extra_recipe_name";
    public static final String EXTRA_RECIPE_INGREDIENTS = "extra_recipe_ingredients";
    private static final String CHANNEL_ID = "faltantes_channel";

    private DatabaseHelper databaseHelper;
    private final List<String> faltantes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faltantes);

        databaseHelper = new DatabaseHelper(this);
        createChannel();

        String recipeName = getIntent().getStringExtra(EXTRA_RECIPE_NAME);
        if (recipeName == null || recipeName.isEmpty()) {
            recipeName = getString(R.string.recipes_title);
        }
        ArrayList<String> recipeIngredients = getIntent().getStringArrayListExtra(EXTRA_RECIPE_INGREDIENTS);
        if (recipeIngredients == null) {
            recipeIngredients = new ArrayList<>();
        }

        MaterialToolbar toolbar = findViewById(R.id.missingToolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView title = findViewById(R.id.recipeTitle);
        title.setText(getString(R.string.missing_title, recipeName));

        List<String> userIngredients = databaseHelper.getIngredients();
        faltantes.addAll(databaseHelper.calculateMissingIngredients(recipeIngredients, userIngredients));

        RecyclerView recyclerView = findViewById(R.id.missingRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MissingAdapter(faltantes));

        if (!faltantes.isEmpty()) {
            boolean added = databaseHelper.addIngredientsToShoppingList(recipeName, faltantes);
            if (added) {
                Snackbar.make(recyclerView, R.string.shopping_list_synced, Snackbar.LENGTH_SHORT).show();
            }
        }

        MaterialButton saveButton = findViewById(R.id.saveListButton);
        String finalRecipeName = recipeName;
        saveButton.setOnClickListener(v -> {
            if (faltantes.isEmpty()) {
                Snackbar.make(v, R.string.no_missing_ingredients, Snackbar.LENGTH_SHORT).show();
                return;
            }
            boolean saved = databaseHelper.addIngredientsToShoppingList(finalRecipeName, faltantes);
            if (saved) {
                Snackbar.make(v, R.string.shopping_list_saved, Snackbar.LENGTH_LONG).show();
                showNotification(finalRecipeName, faltantes.size());
            } else {
                Toast.makeText(this, R.string.shopping_list_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(String recipeName, int missingCount) {
        String content = getString(R.string.notification_body, missingCount, recipeName);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_recipe_placeholder)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1001, builder.build());
    }
}
