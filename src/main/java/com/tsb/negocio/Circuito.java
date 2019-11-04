package com.tsb.negocio;

import com.tsb.soporte.Acumulador;

public class Circuito implements Votable {
    private String codigo;
    private String descripcion;
    private Acumulador cantidadVotos;

    public Circuito(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidadVotos = new Acumulador(0);
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return "Circuito{" +
                "codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }

    @Override
    public Acumulador getAcumulador() {
        return cantidadVotos;
    }
}
