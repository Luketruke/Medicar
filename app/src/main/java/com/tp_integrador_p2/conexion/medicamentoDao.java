package com.tp_integrador_p2.conexion;

import android.util.Log;
import com.tp_integrador_p2.entidad.Medicamento;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class medicamentoDao {
    private static final String TAG = "medicamentoDao";
    private final Conexion conexion;

    private List<Medicamento> cacheMedicamentos = new ArrayList<>();

    public interface CallbackMedicamento {
        void onResult(List<Medicamento> medicamentos);
        void onError(String error);
    }

    public interface CallbackFiltros {
        void onResult(List<String> drogas, List<String> laboratorios);
        void onError(String error);
    }

    public medicamentoDao() {
        this.conexion = new Conexion();
    }

    public void buscarMedicamentos(String nombre, CallbackMedicamento callback) {
        String query = "SELECT * FROM medicamentos WHERE LOWER(marca) LIKE LOWER(?) OR LOWER(droga) LIKE LOWER(?)";
        Log.d(TAG, "Preparando consulta SQL: " + query);

        conexion.ejecutarConsultaPreparada(
                query,
                preparedStatement -> {
                    String searchParam = "%" + nombre.toLowerCase() + "%";
                    preparedStatement.setString(1, searchParam);
                    preparedStatement.setString(2, searchParam);
                },
                new Conexion.CallbackConsulta<List<Medicamento>>() {
                    @Override
                    public void onResult(List<Medicamento> medicamentos) {
                        if (medicamentos != null && !medicamentos.isEmpty()) {
                            List<Medicamento> medicamentosFiltrados = medicamentos.stream()
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                            Log.d(TAG, "Medicamentos encontrados: " + medicamentosFiltrados.size());
                            callback.onResult(medicamentosFiltrados);
                        } else {
                            Log.d(TAG, "No se encontraron medicamentos.");
                            callback.onResult(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error al buscar medicamentos: " + error);
                        callback.onError(error);
                    }
                },
                resultSet -> {
                    try {
                        return new Medicamento(
                                resultSet.getInt("id_medicamento"),
                                resultSet.getString("droga"),
                                resultSet.getString("marca"),
                                resultSet.getString("presentacion"),
                                resultSet.getString("laboratorio"),
                                Double.parseDouble(resultSet.getString("cobertura").replace(",", ".")),
                                resultSet.getString("copago")
                        );
                    } catch (Exception e) {
                        Log.e(TAG, "Error al mapear el resultado a un objeto Medicamento: " + e.getMessage());
                        return null;
                    }
                }
        );
    }
    public void getTodosMedicamentos(CallbackMedicamento callback) {
        String query = "SELECT * FROM medicamentos";
        Log.d(TAG, "Fetching all medicamentos for caching.");

        conexion.ejecutarConsultaPreparada(
                query,
                preparedStatement -> {},
                new Conexion.CallbackConsulta<List<Medicamento>>() {
                    @Override
                    public void onResult(List<Medicamento> medicamentos) {
                        if (medicamentos != null) {
                            cacheMedicamentos = medicamentos.stream()
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());
                            callback.onResult(cacheMedicamentos);
                        } else {
                            callback.onResult(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error fetching medicamentos: " + error);
                        callback.onError(error);
                    }
                },
                resultSet -> {
                    try {
                        return new Medicamento(
                                resultSet.getInt("id_medicamento"),
                                resultSet.getString("droga"),
                                resultSet.getString("marca"),
                                resultSet.getString("presentacion"),
                                resultSet.getString("laboratorio"),
                                Double.parseDouble(resultSet.getString("cobertura").replace(",", ".")),
                                resultSet.getString("copago")
                        );
                    } catch (Exception e) {
                        Log.e(TAG, "Error mapping result to Medicamento: " + e.getMessage());
                        return null;
                    }
                }
        );
    }

    public List<Medicamento> filtrarMedicamentosLocalmente(String nombre) {
        return cacheMedicamentos.stream()
                .filter(m -> m.getMarca().toLowerCase().contains(nombre.toLowerCase()) ||
                        m.getDroga().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void getFiltros(CallbackFiltros callback) {
        String queryDrogas = "SELECT DISTINCT droga FROM medicamentos";
        String queryLaboratorios = "SELECT DISTINCT laboratorio FROM medicamentos";

        List<String> drogas = new ArrayList<>();
        List<String> laboratorios = new ArrayList<>();

        conexion.ejecutarConsultaPreparada(queryDrogas,
                preparedStatement -> {},
                new Conexion.CallbackConsulta<List<String>>() {
            @Override
            public void onResult(List<String> result) {
                drogas.addAll(result);

                conexion.ejecutarConsultaPreparada(queryLaboratorios,
                        preparedStatement -> {},
                        new Conexion.CallbackConsulta<List<String>>() {
                    @Override
                    public void onResult(List<String> result2) {
                        laboratorios.addAll(result2);
                        callback.onResult(drogas, laboratorios);
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                },
                resultSet -> {
                    try {
                        return resultSet.getString("laboratorio");
                    } catch (Exception e) {
                        Log.e(TAG, "Error mapping result to Medicamento: " + e.getMessage());
                        return null;
                    }
                });
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        },
        resultSet -> {
            try {
                return resultSet.getString("droga");
            } catch (Exception e) {
                Log.e(TAG, "Error mapping result to Medicamento: " + e.getMessage());
                return null;
            }
        });
    }
}
