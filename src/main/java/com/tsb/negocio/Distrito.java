package com.tsb.negocio;

import com.tsb.soporte.Acumulador;
import com.tsb.soporte.TSBHashtableDA;

import java.util.Map;

public class Distrito implements Votable {
    private Acumulador cantidadVotos;
    private String codigo;
    private String descripcion;
    private Map<String, Seccion> secciones;

    public Distrito(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.secciones = new TSBHashtableDA<>();
        this.cantidadVotos = new Acumulador(0);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Map<String, Seccion> getChilds() {
        return secciones;
    }

    public void setChilds(Map<String, Seccion> secciones) {
        this.secciones = secciones;
    }

    @Override
    public String toString() {
        return "Distrito{" +
                "codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", secciones=" + secciones.toString() +
                '}';
    }

    @Override
    public Acumulador getAcumulador() {
        return cantidadVotos;
    }
}
