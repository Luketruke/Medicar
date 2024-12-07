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
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tp_integrador_p2.R;
import com.tp_integrador_p2.adapter.farmaciaAdapter;
import com.tp_integrador_p2.conexion.farmaciaDao;
import com.tp_integrador_p2.entidad.Farmacia;
import com.tp_integrador_p2.entidad.Sesion;

import java.util.ArrayList;
import java.util.List;

public class farmaciasActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String EMAIL_KEY = "email_key";
    public static final String PASSWORD_KEY = "password_key";

    private RecyclerView recyclerFarmacias;
    private farmaciaAdapter adapter;
    private farmaciaDao dao;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private Button btnVolver;

    SharedPreferences sharedPreferences;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(EMAIL_KEY, null);

        Log.d("farmaciasActivity", "Estado de Sesion: " + Sesion.getUsuarioActual());

        if (email == null || email.isEmpty() || Sesion.getUsuarioActual() == null) {
            Log.d("farmaciasActivity", "No hay usuario en la sesión. Redirigiendo a login...");
            Intent intent = new Intent(this, loginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Log.d("farmaciasActivity", "Sesión activa con email: " + email);
        setContentView(R.layout.farmacias_cercanas);

        recyclerFarmacias = findViewById(R.id.recyclerFarmacias);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutFarmacias);
        btnVolver = findViewById(R.id.btnVolver);

        recyclerFarmacias.setLayoutManager(new LinearLayoutManager(this));
        adapter = new farmaciaAdapter(this, new ArrayList<>());
        recyclerFarmacias.setAdapter(adapter);

        dao = new farmaciaDao();

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(farmaciasActivity.this, dashboardActivity.class);
            startActivity(intent);
            finish();
        });

        swipeRefreshLayout.setOnRefreshListener(this::verificarPermisoUbicacion);

        verificarPermisoUbicacion();
    }

    private void verificarPermisoUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            cargarFarmaciasCercanas();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cargarFarmaciasCercanas();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void cargarFarmaciasCercanas() {
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
            cargarDatosFarmacias(userLat, userLon);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarDatosFarmacias(double userLat, double userLon) {
        dao.buscarFarmacias(userLat, userLon, new farmaciaDao.CallbackFarmacia() {
            @Override
            public void onResult(List<Farmacia> farmacias) {
                runOnUiThread(() -> {
                    adapter.setFarmacias(farmacias);
                    swipeRefreshLayout.setRefreshing(false);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(farmaciasActivity.this, "Error al cargar farmacias: " + error, Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
            }
        });
    }
}
