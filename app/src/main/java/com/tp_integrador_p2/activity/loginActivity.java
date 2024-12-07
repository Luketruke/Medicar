package com.tp_integrador_p2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tp_integrador_p2.R;
import com.tp_integrador_p2.conexion.Conexion;
import com.tp_integrador_p2.conexion.usuarioDao;
import com.tp_integrador_p2.entidad.Sesion;

public class loginActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "shared_prefs";
    public static final String EMAIL_KEY = "email_key";
    public static final String PASSWORD_KEY = "password_key";
    private EditText etEmail, etPassword;
    private Button loginButton;
    private TextView registrarse, recuperarpassword;
    private usuarioDao usuarioDao;

    SharedPreferences sharedPreferences;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        Log.d("loginActivity", "Estado de Sesion: " + Sesion.getUsuarioActual());

        email = sharedPreferences.getString(EMAIL_KEY, null);
        password = sharedPreferences.getString(PASSWORD_KEY, null);

        Log.d("loginActivity", "Estado de shared preferences, email " + email + " password: "+ password);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.loginButton);
        registrarse = findViewById(R.id.registrarse);
        recuperarpassword = findViewById(R.id.recuperarpassword);

        loginButton.setOnClickListener(v -> iniciarSesion());
        registrarse.setOnClickListener(v -> {
            Intent intent = new Intent(this, registroActivity.class);
            startActivity(intent);
        });
        recuperarpassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, recuperarPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void iniciarSesion() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        usuarioDao = new usuarioDao();

        usuarioDao.loginUsuario(email, password, new Conexion.LoginCallback() {
            @Override
            public void onLoginSuccess() {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(EMAIL_KEY, email);
                editor.putString(PASSWORD_KEY, password);
                editor.apply();

                runOnUiThread(() -> {
                    Toast.makeText(loginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(loginActivity.this, dashboardActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onLoginFailure(String message) {
                runOnUiThread(() -> Toast.makeText(loginActivity.this, message, Toast.LENGTH_SHORT).show());
            }
        });
    }
}