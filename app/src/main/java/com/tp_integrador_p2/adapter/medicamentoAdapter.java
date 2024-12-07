package com.tp_integrador_p2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tp_integrador_p2.R;
import com.tp_integrador_p2.entidad.Medicamento;

import java.util.ArrayList;
import java.util.List;

public class medicamentoAdapter extends RecyclerView.Adapter<medicamentoAdapter.MedicamentoViewHolder> {

    private final Context context;
    private final List<Medicamento> listaMedicamentos;
    private OnMedicamentoClickListener onMedicamentoClickListener;

    public medicamentoAdapter(Context context, List<Medicamento> listaMedicamentos) {
        this.context = context;
        this.listaMedicamentos = listaMedicamentos != null ? listaMedicamentos : new ArrayList<>();
    }

    @NonNull
    @Override
    public MedicamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medicamento, parent, false);
        return new MedicamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicamentoViewHolder holder, int position) {
        Medicamento medicamento = listaMedicamentos.get(position);

        if (medicamento != null) {
            holder.tvNombre.setText(medicamento.getMarca() != null ? medicamento.getMarca() : "N/A");
            holder.tvDroga.setText("Droga: " + (medicamento.getDroga() != null ? medicamento.getDroga() : "N/A"));
            holder.tvLaboratorio.setText("Laboratorio: " + (medicamento.getLaboratorio() != null ? medicamento.getLaboratorio() : "N/A"));
            holder.tvCobertura.setText("Cobertura: " + medicamento.getCobertura() + "%");
            holder.tvCopago.setText("Copago: " + (medicamento.getCopago() != null ? medicamento.getCopago() : "N/A"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (onMedicamentoClickListener != null && medicamento != null) {
                onMedicamentoClickListener.onMedicamentoClick(medicamento);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaMedicamentos.size();
    }

    public void actualizarMedicamentos(List<Medicamento> nuevosMedicamentos) {
        listaMedicamentos.clear();
        if (nuevosMedicamentos != null && !nuevosMedicamentos.isEmpty()) {
            listaMedicamentos.addAll(nuevosMedicamentos);
        }
        notifyDataSetChanged();
    }

    public void setOnMedicamentoClickListener(OnMedicamentoClickListener listener) {
        this.onMedicamentoClickListener = listener;
    }

    public interface OnMedicamentoClickListener {
        void onMedicamentoClick(Medicamento medicamento);
    }

    public static class MedicamentoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDroga, tvLaboratorio, tvCobertura, tvCopago;

        public MedicamentoViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvmedicamentoNombre);
            tvDroga = itemView.findViewById(R.id.tvmedicamentoDroga);
            tvLaboratorio = itemView.findViewById(R.id.tvmedicamentoLaboratorio);
            tvCobertura = itemView.findViewById(R.id.tvmedicamentoCobertura);
            tvCopago = itemView.findViewById(R.id.tvmedicamentoCopago);
        }
    }
}
