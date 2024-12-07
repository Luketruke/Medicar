package com.tp_integrador_p2.conexion;

import android.util.Log;

import com.tp_integrador_p2.entidad.Sesion;
import com.tp_integrador_p2.entidad.Usuario;
import com.tp_integrador_p2.service.emailService;

import java.text.SimpleDateFormat;
import java.security.SecureRandom;
import java.util.Date;

public class usuarioDao {
    private static final String TAG = "usuarioDao";
    private final Conexion conexion;
    private String verificarMailEndpoint = "https://cf5f87ee-ebaa-4d60-90f4-168968fe8f6a-00-36uwwmroqdqck.kirk.repl.co/verificar-email?token=";

    public usuarioDao() {
        this.conexion = new Conexion();
    }

    public void insertarUsuario(Usuario usuario, Conexion.Callback callback) {
        try {
            Log.d(TAG, "Preparando consulta SQL para insertar usuario.");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String fechaNacimiento = dateFormat.format(usuario.getFechaNacimiento());

            String tokenVerificacion = java.util.UUID.randomUUID().toString();

            String query = "INSERT INTO usuarios (nombre, apellido, dni, fechaNacimiento, genero, email, telefono, direccion, password, tipoUsuario, token, email_verificado) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            conexion.ejecutarActualizacionPreparada(
                    query,
                    preparedStatement -> {
                        preparedStatement.setString(1, usuario.getNombre());
                        preparedStatement.setString(2, usuario.getApellido());
                        preparedStatement.setString(3, usuario.getDni());
                        preparedStatement.setString(4, fechaNacimiento);
                        preparedStatement.setString(5, usuario.getGenero());
                        preparedStatement.setString(6, usuario.getEmail().toLowerCase());
                        preparedStatement.setString(7, usuario.getTelefono());
                        preparedStatement.setString(8, usuario.getDireccion());
                        preparedStatement.setString(9, usuario.getPassword());
                        preparedStatement.setString(10, usuario.getTipoUsuario());
                        preparedStatement.setString(11, tokenVerificacion);
                        preparedStatement.setInt(12, usuario.isEmailVerificado());
                    },
                    new Conexion.CallbackConsulta<Void>() {
                        @Override
                        public void onResult(Void result) {
                            Log.d(TAG, "Usuario insertado correctamente.");
                            enviarCorreoVerificacion(usuario.getEmail(), tokenVerificacion);
                            callback.onSuccess();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Error al insertar usuario: " + error);
                            callback.onError(error);
                        }
                    }
            );
        } catch (Exception e) {
            Log.e(TAG, "Error al preparar la consulta SQL: " + e.getMessage(), e);
            callback.onError(e.getMessage());
        }
    }

    public void recuperarPassword(String email, emailService.Callback callback) {
        String nuevaPassword = generarPasswordAleatoria(10);

        String asunto = "Recuperación de cuenta";
        String mensaje = "Hola, tu nueva contraseña es: " + nuevaPassword;

        emailService.enviarCorreo(email, asunto, mensaje, new emailService.Callback() {
            @Override
            public void onSuccess() {
                actualizarPassword(email, nuevaPassword, new emailService.Callback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError("Error al actualizar la contraseña: " + error);
                    }

                    @Override
                    public void onResult(Object o) {
                    }
                });
            }

            @Override
            public void onError(String error) {
                callback.onError("Error al enviar el correo: " + error);
            }

            @Override
            public void onResult(Object o) {
            }
        });
    }

    private String generarPasswordAleatoria(int longitud) {
        final String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_+=<>?";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < longitud; i++) {
            int index = random.nextInt(caracteres.length());
            password.append(caracteres.charAt(index));
        }

        return password.toString();
    }

    private void actualizarPassword(String email, String nuevaPassword, emailService.Callback callback) {
        String query = "UPDATE usuarios SET password = ? WHERE LOWER(email) = LOWER(?)";

        conexion.ejecutarActualizacionPreparada(
                query,
                preparedStatement -> {
                    preparedStatement.setString(1, nuevaPassword);
                    preparedStatement.setString(2, email);
                },
                new Conexion.CallbackConsulta<Void>() {
                    @Override
                    public void onResult(Void result) {
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError("Error al actualizar la contraseña: " + error);
                    }
                }
        );
    }

    private void enviarCorreoVerificacion(String email, String token) {
        String asunto = "Verificación de cuenta";
        String mensaje = "Hola, por favor verifica tu cuenta usando el siguiente enlace:\n" +
                verificarMailEndpoint + token;

        emailService.enviarCorreo(email, asunto, mensaje, new emailService.Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Correo enviado exitosamente a " + email);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error al enviar correo a " + email + ": " + error);
            }

            @Override
            public void onResult(Object o) {
            }
        });
    }

    public void editarUsuario(String email, String nuevoNombre, String nuevoApellido,
                              String nuevoTelefono, String nuevaDireccion,
                              Conexion.CallbackConsulta<Void> callback) {
        String queryActualizar = "UPDATE usuarios SET nombre = ?, apellido = ?, telefono = ?, direccion = ? WHERE LOWER(email) = LOWER(?)";

        Conexion conexion = new Conexion();
        conexion.ejecutarActualizacionPreparada(
                queryActualizar,
                preparedStatement -> {
                    preparedStatement.setString(1, nuevoNombre);
                    preparedStatement.setString(2, nuevoApellido);
                    preparedStatement.setString(3, nuevoTelefono);
                    preparedStatement.setString(4, nuevaDireccion);
                    preparedStatement.setString(5, email);
                },
                new Conexion.CallbackConsulta<Void>() {
                    @Override
                    public void onResult(Void result) {
                        Sesion.getUsuarioActual().setNombre(nuevoNombre);
                        Sesion.getUsuarioActual().setApellido(nuevoApellido);
                        Sesion.getUsuarioActual().setTelefono(nuevoTelefono);
                        Sesion.getUsuarioActual().setDireccion(nuevaDireccion);
                        Sesion.getUsuarioActual().setEmail(email);
                        callback.onResult(null);
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error al actualizar usuario: " + error);
                        callback.onError("Error al actualizar usuario: " + error);
                    }
                }
        );
    }

    public void cargarUsuario(String email, Conexion.CallbackConsulta<Usuario> callback) {
        String query = "SELECT * FROM usuarios WHERE LOWER(email) = LOWER(?)";

        conexion.ejecutarConsultaPreparadaUnica(
                query,
                preparedStatement -> preparedStatement.setString(1, email),
                callback,
                resultSet -> new Usuario(
                        resultSet.getInt("id_usuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido"),
                        resultSet.getString("dni"),
                        resultSet.getDate("fechaNacimiento"),
                        resultSet.getString("genero"),
                        resultSet.getString("email"),
                        resultSet.getString("telefono"),
                        resultSet.getString("direccion"),
                        resultSet.getString("password"),
                        resultSet.getString("tipoUsuario"),
                        1
                )
        );
    }

    public static void cambiarPassword(String email, String passwordActual, String nuevaPassword, Conexion.CallbackConsulta<Void> callback) {
        String queryValidar = "SELECT password FROM usuarios WHERE LOWER(email) = LOWER(?)";
        String queryActualizar = "UPDATE usuarios SET password = ? WHERE LOWER(email) = LOWER(?)";

        Conexion conexion = new Conexion();
        conexion.ejecutarConsultaPreparadaUnica(
                queryValidar,
                preparedStatement -> preparedStatement.setString(1, email),
                new Conexion.CallbackConsulta<String>() {
                    @Override
                    public void onResult(String passwordGuardada) {
                        if (passwordGuardada.equals(passwordActual)) {
                            conexion.ejecutarActualizacionPreparada(
                                    queryActualizar,
                                    preparedStatement -> {
                                        preparedStatement.setString(1, nuevaPassword);
                                        preparedStatement.setString(2, email);
                                    },
                                    new Conexion.CallbackConsulta<Void>() {
                                        @Override
                                        public void onResult(Void result) {
                                            Sesion.getUsuarioActual().setPassword(nuevaPassword);

                                            callback.onResult(null);
                                        }

                                        @Override
                                        public void onError(String error) {
                                            callback.onError("Error al actualizar la contraseña: " + error);
                                        }
                                    }
                            );
                        } else {
                            callback.onError("La contraseña actual es incorrecta.");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError("Error al validar la contraseña actual: " + error);
                    }
                },
                resultSet -> resultSet.getString("PASSWORD")
        );
    }

    public void loginUsuario(String email, String password, Conexion.LoginCallback callback) {
        String queryEmail = "SELECT * FROM usuarios WHERE LOWER(email) = LOWER(?)";

        conexion.ejecutarConsultaPreparadaUnica(
                queryEmail,
                preparedStatement -> preparedStatement.setString(1, email),
                new Conexion.CallbackConsulta<Usuario>() {
                    @Override
                    public void onResult(Usuario usuario) {
                        if (usuario.getPassword().equals(password)) {
                            if (usuario.isEmailVerificado() == 1) {
                                Sesion.setUsuarioActual(usuario);
                                callback.onLoginSuccess();
                            } else {
                                callback.onLoginFailure("El correo no ha sido verificado.");
                            }
                        } else {
                            callback.onLoginFailure("La contraseña es incorrecta.");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        callback.onLoginFailure("El email no está registrado.");
                    }
                },
                resultSet -> new Usuario(
                        resultSet.getInt("id_usuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido"),
                        resultSet.getString("dni"),
                        resultSet.getDate("fechaNacimiento"),
                        resultSet.getString("genero"),
                        resultSet.getString("email"),
                        resultSet.getString("telefono"),
                        resultSet.getString("direccion"),
                        resultSet.getString("password"),
                        resultSet.getString("tipoUsuario"),
                        resultSet.getInt("email_verificado")
                )
        );
    }

    public void verificarDNI(String dni, Conexion.Callback callback) {
        String query = "SELECT COUNT(*) AS count FROM usuarios WHERE dni = ?";
        conexion.ejecutarConsultaPreparadaUnica(
                query,
                preparedStatement -> preparedStatement.setString(1, dni),
                new Conexion.CallbackConsulta<Integer>() {
                    @Override
                    public void onResult(Integer count) {
                        if (count > 0) {
                            callback.onError("El DNI ya está registrado");
                        } else {
                            callback.onSuccess();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError("Error al verificar DNI: " + error);
                    }
                },
                resultSet -> resultSet.getInt("count")
        );
    }

    public void verificarCorreo(String email, Conexion.Callback callback) {
        String query = "SELECT COUNT(*) AS count FROM usuarios WHERE LOWER(email) = LOWER(?)";

        Log.d("RecuperarPassword", "Email enviado: " + email);
        Log.d("RecuperarPassword", "Query ejecutada: " + query);

        conexion.ejecutarConsultaPreparadaUnica(
                query,
                preparedStatement -> preparedStatement.setString(1, email),
                new Conexion.CallbackConsulta<Integer>() {
                    @Override
                    public void onResult(Integer count) {
                        Log.d("RecuperarPassword", "Resultado de la consulta (count): " + count);

                        if (count > 0) {
                            callback.onSuccess();
                        } else {
                            callback.onError("El correo no está registrado");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("RecuperarPassword", "Error al ejecutar la consulta: " + error);
                        callback.onError("Error al verificar correo: " + error);
                    }
                },
                resultSet -> resultSet.getInt("count")
        );
    }

    public void verificarCorreoRegistro(String email, Conexion.Callback callback) {
        String query = "SELECT COUNT(*) AS count FROM usuarios WHERE LOWER(email) = LOWER(?)";

        Log.d("RecuperarPassword", "Email enviado: " + email);
        Log.d("RecuperarPassword", "Query ejecutada: " + query);

        conexion.ejecutarConsultaPreparadaUnica(
                query,
                preparedStatement -> preparedStatement.setString(1, email),
                new Conexion.CallbackConsulta<Integer>() {
                    @Override
                    public void onResult(Integer count) {
                        Log.d("RecuperarPassword", "Resultado de la consulta (count): " + count);

                        if (count == 0) {
                            callback.onSuccess();
                        } else {
                            callback.onError("El correo se encuentra registrado");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("RecuperarPassword", "Error al ejecutar la consulta: " + error);
                        callback.onError("Error al verificar correo: " + error);
                    }
                },
                resultSet -> resultSet.getInt("count")
        );
    }
}
