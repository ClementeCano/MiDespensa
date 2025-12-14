package com.example.midespensa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "midespensa.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_INGREDIENTES = "tabla_ingredientes";
    public static final String TABLE_RECETAS = "tabla_recetas";
    public static final String TABLE_LISTA_COMPRA = "tabla_lista_compra";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_INGREDIENTES + " (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT UNIQUE)");
        db.execSQL("CREATE TABLE " + TABLE_RECETAS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, dificultad TEXT, ingredientes TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_LISTA_COMPRA + " (id INTEGER PRIMARY KEY AUTOINCREMENT, receta TEXT, faltantes TEXT, creado_en INTEGER)");
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECETAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTA_COMPRA);
        onCreate(db);
    }

    public boolean addIngredient(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre.trim());
        long result = db.insertWithOnConflict(TABLE_INGREDIENTES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        return result != -1;
    }

    public boolean deleteIngredient(String nombre) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_INGREDIENTES, "nombre = ?", new String[]{nombre});
        return rows > 0;
    }

    public List<String> getIngredients() {
        List<String> ingredientes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT nombre FROM " + TABLE_INGREDIENTES + " ORDER BY nombre", null)) {
            while (cursor.moveToNext()) {
                ingredientes.add(cursor.getString(0));
            }
        }
        return ingredientes;
    }

    public void seedDefaultRecipes() {
        SQLiteDatabase db = getWritableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RECETAS, null)) {
            if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
                seedRecipes(db);
            }
        }
    }

    private void seedRecipes(SQLiteDatabase db) {
        saveRecipe(db, new Recipe("Bizcocho", "Fácil", Arrays.asList("huevos", "azúcar", "harina", "aceite")));
        saveRecipe(db, new Recipe("Tortilla de patatas", "Fácil", Arrays.asList("huevos", "patatas", "aceite", "sal")));
        saveRecipe(db, new Recipe("Ensalada verde", "Muy fácil", Arrays.asList("lechuga", "tomate", "aceite", "vinagre", "sal")));
    }

    public void saveRecipe(Recipe recipe) {
        SQLiteDatabase db = getWritableDatabase();
        saveRecipe(db, recipe);
    }

    private void saveRecipe(SQLiteDatabase db, Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put("nombre", recipe.getNombre());
        values.put("dificultad", recipe.getDificultad());
        values.put("ingredientes", String.join(",", recipe.getIngredientes()));
        db.insert(TABLE_RECETAS, null, values);
    }

    public List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT nombre, dificultad, ingredientes FROM " + TABLE_RECETAS + " ORDER BY nombre", null)) {
            while (cursor.moveToNext()) {
                String nombre = cursor.getString(0);
                String dificultad = cursor.getString(1);
                String ingredientesTxt = cursor.getString(2);
                List<String> ingredientes = new ArrayList<>();
                if (ingredientesTxt != null && !ingredientesTxt.isEmpty()) {
                    ingredientes.addAll(Arrays.asList(ingredientesTxt.split(",")));
                }
                recipes.add(new Recipe(nombre, dificultad, ingredientes));
            }
        }
        return recipes;
    }

    public List<String> calculateMissingIngredients(List<String> recetaIngredientes, List<String> userIngredientes) {
        Set<String> normalizedUser = new HashSet<>();
        for (String ingrediente : userIngredientes) {
            normalizedUser.add(ingrediente.trim().toLowerCase());
        }
        List<String> faltantes = new ArrayList<>();
        for (String ingrediente : recetaIngredientes) {
            String normalized = ingrediente.trim();
            if (!normalized.isEmpty() && !normalizedUser.contains(normalized.toLowerCase())) {
                faltantes.add(normalized);
            }
        }
        return faltantes;
    }

    public boolean saveShoppingList(String receta, List<String> faltantes) {
        if (receta == null || faltantes == null) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("receta", receta);
        values.put("faltantes", String.join(",", faltantes));
        values.put("creado_en", System.currentTimeMillis());
        long result = db.insert(TABLE_LISTA_COMPRA, null, values);
        return result != -1;
    }

    public boolean addIngredientsToShoppingList(String receta, List<String> faltantes) {
        if (faltantes == null || faltantes.isEmpty()) {
            return false;
        }
        return saveShoppingList(receta == null ? "" : receta, faltantes);
    }

    public List<String> getShoppingListItems() {
        Set<String> uniqueItems = new HashSet<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT faltantes FROM " + TABLE_LISTA_COMPRA + " ORDER BY creado_en DESC", null)) {
            while (cursor.moveToNext()) {
                String faltantesTxt = cursor.getString(0);
                if (faltantesTxt != null && !faltantesTxt.isEmpty()) {
                    for (String item : faltantesTxt.split(",")) {
                        if (!item.trim().isEmpty()) {
                            uniqueItems.add(item.trim());
                        }
                    }
                }
            }
        }
        List<String> result = new ArrayList<>(uniqueItems);
        result.sort(String::compareToIgnoreCase);
        return result;
    }

    public void clearShoppingList() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_LISTA_COMPRA, null, null);
    }
}
