package com.tp_integrador_p2.conexion;

import android.util.Log;
import com.tp_integrador_p2.entidad.CentroMedico;
import com.tp_integrador_p2.entidad.Provincia;
import com.tp_integrador_p2.entidad.Localidad;

import java.util.List;

public class centroMedicoDao {
    private static final String TAG = "centroMedicoDao";
    private final Conexion conexion;

    public interface CallbackCentroMedico {
        void onResult(List<CentroMedico> centrosMedicos);
        void onError(String error);
    }

    public centroMedicoDao() {
        this.conexion = new Conexion();
    }

    public void buscarCentrosMedicos(double userLat, double userLon, CallbackCentroMedico callback) {
        String query = "SELECT cm.id_centromedico, cm.nombre, cm.direccion, cm.horario, cm.telefono, " +
                "cm.latitud, cm.longitud, p.id_provincia, p.nombreProvincia AS nombreProvincia, " +
                "l.id_localidad, l.nombreLocalidad AS nombreLocalidad, " +
                "(6371 * acos(cos(radians(?)) * cos(radians(cm.latitud)) * cos(radians(cm.longitud) - radians(?)) " +
                "+ sin(radians(?)) * sin(radians(cm.latitud)))) AS distancia " +
                "FROM centros_medicos cm " +
                "JOIN provincias p ON cm.id_provincia = p.id_provincia " +
                "JOIN localidades l ON cm.id_localidad = l.id_localidad " +
                "ORDER BY distancia ASC LIMIT 20";

        Log.d(TAG, "Preparando consulta SQL: " + query);

        conexion.ejecutarConsultaPreparada(
                query,
                preparedStatement -> {
                    preparedStatement.setDouble(1, userLat);
                    preparedStatement.setDouble(2, userLon);
                    preparedStatement.setDouble(3, userLat);
                },
                new Conexion.CallbackConsulta<List<CentroMedico>>() {
                    @Override
                    public void onResult(List<CentroMedico> centros) {
                        Log.d(TAG, "Centros médicos encontrados: " + centros.size());
                        callback.onResult(centros);
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error al buscar centros médicos: " + error);
                        callback.onError(error);
                    }
                },
                resultSet -> {
                    Provincia provincia = new Provincia(
                            resultSet.getInt("id_provincia"),
                            resultSet.getString("nombreProvincia")
                    );

                    Localidad localidad = new Localidad(
                            resultSet.getInt("id_localidad"),
                            resultSet.getString("nombreLocalidad")
                    );

                    return new CentroMedico(
                            resultSet.getInt("id_centromedico"),
                            resultSet.getString("nombre"),
                            resultSet.getString("direccion"),
                            resultSet.getString("horario"),
                            resultSet.getString("telefono"),
                            resultSet.getDouble("latitud"),
                            resultSet.getDouble("longitud"),
                            provincia,
                            localidad
                    );
                }
        );
    }
}
