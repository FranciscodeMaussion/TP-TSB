package com.tsb.negocio;

import com.tsb.soporte.Acumulador;

public class Mesa implements Votable {

    private String codigo;
    private String descripcion;
    private Acumulador cantidadVotos;

    public Mesa(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidadVotos = new Acumulador(0);
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
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
