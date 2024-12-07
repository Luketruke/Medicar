package com.tp_integrador_p2.entidad;

public class Farmacia {
    private long id_farmacia;
    private String nombre;
    private String direccion;
    private int id_provincia;
    private int id_localidad;
    private String cp;
    private double latitud;
    private double longitud;
    private String telefono;
    private String horario;
    private String origen_financiamiento;

    public Farmacia(long id_farmacia, String nombre, String direccion, int id_provincia, int id_localidad,
                    String cp, double latitud, double longitud, String telefono, String horario,
                    String origen_financiamiento) {
        this.id_farmacia = id_farmacia;
        this.nombre = nombre;
        this.direccion = direccion;
        this.id_provincia = id_provincia;
        this.id_localidad = id_localidad;
        this.cp = cp;
        this.latitud = latitud;
        this.longitud = longitud;
        this.telefono = telefono;
        this.horario = horario;
        this.origen_financiamiento = origen_financiamiento;
    }

    public long getIdFarmacia() {
        return id_farmacia;
    }

    public void setIdFarmacia(long id_farmacia) {
        this.id_farmacia = id_farmacia;
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

    public int getIdProvincia() {
        return id_provincia;
    }

    public void setIdProvincia(int id_provincia) {
        this.id_provincia = id_provincia;
    }

    public int getIdLocalidad() {
        return id_localidad;
    }

    public void setIdLocalidad(int id_localidad) {
        this.id_localidad = id_localidad;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getOrigenFinanciamiento() {
        return origen_financiamiento;
    }

    public void setOrigenFinanciamiento(String origen_financiamiento) {
        this.origen_financiamiento = origen_financiamiento;
    }

    @Override
    public String toString() {
        return "Farmacia{" +
                "id_farmacia=" + id_farmacia +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", id_provincia=" + id_provincia +
                ", id_localidad=" + id_localidad +
                ", cp='" + cp + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", telefono='" + telefono + '\'' +
                ", horario='" + horario + '\'' +
                ", origen_financiamiento='" + origen_financiamiento + '\'' +
                '}';
    }
}