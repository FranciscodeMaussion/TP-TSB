package com.tsb.negocio;

import com.tsb.soporte.Acumulador;
import com.tsb.soporte.TSBHashtableDA;

import java.util.Map;

public class Circuito implements Votable {
    private String codigo;
    private String descripcion;
    private Acumulador cantidadVotos;
    private Map<String, Mesa> mesas;

    public Circuito(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidadVotos = new Acumulador(0);
        this.mesas = new TSBHashtableDA<>();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Map<String, Mesa> getMesas() {
        return mesas;
    }

    public void setMesas(Map<String, Mesa> mesas) {
        this.mesas = mesas;
    }

    @Override
    public String toString() {
        return "Circuito{" +
                "codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }

    @Override
    public int getVotos() {
        return cantidadVotos.getCantidad();
    }

    @Override
    public void sumarVotos(int votos) {
        cantidadVotos.sumar(votos);
    }
}
