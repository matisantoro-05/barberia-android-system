package com.example.barberia.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberia.R;
import com.example.barberia.models.Usuario;

import java.util.List;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder> {

    private List<Usuario> usuariosList;
    private OnUsuarioClickListener listener;

    public interface OnUsuarioClickListener {
        void onEditClick(Usuario usuario);
        void onDeleteClick(Usuario usuario);
    }

    public UsuariosAdapter(List<Usuario> usuariosList, OnUsuarioClickListener listener) {
        this.usuariosList = usuariosList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuariosList.get(position);

        holder.tvNombre.setText(usuario.getNombre());
        holder.tvEmail.setText(usuario.getEmail());
        holder.tvTipo.setText(usuario.getTipo());
        holder.tvTelefono.setText(usuario.getTelefono() != null ? usuario.getTelefono() : "Sin teléfono");

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(usuario));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(usuario));
    }

    @Override
    public int getItemCount() {
        return usuariosList.size();
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEmail, tvTipo, tvTelefono;
        Button btnEdit, btnDelete;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}