package com.tp_integrador_p2.conexion;

import android.util.Log;

import com.tp_integrador_p2.entidad.ConsultaMedicamento;
import com.tp_integrador_p2.entidad.Sesion;
import com.tp_integrador_p2.entidad.Usuario;

import java.util.List;

public class consultaMedicamentoDao {

    public interface Callback {
        void onSuccess();
        void onError(String error);
    }

    public void guardarConsultaMedicamento(String busqueda, Callback callback) {
        Conexion conexion = new Conexion();
        String query = "INSERT INTO consultas_medicamentos (id_usuario, busqueda, fecha) VALUES (?, ?, ?)";

        Usuario usuarioActual = Sesion.getUsuarioActual();
        if (usuarioActual == null) {
            Log.e("consultaMedicamentoDao", "Usuario actual es null. No se puede guardar la consulta.");
            callback.onError("El usuario no está autenticado.");
            return;
        }

        int usuarioId = usuarioActual.getId();

        conexion.ejecutarActualizacionPreparada(query, preparedStatement -> {
            preparedStatement.setInt(1, usuarioId);
            preparedStatement.setString(2, busqueda);
            preparedStatement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
        }, new Conexion.CallbackConsulta<Void>() {
            @Override
            public void onResult(Void result) {
                Log.d("consultaMedicamentoDao", "Consulta guardada exitosamente.");
                callback.onSuccess();
            }

            @Override
            public void onError(String error) {
                Log.e("consultaMedicamentoDao", "Error al guardar la consulta: " + error);
                callback.onError(error);
            }
        });
    }

    public interface HistorialCallback {
        void onSuccess(List<ConsultaMedicamento> historial);
        void onError(String error);
    }

    public void obtenerHistorial(HistorialCallback callback) {
        Conexion conexion = new Conexion();
        Usuario usuarioActual = Sesion.getUsuarioActual();

        if (usuarioActual == null) {
            callback.onError("El usuario no está autenticado.");
            return;
        }

        int usuarioId = usuarioActual.getId();
        String query = "SELECT id_consulta_medicamento, busqueda, fecha FROM consultas_medicamentos WHERE id_usuario = ? ORDER BY fecha DESC";

        conexion.ejecutarConsultaPreparada(query, preparedStatement -> {
            preparedStatement.setInt(1, usuarioId);
        }, new Conexion.CallbackConsulta<List<ConsultaMedicamento>>() {
            @Override
            public void onResult(List<ConsultaMedicamento> historial) {
                callback.onSuccess(historial);
            }

            @Override
            public void onError(String error) {
                Log.e("consultaMedicamentoDao", "Error al obtener el historial: " + error);
                callback.onError(error);
            }
        }, resultSet -> {
            ConsultaMedicamento consulta = new ConsultaMedicamento();
            consulta.setId(resultSet.getInt("id_consulta_medicamento"));
            consulta.setBusqueda(resultSet.getString("busqueda"));
            consulta.setFecha(resultSet.getDate("fecha"));
            return consulta;
        });
    }
}
