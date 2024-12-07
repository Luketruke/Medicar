package com.tp_integrador_p2.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tp_integrador_p2.R;
import com.tp_integrador_p2.adapter.centroMedicoAdapter;
import com.tp_integrador_p2.conexion.centroMedicoDao;
import com.tp_integrador_p2.entidad.CentroMedico;
import com.tp_integrador_p2.entidad.Sesion;

import java.util.ArrayList;
import java.util.List;

public class centroMedicoActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String EMAIL_KEY = "email_key";
    public static final String PASSWORD_KEY = "password_key";

    private RecyclerView recyclerCentrosMedicos;
    private centroMedicoAdapter adapter;
    private com.tp_integrador_p2.conexion.centroMedicoDao centroMedicoDao;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;
    private static final int REQUEST_LOCATION_PERMISSION = 100;

    SharedPreferences sharedPreferences;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(EMAIL_KEY, null);

        Log.d("centroMedicoActivity", "Estado de Sesion: " + Sesion.getUsuarioActual());

        if (email == null || email.isEmpty() || Sesion.getUsuarioActual() == null) {
            Log.d("centroMedicoActivity", "No hay usuario en la sesión. Redirigiendo a login...");
            Intent intent = new Intent(this, loginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Log.d("centroMedicoActivity", "Sesión activa con email: " + email);
        setContentView(R.layout.centros_medicos_cercanos);

        recyclerCentrosMedicos = findViewById(R.id.recyclerCentrosMedicos);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerCentrosMedicos.setLayoutManager(new LinearLayoutManager(this));

        centroMedicoDao = new centroMedicoDao();
        adapter = new centroMedicoAdapter(this, new ArrayList<>());
        recyclerCentrosMedicos.setAdapter(adapter);

        findViewById(R.id.btnVolver).setOnClickListener(v -> {
            Intent intent = new Intent(centroMedicoActivity.this, dashboardActivity.class);
            startActivity(intent);
            finish();
        });

        verificarPermisoUbicacion();

        swipeRefreshLayout.setOnRefreshListener(this::verificarPermisoUbicacion);
    }

    private void verificarPermisoUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            cargarCentrosMedicosCercanos();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cargarCentrosMedicosCercanos();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void cargarCentrosMedicosCercanos() {
        swipeRefreshLayout.setRefreshing(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            double userLat = location.getLatitude();
            double userLon = location.getLongitude();
            cargarDatosCentrosMedicos(userLat, userLon);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarDatosCentrosMedicos(double userLat, double userLon) {
        centroMedicoDao.buscarCentrosMedicos(userLat, userLon, new com.tp_integrador_p2.conexion.centroMedicoDao.CallbackCentroMedico() {
            @Override
            public void onResult(List<CentroMedico> centrosMedicos) {
                runOnUiThread(() -> {
                    adapter.setCentrosMedicos(centrosMedicos);
                    swipeRefreshLayout.setRefreshing(false);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(centroMedicoActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
            }
        });
    }
}
