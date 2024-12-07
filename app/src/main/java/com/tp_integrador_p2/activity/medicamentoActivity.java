package com.tp_integrador_p2.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tp_integrador_p2.R;
import com.tp_integrador_p2.adapter.medicamentoAdapter;
import com.tp_integrador_p2.conexion.medicamentoDao;
import com.tp_integrador_p2.conexion.consultaMedicamentoDao;
import com.tp_integrador_p2.entidad.Medicamento;
import com.tp_integrador_p2.entidad.Sesion;
import com.tp_integrador_p2.entidad.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class medicamentoActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String EMAIL_KEY = "email_key";
    public static final String PASSWORD_KEY = "password_key";

    private EditText txtMedicamento;
    private Button btnVerFarmaciasCercanas, btnVolver, btnLimpiar;
    private RecyclerView recyclerResultados;

    Spinner spinnerDroga, spinnerLaboratorio;
    private medicamentoAdapter adapter;
    private medicamentoDao medicamentoDao;

    SharedPreferences sharedPreferences;
    String email;

    private static final int REQUEST_LOCATION_PERMISSION = 100;

    private static final String TAG = "medicamentoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(EMAIL_KEY, null);

        Log.d("medicamentoActivity", "Estado de Sesion: " + Sesion.getUsuarioActual());

        if (email == null || email.isEmpty() || Sesion.getUsuarioActual() == null) {
            Log.d("medicamentoActivity", "No hay usuario en la sesión. Redirigiendo a login...");
            Intent intent = new Intent(this, loginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Log.d("medicamentoActivity", "Sesión activa con email: " + email);
        setContentView(R.layout.activity_buscar_medicamentos);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Buscar Medicamentos");
        }

        txtMedicamento = findViewById(R.id.txtMedicamento);
        btnVerFarmaciasCercanas = findViewById(R.id.btnVerFarmaciasCercanas);
        btnVolver = findViewById(R.id.btnVolver);
        recyclerResultados = findViewById(R.id.recyclerViewResultados);
        spinnerLaboratorio = findViewById(R.id.spinnerLaboratorio);
        spinnerDroga = findViewById(R.id.spinnerDroga);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        Button btnFiltrar = findViewById(R.id.btnFiltrar);

        medicamentoDao = new medicamentoDao();
        recyclerResultados.setLayoutManager(new LinearLayoutManager(this));
        adapter = new medicamentoAdapter(this, new ArrayList<>());
        recyclerResultados.setAdapter(adapter);

        btnVerFarmaciasCercanas.setOnClickListener(v -> verificarPermisoUbicacion());
        btnVolver.setOnClickListener(v -> volverAlDashboard());
        btnLimpiar.setOnClickListener(v -> limpiarFiltros());

        cargarSpinners();

        medicamentoDao.getTodosMedicamentos(new medicamentoDao.CallbackMedicamento() {
            @Override
            public void onResult(List<Medicamento> medicamentos) {
                adapter.actualizarMedicamentos(medicamentos);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(medicamentoActivity.this, "Error cargando datos: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        btnFiltrar.setOnClickListener(v -> {
            Usuario usuarioActual = Sesion.getUsuarioActual();
            if (usuarioActual == null) {
                Toast.makeText(medicamentoActivity.this, "Debes iniciar sesión para realizar esta acción.", Toast.LENGTH_SHORT).show();
                return;
            }

            String drogaSeleccionada = spinnerDroga.getSelectedItem() != null ? spinnerDroga.getSelectedItem().toString() : "";
            String laboratorioSeleccionado = spinnerLaboratorio.getSelectedItem() != null ? spinnerLaboratorio.getSelectedItem().toString() : "";
            String nombre = txtMedicamento.getText().toString();

            if (!drogaSeleccionada.trim().isEmpty() && nombre.trim().isEmpty()) {
                guardarConsultaMedicamento(drogaSeleccionada);
            } else if (!nombre.trim().isEmpty()) {
                guardarConsultaMedicamento(nombre);
            }

            filtrarMedicamentos(nombre, drogaSeleccionada, laboratorioSeleccionado);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void verificarPermisoUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            mostrarFarmaciasCercanas();
        }
    }

    private void guardarConsultaMedicamento(String busqueda) {
        consultaMedicamentoDao dao = new consultaMedicamentoDao();
        dao.guardarConsultaMedicamento(busqueda, new consultaMedicamentoDao.Callback() {
            @Override
            public void onSuccess() {
                //runOnUiThread(() -> Toast.makeText(medicamentoActivity.this, "Consulta guardada exitosamente.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(String error) {
                //runOnUiThread(() -> Toast.makeText(medicamentoActivity.this, "Error al guardar consulta: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void volverAlDashboard() {
        Intent intent = new Intent(this, dashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void mostrarFarmaciasCercanas() {
        Intent intent = new Intent(medicamentoActivity.this, farmaciasActivity.class);
        startActivity(intent);
    }

    private void filtrarMedicamentos(String nombre, String drogaSeleccionada, String laboratorioSeleccionado) {
        List<Medicamento> medicamentosFiltrados = medicamentoDao.filtrarMedicamentosLocalmente(nombre);

        if (drogaSeleccionada != null && !drogaSeleccionada.isEmpty()) {
            medicamentosFiltrados = medicamentosFiltrados.stream()
                    .filter(m -> m.getDroga().equalsIgnoreCase(drogaSeleccionada))
                    .collect(Collectors.toList());
        }

        if (laboratorioSeleccionado != null && !laboratorioSeleccionado.isEmpty()) {
            medicamentosFiltrados = medicamentosFiltrados.stream()
                    .filter(m -> m.getLaboratorio().equalsIgnoreCase(laboratorioSeleccionado))
                    .collect(Collectors.toList());
        }

        adapter.actualizarMedicamentos(medicamentosFiltrados);
    }

    private void cargarSpinners() {
        medicamentoDao.getFiltros(new medicamentoDao.CallbackFiltros() {
            @Override
            public void onResult(List<String> drogas, List<String> laboratorios) {
                drogas.add(0, "");
                laboratorios.add(0, "");

                ArrayAdapter<String> drogaAdapter = new ArrayAdapter<>(medicamentoActivity.this, android.R.layout.simple_spinner_item, drogas);
                drogaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDroga.setAdapter(drogaAdapter);

                ArrayAdapter<String> laboratorioAdapter = new ArrayAdapter<>(medicamentoActivity.this, android.R.layout.simple_spinner_item, laboratorios);
                laboratorioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLaboratorio.setAdapter(laboratorioAdapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(medicamentoActivity.this, "Error cargando filtros: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limpiarFiltros() {
        txtMedicamento.setText("");
        spinnerDroga.setSelection(0);
        spinnerLaboratorio.setSelection(0);
    }
}
