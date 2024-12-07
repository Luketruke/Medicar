package com.tp_integrador_p2.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tp_integrador_p2.R;
import com.tp_integrador_p2.entidad.Farmacia;

import java.util.ArrayList;
import java.util.List;

public class farmaciaAdapter extends RecyclerView.Adapter<farmaciaAdapter.ViewHolder> {

    private final Context context;
    private final List<Farmacia> farmacias;

    public farmaciaAdapter(Context context, List<Farmacia> farmacias) {
        this.context = context;
        this.farmacias = new ArrayList<>(farmacias);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_farmacia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Farmacia farmacia = farmacias.get(position);

        holder.tvPharmacyName.setText(farmacia.getNombre());
        holder.tvPharmacyAddress.setText(farmacia.getDireccion());
        holder.tvPharmacyHours.setText(farmacia.getHorario());
        holder.tvPharmacyPhone.setText(farmacia.getTelefono());

        holder.btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + farmacia.getTelefono()));
            context.startActivity(intent);
        });

        holder.btnGetDirections.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:0,0?q=" + Uri.encode(farmacia.getDireccion())));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return farmacias.size();
    }

    public void setFarmacias(List<Farmacia> nuevasFarmacias) {
        this.farmacias.clear();
        this.farmacias.addAll(nuevasFarmacias);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPharmacyName, tvPharmacyAddress, tvPharmacyHours, tvPharmacyPhone;
        Button btnCall, btnGetDirections;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPharmacyName = itemView.findViewById(R.id.tvPharmacyName);
            tvPharmacyAddress = itemView.findViewById(R.id.tvPharmacyAddress);
            tvPharmacyHours = itemView.findViewById(R.id.tvPharmacyHours);
            tvPharmacyPhone = itemView.findViewById(R.id.tvPharmacyPhone);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnGetDirections = itemView.findViewById(R.id.btnGetDirections);
        }
    }
}
