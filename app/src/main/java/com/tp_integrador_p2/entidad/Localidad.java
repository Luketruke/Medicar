package com.tp_integrador_p2.entidad;

public class Localidad {
    private int id_localidad;
    private String nombreLocalidad;

    public Localidad(int id_localidad, String nombreLocalidad) {
        this.id_localidad = id_localidad;
        this.nombreLocalidad = nombreLocalidad;
    }

    public int getIdLocalidad() {
        return id_localidad;
    }

    public void setIdLocalidad(int id_localidad) {
        this.id_localidad = id_localidad;
    }

    public String getNombreLocalidad() {
        return nombreLocalidad;
    }

    public void setNombreLocalidad(String nombreLocalidad) {
        this.nombreLocalidad = nombreLocalidad;
    }

    @Override
    public String toString() {
        return "Localidad{" +
                "id_localidad=" + id_localidad +
                ", nombreLocalidad='" + nombreLocalidad + '\'' +
                '}';
    }
}
