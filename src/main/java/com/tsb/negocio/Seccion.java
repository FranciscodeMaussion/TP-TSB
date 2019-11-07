package com.tsb.negocio;

import com.tsb.soporte.Acumulador;
import com.tsb.soporte.TSBHashtableDA;

import java.util.Map;

public class Seccion implements Votable {
    private Map<String, Acumulador> cantidadVotos;
    private String codigo;
    private String descripcion;
    private Map<String, Circuito> circuitos;


    public Seccion(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.circuitos = new TSBHashtableDA<>();
        this.cantidadVotos = new TSBHashtableDA<>();
    }

    public Map<String, Circuito> getCircuitos() {
        return circuitos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
