package com.tp_integrador_p2.entidad;

public class Sesion {
    private static Usuario usuarioActual;

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }

    @Override
    public String toString() {
        return "Sesion{" +
                "usuarioActual=" + (usuarioActual != null ? usuarioActual.toString() : "null") +
                '}';
    }
}
