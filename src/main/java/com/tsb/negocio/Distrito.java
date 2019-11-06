package com.tsb.negocio;

import com.tsb.soporte.Acumulador;
import com.tsb.soporte.TSBHashtableDA;

import java.util.Map;

public class Distrito implements Votable {
    private Map<String, Acumulador> cantidadVotos;
    private String codigo;
    private String descripcion;
    private Map<String, Seccion> secciones;

    public Distrito(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.secciones = new TSBHashtableDA<>();
        this.cantidadVotos = new TSBHashtableDA<>();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Map<String, Seccion> getSecciones() {
        return secciones;
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
    public int getVotosAgrupacion(String codigoAgrupacion) {
        return cantidadVotos.get(codigoAgrupacion).getCantidad();
    }

    @Override
    public void sumarVotosAgrupacion(int votos, String codigoAgrupacion) {
        Acumulador votosAgrupacion = cantidadVotos.get(codigoAgrupacion);
        if (votosAgrupacion == null) {
            votosAgrupacion = new Acumulador(0);
        }
        votosAgrupacion.sumar(votos);
        cantidadVotos.put(codigoAgrupacion, votosAgrupacion);
    }
}
