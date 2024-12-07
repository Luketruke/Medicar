package com.tp_integrador_p2.entidad;

public class CentroMedico {
    private int id_centromedico;
    private String nombre;
    private String direccion;
    private String horario;
    private String telefono;
    private double latitud;
    private double longitud;
    private Provincia provincia;
    private Localidad localidad;

    public CentroMedico(int id_centromedico, String nombre, String direccion, String horario, String telefono, double latitud, double longitud, Provincia provincia, Localidad localidad) {
        this.id_centromedico = id_centromedico;
        this.nombre = nombre;
        this.direccion = direccion;
        this.horario = horario;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
        this.provincia = provincia;
        this.localidad = localidad;
    }

    public int getIdCentroMedico() {
        return id_centromedico;
    }

    public void setIdCentroMedico(int id_centromedico) {
        this.id_centromedico = id_centromedico;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    @Override
    public String toString() {
        return "CentroMedico{" +
                "id_centromedico=" + id_centromedico +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", horario='" + horario + '\'' +
                ", telefono='" + telefono + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", provincia=" + provincia +
                ", localidad=" + localidad + '\'' +
                '}';
    }
}
