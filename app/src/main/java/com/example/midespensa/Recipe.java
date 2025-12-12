package com.example.midespensa;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private final String nombre;
    private final String dificultad;
    private final List<String> ingredientes;

    public Recipe(String nombre, String dificultad, List<String> ingredientes) {
        this.nombre = nombre;
        this.dificultad = dificultad;
        this.ingredientes = new ArrayList<>(ingredientes);
    }

    public String getNombre() {
        return nombre;
    }

    public String getDificultad() {
        return dificultad;
    }

    public List<String> getIngredientes() {
        return new ArrayList<>(ingredientes);
    }
}
