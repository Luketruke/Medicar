package com.tp_integrador_p2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tp_integrador_p2.R;
import com.tp_integrador_p2.conexion.consultaMedicamentoDao;
import com.tp_integrador_p2.entidad.ConsultaMedicamento;
import com.tp_integrador_p2.entidad.Sesion;

import java.text.SimpleDateFormat;
import java.util.List;

public class historialBusquedaActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String EMAIL_KEY = "email_key";
    public static final String PASSWORD_KEY = "password_key";

    private LinearLayout historialContainer;
    private Button btnVolver;
    SharedPreferences sharedPreferences;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(EMAIL_KEY, null);

        Log.d("historialBusquedaActivity", "Estado de Sesion: " + Sesion.getUsuarioActual());

        if (email == null || email.isEmpty() || Sesion.getUsuarioActual() == null) {
            Log.d("historialBusquedaActivity", "No hay usuario en la sesión. Redirigiendo a login...");
            Intent intent = new Intent(this, loginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Log.d("historialBusquedaActivity", "Sesión activa con email: " + email);
        setContentView(R.layout.activity_historial_busquedas);

        historialContainer = findViewById(R.id.historialContainer);
        btnVolver = findViewById(R.id.btnVolver);

        cargarHistorial();

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(historialBusquedaActivity.this, dashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void cargarHistorial() {
        try {
            consultaMedicamentoDao dao = new consultaMedicamentoDao();
            dao.obtenerHistorial(new consultaMedicamentoDao.HistorialCallback() {
                @Override
                public void onSuccess(List<ConsultaMedicamento> historial) {
                    runOnUiThread(() -> {
                        if (historial.isEmpty()) {
                            Toast.makeText(historialBusquedaActivity.this, "No se encontraron resultados.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                        for (ConsultaMedicamento consulta : historial) {
                            View vistaHistorial = getLayoutInflater().inflate(R.layout.item_historial, null);

                            TextView tvBusqueda = vistaHistorial.findViewById(R.id.tvBusqueda);
                            TextView tvFecha = vistaHistorial.findViewById(R.id.tvFecha);

                            tvBusqueda.setText(consulta.getBusqueda());
                            tvFecha.setText("Fecha: " + dateFormat.format(consulta.getFecha()));

                            historialContainer.addView(vistaHistorial);
                        }
                    });
                }
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(historialBusquedaActivity.this, "Error cargando historial: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            Log.e("HistorialBusqueda", "Error cargando historial", e);
            Toast.makeText(this, "Error cargando historial", Toast.LENGTH_SHORT).show();
        }
    }
}
