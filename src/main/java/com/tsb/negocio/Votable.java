package com.tsb.negocio;

public interface Votable {

    int getVotosAgrupacion(String codigoAgrupacion);

    void sumarVotosAgrupacion(int votos, String codigoAgrupacion);

}
