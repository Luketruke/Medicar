package com.tp_integrador_p2.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.tp_integrador_p2.R;
import com.tp_integrador_p2.conexion.Conexion;
import com.tp_integrador_p2.conexion.usuarioDao;
import com.tp_integrador_p2.entidad.Sesion;
import com.tp_integrador_p2.entidad.Usuario;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class perfilUsuarioActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String EMAIL_KEY = "email_key";
    public static final String PASSWORD_KEY = "password_key";

    private usuarioDao usuarioDao;
    private SharedPreferences sharedPreferences;
    String email;

    private TextView userName, userSurname, userEmail, userPhone, userAddress, userDni, userBirthdate;
    private Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(EMAIL_KEY, null);

        Log.d("perfilUsuarioActivity", "Estado de Sesion: " + Sesion.getUsuarioActual());

        if (email == null || email.isEmpty() || Sesion.getUsuarioActual() == null) {
            Log.d("perfilUsuarioActivity", "No hay usuario en la sesión. Redirigiendo a login...");
            Intent intent = new Intent(this, loginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Log.d("perfilUsuarioActivity", "Sesión activa con email: " + email);
        setContentView(R.layout.activity_perfil_usuario);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(EMAIL_KEY, null);

        Button btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        Button btnCambiarPassword = findViewById(R.id.btnCambiarPassword);

        usuarioDao = new usuarioDao();

        if (email == null) {
            Toast.makeText(this, "No se encontró un usuario logueado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userName = findViewById(R.id.userName);
        userSurname = findViewById(R.id.userSurname);
        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        userAddress = findViewById(R.id.userAddress);
        userDni = findViewById(R.id.userDni);
        userBirthdate = findViewById(R.id.userBirthdate);
        btnVolver = findViewById(R.id.btnVolver);

        cargarDatosEnVista(Sesion.getUsuarioActual());

        btnCambiarPassword.setOnClickListener(v -> mostrarDialogoCambiarPassword());

        btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(perfilUsuarioActivity.this, editarUsuarioActivity.class);
            //intent.putExtra("email", email);
            startActivity(intent);
        });

        btnVolver.setOnClickListener((v -> volverAlDashboard()));
    }

    private void cargarDatosEnVista(Usuario usuario) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String fechaNacimiento = dateFormat.format(usuario.getFechaNacimiento());

        userName.setText(usuario.getNombre());
        userSurname.setText(usuario.getApellido());
        userEmail.setText(usuario.getEmail());
        userPhone.setText(usuario.getTelefono());
        userAddress.setText(usuario.getDireccion());
        userDni.setText(usuario.getDni());
        userBirthdate.setText(fechaNacimiento);
    }

    private void mostrarDialogoCambiarPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_cambiar_password, null);
        builder.setView(dialogView);

        TextInputEditText etPasswordActual = dialogView.findViewById(R.id.etPasswordActual);
        TextInputEditText etPasswordNueva = dialogView.findViewById(R.id.etPasswordNueva);
        TextInputEditText etPasswordConfirmar = dialogView.findViewById(R.id.etPasswordConfirmar);
        Button btnConfirmar = dialogView.findViewById(R.id.btnConfirmar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        AlertDialog dialog = builder.create();

        btnConfirmar.setOnClickListener(v -> {
            String passwordActual = etPasswordActual.getText().toString().trim();
            String passwordNueva = etPasswordNueva.getText().toString().trim();
            String passwordConfirmar = etPasswordConfirmar.getText().toString().trim();

            if (passwordActual.isEmpty() || passwordNueva.isEmpty() || passwordConfirmar.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!Sesion.getUsuarioActual().getPassword().equals(passwordActual)){
                Toast.makeText(this, "La contraseña actual ingresada no es la correcta", Toast.LENGTH_SHORT).show();
                return;
            }

            if(passwordActual.equals(passwordNueva)){
                Toast.makeText(this, "La nueva contraseña debe ser diferente a la anterior", Toast.LENGTH_LONG).show();
                return;
            }

            if (!passwordNueva.equals(passwordConfirmar)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!Usuario.isPasswordStrong(passwordNueva)) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres, una letra mayúscula, una minúscula y un número", Toast.LENGTH_LONG).show();
                return;
            }

            com.tp_integrador_p2.conexion.usuarioDao.cambiarPassword(email, passwordActual, passwordNueva, new Conexion.CallbackConsulta<Void>() {
                @Override
                public void onResult(Void result) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PASSWORD_KEY, passwordNueva);
                    editor.apply();

                    Toast.makeText(perfilUsuarioActivity.this, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(perfilUsuarioActivity.this, "Error al cambiar la contraseña: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void volverAlDashboard() {
        Intent intent = new Intent(this, dashboardActivity.class);
        startActivity(intent);
        finish();
    }

}


