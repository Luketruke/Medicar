package com.tp_integrador_p2.conexion;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Conexion {
/*
    private static final String HOST = "192.168.1.37";
    private static final String PORT = "3306";
    private static final String DATABASE_NAME = "medicar";
    private static final String USER = "usuario_android";
    private static final String PASSWORD = "";
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE_NAME;
*/
   private static final String HOST = "sql10.freesqldatabase.com";
    private static final String PORT = "3306";
    private static final String DATABASE_NAME = "sql10749648";
    private static final String USER = "sql10749648";
    private static final String PASSWORD = "E5Xug5vSJj";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE_NAME;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface Callback {
        void onSuccess();
        void onError(String error);
    }

    public interface CallbackConsulta<T> {
        void onResult(T result);
        void onError(String error);
    }

    public interface LoginCallback {
        void onLoginSuccess();
        void onLoginFailure(String message);
    }

    public interface ParamConfigurator {
        void configure(PreparedStatement preparedStatement) throws Exception;
    }

    public interface ResultParser<T> {
        T parse(ResultSet resultSet) throws Exception;
    }

    public <T> void ejecutarConsultaPreparada(String query, ParamConfigurator configurador, CallbackConsulta<List<T>> callback, ResultParser<T> parser) {
        executor.execute(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                configurador.configure(preparedStatement);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<T> resultados = new ArrayList<>();
                    while (resultSet.next()) {
                        T resultado = parser.parse(resultSet);
                        resultados.add(resultado);
                    }

                    new Handler(Looper.getMainLooper()).post(() -> callback.onResult(resultados));
                }
            } catch (Exception e) {
                Log.e("Conexion", "Error al ejecutar consulta preparada: " + e.getMessage(), e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public <T> void ejecutarConsultaPreparadaUnica(String query, ParamConfigurator configurador, CallbackConsulta<T> callback, ResultParser<T> parser) {
        executor.execute(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                configurador.configure(preparedStatement);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        T resultado = parser.parse(resultSet);
                        new Handler(Looper.getMainLooper()).post(() -> callback.onResult(resultado));
                    } else {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onError("No se encontraron resultados."));
                    }
                }
            } catch (Exception e) {
                Log.e("Conexion", "Error al ejecutar consulta preparada: " + e.getMessage(), e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public void ejecutarActualizacionPreparada(String query, ParamConfigurator configurador, CallbackConsulta<Void> callback) {
        executor.execute(() -> {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                configurador.configure(preparedStatement);
                preparedStatement.executeUpdate();

                new Handler(Looper.getMainLooper()).post(() -> callback.onResult(null));
            } catch (Exception e) {
                Log.e("Conexion", "Error al ejecutar actualización: " + e.getMessage(), e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            }
        });
    }
}