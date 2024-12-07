package com.tp_integrador_p2.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import com.tp_integrador_p2.entidad.Sesion;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tp_integrador_p2.R;

import java.util.Objects;

public class dashboardActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String EMAIL_KEY = "email_key";
    public static final String PASSWORD_KEY = "password_key";
    private Button btnBuscarMedicamento, btnFarmaciasCercanas, btnCentrosMedicos, btnHistorialBusqueda, btnPerfilUsuario, btnCerrarSesion;

    private static final int REQUEST_LOCATION_PERMISSION = 100;
    SharedPreferences sharedPreferences;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(EMAIL_KEY, null);

        Log.d("dashboardActivity", "Estado de Sesion: " + Sesion.getUsuarioActual());

        if (email == null || email.isEmpty() || Sesion.getUsuarioActual() == null) {
            Log.d("dashboardActivity", "No hay usuario en la sesión. Redirigiendo a login...");
            Intent intent = new Intent(this, loginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Log.d("dashboardActivity", "Sesión activa con email: " + email);
        setContentView(R.layout.activity_dashboard);

        btnBuscarMedicamento = findViewById(R.id.btnBuscarMedicamento);
        btnFarmaciasCercanas = findViewById(R.id.btnFarmaciasCercanas);
        btnCentrosMedicos = findViewById(R.id.btnCentrosMedicos);
        btnHistorialBusqueda = findViewById(R.id.btnHistorialBusqueda);
        btnPerfilUsuario = findViewById(R.id.btnPerfilUsuario);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnBuscarMedicamento.setOnClickListener(v -> buscarMedicamento());
        btnFarmaciasCercanas.setOnClickListener(v -> verificarPermisoUbicacion("Farmacias"));
        btnCentrosMedicos.setOnClickListener(v -> verificarPermisoUbicacion("CentrosMedicos"));
        btnHistorialBusqueda.setOnClickListener(v -> verHistorialBusqueda());
        btnPerfilUsuario.setOnClickListener(v -> verPerfilUsuario());
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void buscarMedicamento() {
        Intent intent = new Intent(dashboardActivity.this, medicamentoActivity.class);
        startActivity(intent);
    }

    private void verificarPermisoUbicacion(String value) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            if(Objects.equals(value, "CentrosMedicos")) {
                mostrarCentrosMedicos();
            } else if(Objects.equals(value, "Farmacias")){
                mostrarFarmaciasCercanas();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mostrarCentrosMedicos();
            } else {
                Toast.makeText(this, "Permiso de ubicacion denegado. Por favor habilitelo desde los ajustes del telefono", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void mostrarCentrosMedicos() {
        Intent intent = new Intent(dashboardActivity.this, centroMedicoActivity.class);
        startActivity(intent);
    }

    private void mostrarFarmaciasCercanas() {
        Intent intent = new Intent(dashboardActivity.this, farmaciasActivity.class);
        startActivity(intent);
    }

    private void verHistorialBusqueda() {
        Intent intent = new Intent(dashboardActivity.this, historialBusquedaActivity.class);
        startActivity(intent);
        finish();
    }

    private void verPerfilUsuario() {
        Intent intent = new Intent(dashboardActivity.this, perfilUsuarioActivity.class);
        startActivity(intent);
        finish();
    }

    private void cerrarSesion() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Sesion.cerrarSesion();
        editor.clear();
        editor.apply();

        Toast.makeText(dashboardActivity.this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(dashboardActivity.this, loginActivity.class);
        startActivity(intent);
        finish();
    }
}
