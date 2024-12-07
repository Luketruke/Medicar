package com.tp_integrador_p2.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tp_integrador_p2.R;
import com.tp_integrador_p2.activity.mapaActivity;
import com.tp_integrador_p2.entidad.CentroMedico;

import java.util.ArrayList;
import java.util.List;

public class centroMedicoAdapter extends RecyclerView.Adapter<centroMedicoAdapter.CentroMedicoViewHolder> {

    private final Context context;
    private final List<CentroMedico> listaCentrosMedicos;

    public centroMedicoAdapter(Context context, List<CentroMedico> listaCentrosMedicos) {
        this.context = context;
        this.listaCentrosMedicos = new ArrayList<>(listaCentrosMedicos);
    }

    @NonNull
    @Override
    public CentroMedicoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_centro_medico, parent, false);
        return new CentroMedicoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CentroMedicoViewHolder holder, int position) {
        CentroMedico centroMedico = listaCentrosMedicos.get(position);

        holder.tvNombre.setText(centroMedico.getNombre());
        holder.tvDireccion.setText("Direccion: " + centroMedico.getDireccion());
        holder.tvHorario.setText("Horario: " + centroMedico.getHorario());
        holder.tvTelefono.setText("Telefono: " + (centroMedico.getTelefono() != null ? centroMedico.getTelefono() : "No disponible"));
        holder.tvProvincia.setText("Provincia: " + centroMedico.getProvincia().getNombreProvincia());
        holder.tvLocalidad.setText("Localidad: " + centroMedico.getLocalidad().getNombreLocalidad());

        holder.btnLlamar.setOnClickListener(v -> {
            if (centroMedico.getTelefono() != null && !centroMedico.getTelefono().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + centroMedico.getTelefono()));
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Telefono no disponible para este centro", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnObtenerDirecciones.setOnClickListener(v -> {
            Intent intent = new Intent(context, mapaActivity.class);
            intent.putExtra("latitude", centroMedico.getLatitud());
            intent.putExtra("longitude", centroMedico.getLongitud());
            intent.putExtra("name", centroMedico.getNombre());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaCentrosMedicos.size();
    }

    public void setCentrosMedicos(List<CentroMedico> nuevosCentros) {
        listaCentrosMedicos.clear();
        listaCentrosMedicos.addAll(nuevosCentros);
        notifyDataSetChanged();
    }

    public static class CentroMedicoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDireccion, tvHorario, tvTelefono, tvProvincia, tvLocalidad;
        Button btnLlamar, btnObtenerDirecciones;

        public CentroMedicoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCentro);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvHorario = itemView.findViewById(R.id.tvHorario);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
            tvProvincia = itemView.findViewById(R.id.tvProvincia);
            tvLocalidad = itemView.findViewById(R.id.tvLocalidad);
            btnLlamar = itemView.findViewById(R.id.btnLlamar);
            btnObtenerDirecciones = itemView.findViewById(R.id.btnObtenerDirecciones);
        }
    }
}
