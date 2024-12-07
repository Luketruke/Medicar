package com.tp_integrador_p2.conexion;

import android.util.Log;
import com.tp_integrador_p2.entidad.Farmacia;

import java.util.List;

public class farmaciaDao {
    private static final String TAG = "farmaciaDao";
    private final Conexion conexion;

    public interface CallbackFarmacia {
        void onResult(List<Farmacia> farmacias);
        void onError(String error);
    }

    public farmaciaDao() {
        this.conexion = new Conexion();
    }

    public void buscarFarmacias(double userLat, double userLon, CallbackFarmacia callback) {
        String query = "SELECT f.id_farmacia, f.nombre, f.direccion, f.horario AS horario, f.telefono, " +
                "f.latitud, f.longitud, f.id_provincia, f.id_localidad, f.cp, f.origen_financiamiento, " +
                "(6371 * acos(cos(radians(?)) * cos(radians(f.latitud)) * cos(radians(f.longitud) - radians(?)) " +
                "+ sin(radians(?)) * sin(radians(f.latitud)))) AS distancia " +
                "FROM farmacias f " +
                "ORDER BY distancia ASC LIMIT 20";

        Log.d(TAG, "Preparando consulta SQL: " + query);

        conexion.ejecutarConsultaPreparada(
                query,
                preparedStatement -> {
                    preparedStatement.setDouble(1, userLat);
                    preparedStatement.setDouble(2, userLon);
                    preparedStatement.setDouble(3, userLat);
                },
                new Conexion.CallbackConsulta<List<Farmacia>>() {
                    @Override
                    public void onResult(List<Farmacia> farmacias) {
                        Log.d(TAG, "Farmacias encontradas: " + farmacias.size());
                        callback.onResult(farmacias);
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error al buscar farmacias: " + error);
                        callback.onError(error);
                    }
                },
                resultSet -> {
                    return new Farmacia(
                            resultSet.getLong("id_farmacia"),
                            resultSet.getString("nombre"),
                            resultSet.getString("direccion"),
                            resultSet.getInt("id_provincia"),
                            resultSet.getInt("id_localidad"),
                            resultSet.getString("cp") != null ? resultSet.getString("cp") : "Sin código postal",
                            resultSet.getDouble("latitud"),
                            resultSet.getDouble("longitud"),
                            resultSet.getString("telefono") != null ? resultSet.getString("telefono") : "Sin teléfono",
                            resultSet.getString("horario") != null ? resultSet.getString("horario") : "Horario no disponible",
                            resultSet.getString("origen_financiamiento") != null ? resultSet.getString("origen_financiamiento") : "Desconocido"
                    );
                }
        );
    }
}
