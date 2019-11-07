package com.tsb.negocio;

import com.tsb.soporte.Acumulador;
import com.tsb.soporte.TSBHashtableDA;

import java.util.Map;

public class Circuito implements Votable {
    private Map<String, Acumulador> cantidadVotos;
    private String codigo;
    private String descripcion;
    private Map<String, Mesa> mesas;

    public Circuito(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidadVotos = new TSBHashtableDA<>();
        this.mesas = new TSBHashtableDA<>();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Map<String, Mesa> getMesas() {
        return mesas;
    }

    @Override
    public String toString() {
        return "Circuito{" +
                "codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
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
