package com.tsb.negocio;

import com.tsb.soporte.Acumulador;

public class Agrupacion{
    private String codigoCategoria;
    private int codigoAgrupacion;
    private String nombreAgrupacion;
    private Acumulador cantidadVotos;

    public Agrupacion(String codigoCategoria, int codigoAgrupacion, String nombreAgrupacion) {
        this.codigoCategoria = codigoCategoria;
        this.codigoAgrupacion = codigoAgrupacion;
        this.nombreAgrupacion = nombreAgrupacion;
        this.cantidadVotos = new Acumulador(0);
    }

    public String getNombreAgrupacion() {
        return nombreAgrupacion;
    }

    @Override
    public String toString() {
        return "Agrupacion{" +
                "codigoCategoria='" + codigoCategoria + '\'' +
                ", codigoAgrupacion=" + codigoAgrupacion +
                ", nombreAgrupacion='" + nombreAgrupacion + '\'' +
                '}';
    }

    public int getVotos() {
        return cantidadVotos.getCantidad();
    }

    public void sumarVotos(int votos) {
        cantidadVotos.sumar(votos);
    }
}
