package com.tp_integrador_p2.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.tp_integrador_p2.R;
import com.tp_integrador_p2.conexion.Conexion;
import com.tp_integrador_p2.conexion.usuarioDao;
import com.tp_integrador_p2.entidad.Usuario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class registroActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etApellido, etDNI, etFechaNacimiento, etEmail, etTelefono, etDireccion, etPassword, etConfirmPassword;
    private Spinner spinnerGenero, spinnerTipoUsuario;
    private Button btnRegistrar;

    private usuarioDao usuarioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        Button btnVolverIniciarSesion = findViewById(R.id.btnVolverIniciarSesion);

        usuarioDao = new usuarioDao();

        etNombre = findViewById(R.id.etName);
        etApellido = findViewById(R.id.etLastName);
        etDNI = findViewById(R.id.etDNI);
        etFechaNacimiento = findViewById(R.id.etBirthdate);
        etEmail = findViewById(R.id.etEmail);
        etTelefono = findViewById(R.id.etPhone);
        etDireccion = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spinnerGenero = findViewById(R.id.spinnerGender);
        spinnerTipoUsuario = findViewById(R.id.spinnerUserType);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        setupTextWatcher(etNombre);
        setupTextWatcher(etApellido);

        etFechaNacimiento.setOnClickListener(v -> mostrarDatePickerDialog());
        btnRegistrar.setOnClickListener(view -> registrarUsuario());

        btnVolverIniciarSesion.setOnClickListener(view -> {
            Intent intent = new Intent(registroActivity.this, loginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void mostrarDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, yearSelected, monthSelected, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(yearSelected, monthSelected, dayOfMonth);

            Calendar minValidDate = Calendar.getInstance();
            minValidDate.add(Calendar.YEAR, -18);

            if (selectedDate.after(minValidDate)) {
                Toast.makeText(this, "Debes tener al menos 18 años", Toast.LENGTH_SHORT).show();
                etFechaNacimiento.setText("");
            } else {
                String fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, monthSelected + 1, yearSelected);
                etFechaNacimiento.setText(fechaSeleccionada);
            }
        }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void registrarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String dni = etDNI.getText().toString().trim();
        String fechaNacimientoStr = etFechaNacimiento.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String genero = spinnerGenero.getSelectedItem().toString();
        String tipoUsuario = spinnerTipoUsuario.getSelectedItem().toString();

        if (!validarCampos(nombre, apellido, dni, fechaNacimientoStr, email, telefono, direccion, password, confirmPassword, genero, tipoUsuario)) {
            return;
        }

        java.util.Date fechaNacimiento;
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            fechaNacimiento = inputFormat.parse(fechaNacimientoStr);
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de fecha no válido. Usa dd/MM/yyyy", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = new Usuario(nombre, apellido, dni, fechaNacimiento, genero, email, telefono, direccion, password, tipoUsuario);
        usuario.setEmailVerificado(0);

        usuarioDao.verificarCorreoRegistro(email, new Conexion.Callback() {
            @Override
            public void onSuccess() {
                usuarioDao.verificarDNI(dni, new Conexion.Callback() {
                    @Override
                    public void onSuccess() {
                        usuarioDao.insertarUsuario(usuario, new Conexion.Callback() {
                            @Override
                            public void onSuccess() {
                                runOnUiThread(() -> {
                                    Toast.makeText(registroActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(registroActivity.this, loginActivity.class);
                                    startActivity(intent);
                                    finish();
                                });
                            }
                            @Override
                            public void onError(String error) {
                                runOnUiThread(() -> {
                                    Toast.makeText(registroActivity.this, "Error en el registro: " + error, Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    }
                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(registroActivity.this, "El DNI ya está registrado. Intenta con otro.", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(registroActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setupTextWatcher(TextInputEditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isEditing) return;

                isEditing = true;

                String input = editable.toString();
                if (!input.isEmpty()) {
                    String capitalized = input.substring(0, 1).toUpperCase() + input.substring(1);
                    editText.setText(capitalized);
                    editText.setSelection(capitalized.length());
                }

                isEditing = false;
            }
        });
    }

    private boolean validarCampos(String nombre, String apellido, String dni, String fechaNacimientoStr,
                                  String email, String telefono, String direccion, String password,
                                  String confirmPassword, String genero, String tipoUsuario) {
        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || fechaNacimientoStr.isEmpty() ||
                email.isEmpty() || telefono.isEmpty() || direccion.isEmpty() || password.isEmpty() ||
                genero.equals("Seleccione genero...") || tipoUsuario.equals("Seleccione tipo de afiliado...")) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (genero.equals("Seleccione genero...") || genero.isEmpty()) {
            Toast.makeText(this, "Debes seleccionar un genero", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tipoUsuario.equals("Seleccione tipo de afiliado...") || tipoUsuario.isEmpty()) {
            Toast.makeText(this, "Debes seleccionar un tipo de afiliado", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Usuario.isEmailValid(email)) {
            Toast.makeText(this, "Correo electrónico no valido", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Usuario.isPasswordStrong(password)) {
            Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres, una letra mayúscula, una minúscula y un número", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
