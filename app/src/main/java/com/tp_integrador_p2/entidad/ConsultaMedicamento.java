package com.tp_integrador_p2.entidad;

import java.util.Date;

public class ConsultaMedicamento {
    private int id;
    private Usuario usuario;
    private String busqueda;
    private Date fecha;

    public ConsultaMedicamento() {
    }

    public ConsultaMedicamento(int id, Usuario usuario, String busqueda, Date fecha) {
        this.id = id;
        this.usuario = usuario;
        this.busqueda = busqueda;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getBusqueda() {
        return busqueda;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setBusqueda(String busqueda) {
        this.busqueda = busqueda;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "ConsultaMedicamento{" +
                "id=" + id +
                ", usuario=" + (usuario != null ? usuario.toString() : "null") +
                ", busqueda='" + busqueda + '\'' +
                ", fecha=" + fecha +
                '}';
    }
}
