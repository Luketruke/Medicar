package com.tp_integrador_p2.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tp_integrador_p2.R;
import com.tp_integrador_p2.entidad.Sesion;

import java.util.ArrayList;
import java.util.List;

public class mapaActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String EMAIL_KEY = "email_key";
    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private String centerName;
    private LatLng userLatLng;

    SharedPreferences sharedPreferences;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(EMAIL_KEY, null);

        Log.d("mapaActivity", "Estado de Sesion: " + Sesion.getUsuarioActual());

        if (email == null || email.isEmpty() || Sesion.getUsuarioActual() == null) {
            Log.d("mapaActivity", "No hay usuario en la sesión. Redirigiendo a login...");
            Intent intent = new Intent(this, loginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Log.d("mapaActivity", "Sesión activa con email: " + email);
        setContentView(R.layout.activity_mapa);

        latitude = getIntent().getDoubleExtra("latitude", 0.0);
        longitude = getIntent().getDoubleExtra("longitude", 0.0);
        centerName = getIntent().getStringExtra("name");

        Button btnVolverCentros = findViewById(R.id.btnVolverCentros);
        Button btnTomarRuta = findViewById(R.id.btnTomarRuta);

        btnVolverCentros.setOnClickListener(v -> finish());

        btnTomarRuta.setOnClickListener(v -> {
            if (userLatLng != null) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(this, "Google Maps no esta instalado", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Ubicacion del usuario no disponible", Toast.LENGTH_SHORT).show();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng centerLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(centerLocation).title(centerName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLocation, 12));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (userLocation != null) {
                userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                trazarRuta(userLatLng.latitude, userLatLng.longitude, latitude, longitude);
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicacion actual", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }
    }

    private void trazarRuta(double userLat, double userLng, double destLat, double destLng) {
        List<LatLng> path = new ArrayList<>();
        path.add(new LatLng(userLat, userLng));
        path.add(new LatLng(destLat, destLng));

        mMap.addPolyline(new PolylineOptions().addAll(path).color(0xFF0000FF).width(10));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
