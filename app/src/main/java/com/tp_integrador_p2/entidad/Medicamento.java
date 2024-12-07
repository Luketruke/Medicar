package com.tp_integrador_p2.entidad;

public class Medicamento {
    private int id_medicamento;
    private String droga;
    private String marca;
    private String presentacion;
    private String laboratorio;
    private double cobertura;
    private String copago;

    public Medicamento(int id_medicamento, String droga, String marca, String presentacion, String laboratorio, double cobertura, String copago) {
        this.id_medicamento = id_medicamento;
        this.droga = droga;
        this.marca = marca;
        this.presentacion = presentacion;
        this.laboratorio = laboratorio;
        this.cobertura = cobertura;
        this.copago = copago;
    }

    public int getIdMedicamento() {
        return id_medicamento;
    }

    public void setIdMedicamento(int id_medicamento) {
        this.id_medicamento = id_medicamento;
    }

    public String getDroga() {
        return droga;
    }

    public void setDroga(String droga) {
        this.droga = droga;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public double getCobertura() {
        return cobertura;
    }

    public void setCobertura(double cobertura) {
        this.cobertura = cobertura;
    }

    public String getCopago() {
        return copago;
    }

    public void setCopago(String copago) {
        this.copago = copago;
    }

    @Override
    public String toString() {
        return "Medicamento{" +
                "id_medicamento=" + id_medicamento +
                ", droga='" + droga + '\'' +
                ", marca='" + marca + '\'' +
                ", presentacion='" + presentacion + '\'' +
                ", laboratorio='" + laboratorio + '\'' +
                ", cobertura=" + cobertura +
                ", copago='" + copago + '\'' +
                '}';
    }
}
