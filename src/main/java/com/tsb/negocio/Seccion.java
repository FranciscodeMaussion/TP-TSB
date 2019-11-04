package com.tsb.negocio;

import com.tsb.soporte.Acumulador;
import com.tsb.soporte.TSBHashtableDA;

import java.util.Map;

public class Seccion implements Votable{
    private Acumulador cantidadVotos;
    private String codigo;
    private String descripcion;
    private Map<String, Circuito> circuitos;


    public Seccion(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.circuitos = new TSBHashtableDA<>();
        this.cantidadVotos = new Acumulador(0);
    }

    public Map<String, Circuito> getChilds() {
        return circuitos;
    }

    public void setChilds(Map circuitos) {
        this.circuitos = circuitos;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Map<String, Circuito> getCircuitos() {
        return circuitos;
    }

    @Override
    public String toString() {
        return "Seccion{" +
                "codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", circuitos=" + circuitos +
                '}';
    }

    @Override
    public Acumulador getAcumulador() {
        return cantidadVotos;
    }
}
