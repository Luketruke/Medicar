package com.tp_integrador_p2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.tp_integrador_p2.R;
import com.tp_integrador_p2.conexion.Conexion;
import com.tp_integrador_p2.conexion.usuarioDao;
import com.tp_integrador_p2.entidad.Usuario;
import com.tp_integrador_p2.service.emailService;

public class recuperarPasswordActivity extends AppCompatActivity {
    private EditText emailInput;
    private Button btnEnviarEnlace;
    private TextView tvRegresarLogin;

    private usuarioDao usuarioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_password);

        emailInput = findViewById(R.id.emailInput);
        btnEnviarEnlace = findViewById(R.id.btnEnviarEnlace);
        tvRegresarLogin = findViewById(R.id.tvRegresarLogin);

        usuarioDao = new usuarioDao();

        btnEnviarEnlace.setOnClickListener(v -> enviarEnlaceRecuperacion());

        tvRegresarLogin.setOnClickListener(v -> {
            Intent intent = new Intent(recuperarPasswordActivity.this, loginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void enviarEnlaceRecuperacion() {
        String email = emailInput.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese su correo electrónico.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarCampos(email)) {
            return;
        }

        usuarioDao.verificarCorreo(email, new Conexion.Callback() {
            @Override
            public void onSuccess() {
                usuarioDao.recuperarPassword(email, new emailService.Callback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(
                                    recuperarPasswordActivity.this,
                                    "Correo de recuperación enviado correctamente.",
                                    Toast.LENGTH_LONG
                            ).show();

                            Intent intent = new Intent(recuperarPasswordActivity.this, loginActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(
                                    recuperarPasswordActivity.this,
                                    "Error al enviar el correo: " + error,
                                    Toast.LENGTH_SHORT
                            ).show();
                        });
                    }

                    @Override
                    public void onResult(Object o) {
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(
                            recuperarPasswordActivity.this,
                            "El correo ingresado no está registrado.",
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }
        });
    }

    private boolean validarCampos(String email) {
        if (!Usuario.isEmailValid(email)) {
            Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
