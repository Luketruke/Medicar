package com.tp_integrador_p2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tp_integrador_p2.R;
import com.tp_integrador_p2.conexion.Conexion;
import com.tp_integrador_p2.conexion.usuarioDao;

public class launcherActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String EMAIL_KEY = "email_key";
    public static final String PASSWORD_KEY = "password_key";
    private usuarioDao usuarioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loadscreen);
        new Handler().postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            String email = sharedPreferences.getString(EMAIL_KEY, null);
            String password = sharedPreferences.getString(PASSWORD_KEY, null);

            if (email != null && password != null) {
                usuarioDao = new usuarioDao();
                Log.d("launcherActivity", "Attempting to rehydrate session with saved credentials.");

                usuarioDao.loginUsuario(email, password, new Conexion.LoginCallback() {
                    @Override
                    public void onLoginSuccess() {
                        Log.d("Sesion", "Rehydrated session for user: " + email);

                        Intent intent = new Intent(launcherActivity.this, dashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onLoginFailure(String message) {
                        Log.e("Sesion", "Failed to rehydrate session: " + message);
                        runOnUiThread(() -> Toast.makeText(launcherActivity.this, "Session rehydration failed: " + message, Toast.LENGTH_SHORT).show());
                        navigateToLogin();
                    }
                });
            } else {
                Log.d("launcherActivity", "No session found. Redirecting to login.");
                navigateToLogin();
            }
        }, 1000);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(launcherActivity.this, loginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
