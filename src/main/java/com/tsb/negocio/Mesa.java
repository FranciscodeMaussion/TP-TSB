package com.tsb.negocio;

import com.tsb.soporte.Acumulador;
import com.tsb.soporte.TSBHashtableDA;

import java.util.Map;

public class Mesa implements Votable {
    private Map<String, Acumulador> cantidadVotos;

    private String codigo;

    public Mesa(String codigo) {
        this.codigo = codigo;
        this.cantidadVotos = new TSBHashtableDA<>();
    }

    @Override
    public int getVotosAgrupacion(String codigoAgrupacion) {
        return cantidadVotos.get(codigoAgrupacion).getCantidad();
    }

    @Override
    public void sumarVotosAgrupacion(int votos, String codigoAgrupacion) {
        Acumulador votosAgrupacion = cantidadVotos.get(codigoAgrupacion);
        if (votosAgrupacion == null){
            votosAgrupacion = new Acumulador(0);
        }
        votosAgrupacion.sumar(votos);
        cantidadVotos.put(codigoAgrupacion, votosAgrupacion);
    }
}
