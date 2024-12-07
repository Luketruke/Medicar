package com.tp_integrador_p2.entidad;

public class Provincia {
    private int id_provincia;
    private String nombreProvincia;

    public Provincia(int id_provincia, String nombreProvincia) {
        this.id_provincia = id_provincia;
        this.nombreProvincia = nombreProvincia;
    }

    public int getIdProvincia() {
        return id_provincia;
    }

    public void setIdProvincia(int id_provincia) {
        this.id_provincia = id_provincia;
    }

    public String getNombreProvincia() {
        return nombreProvincia;
    }

    public void setNombreProvincia(String nombreProvincia) {
        this.nombreProvincia = nombreProvincia;
    }

    @Override
    public String toString() {
        return "Provincia{" +
                "id_provincia=" + id_provincia +
                ", nombreProvincia='" + nombreProvincia + '\'' +
                '}';
    }
}
