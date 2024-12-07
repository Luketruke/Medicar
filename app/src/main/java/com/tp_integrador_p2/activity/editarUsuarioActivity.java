package com.tp_integrador_p2.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.tp_integrador_p2.R;
import com.tp_integrador_p2.conexion.Conexion;
import com.tp_integrador_p2.conexion.usuarioDao;
import com.tp_integrador_p2.entidad.Sesion;
import com.tp_integrador_p2.entidad.Usuario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class editarUsuarioActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String EMAIL_KEY = "email_key";
    public static final String PASSWORD_KEY = "password_key";

    private TextInputEditText etNombre, etApellido, etTelefono, etDireccion;
    private Button btnGuardar, btnVolver;
    private usuarioDao usuarioDao;

    SharedPreferences sharedPreferences;
    String email;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(EMAIL_KEY, null);

        Log.d("editarUsuarioActivity", "Estado de Sesion: " + Sesion.getUsuarioActual());

        if (email == null || email.isEmpty() || Sesion.getUsuarioActual() == null) {
            Log.d("editarUsuarioActivity", "No hay usuario en la sesión. Redirigiendo a login...");
            Intent intent = new Intent(this, loginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Log.d("editarUsuarioActivity", "Sesión activa con email: " + email);
        setContentView(R.layout.activity_editar_perfil);

        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        btnGuardar = findViewById(R.id.btnGuardarCambios);
        btnVolver = findViewById(R.id.btnVolver);

        //String email = getIntent().getStringExtra("email");

        cargarDatosUsuario(Sesion.getUsuarioActual());
        usuarioDao = new usuarioDao();
        /*

        usuarioDao.cargarUsuario(email, new Conexion.CallbackConsulta<Usuario>() {
            @Override
            public void onResult(Usuario usuario) {

                etNombre.setText(usuario.getNombre());
                etApellido.setText(usuario.getApellido());
                etTelefono.setText(usuario.getTelefono());
                etDireccion.setText(usuario.getDireccion());
            }
            @Override
            public void onError(String error) {
                Toast.makeText(editarUsuarioActivity.this, "Error al cargar usuario: " + error, Toast.LENGTH_SHORT).show();
            }
        });
        */

        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnVolver.setOnClickListener(v -> volverAlPerfil());
    }



    private boolean validarCampos(String nombre, String apellido, String telefono, String direccion) {
        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void guardarCambios() {
        String nuevoNombre = etNombre.getText().toString().trim();
        String nuevoApellido = etApellido.getText().toString().trim();
        String nuevoTelefono = etTelefono.getText().toString().trim();
        String nuevaDireccion = etDireccion.getText().toString().trim();

        if (!validarCampos(nuevoNombre, nuevoApellido, nuevoTelefono, nuevaDireccion)) {
            return;
        }

        //Intent intent = getIntent();
        //String email = intent.getStringExtra("email");
        usuarioDao.editarUsuario(email,nuevoNombre,nuevoApellido,nuevoTelefono,nuevaDireccion, new Conexion.CallbackConsulta<Void>() {
            @Override
            public void onResult(Void result) {
                Toast.makeText(editarUsuarioActivity.this, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show();
                volverAlPerfil();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(editarUsuarioActivity.this, "Error al guardar cambios: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void volverAlPerfil() {
        Intent intent = new Intent(this, perfilUsuarioActivity.class);
        startActivity(intent);
        finish();
    }

    /*
    private void mostrarDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, yearSelected, monthSelected, dayOfMonth) -> {
            String fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, monthSelected + 1, yearSelected);
            etfechaNacimiento.setText(fechaSeleccionada);
        }, year, month, day);
        datePickerDialog.show();
    }
     */
    private void cargarDatosUsuario(Usuario usuario) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        //String fechaNacimiento = dateFormat.format(usuario.getFechaNacimiento());

        etNombre.setText(usuario.getNombre());
        etApellido.setText(usuario.getApellido());
        etTelefono.setText(usuario.getTelefono());
        etDireccion.setText(usuario.getDireccion());

        /*userName.setText(usuario.getNombre());
        userSurname.setText(usuario.getApellido());
        userEmail.setText(usuario.getEmail());
        userPhone.setText(usuario.getTelefono());
        userAddress.setText(usuario.getDireccion());
        userDni.setText(usuario.getDni());
        userBirthdate.setText(fechaNacimiento);
        */
    }
}
