package com.example.midespensa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MissingAdapter extends RecyclerView.Adapter<MissingAdapter.ViewHolder> {
    private final List<String> faltantes;

    public MissingAdapter(List<String> faltantes) {
        this.faltantes = faltantes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faltante, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nombre.setText(faltantes.get(position));
    }

    @Override
    public int getItemCount() {
        return faltantes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nombre;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.missingName);
        }
    }
}
