package services.strategies.impl;

import negocio.Circuito;
import negocio.Distrito;
import negocio.Seccion;
import services.strategies.RegionStrategy;

import java.util.Map;

import static constants.Constants.*;

public class RegionCircuitoStrategy implements RegionStrategy {

    private static RegionCircuitoStrategy instance;

    private RegionCircuitoStrategy() {
    }

    public static RegionCircuitoStrategy getInstance() {
        if (instance == null) {
            instance = new RegionCircuitoStrategy();
        }
        return instance;
    }

    @Override
    public void process(String[] campos, Map table) {
        Seccion seccion = null;
        String distritoCode = campos[CODIGO_REGION].substring(0, LENGTH_DISTRITO - 1);
        String seccionCode = campos[CODIGO_REGION].substring(LENGTH_DISTRITO, LENGTH_SECCION - 1);
        String circuitoCode = campos[CODIGO_REGION].substring(LENGTH_SECCION);

        Circuito circuito = new Circuito(circuitoCode, campos[NOMBRE_REGION]);

        Distrito distrito = (Distrito) table.get(distritoCode);
        if (distrito == null) {
            distrito = new Distrito(distritoCode, DEFAULT_NAME);
            table.put(distritoCode, distrito);
        } else {
            seccion = (Seccion) table.get(seccionCode);
        }

        if (seccion == null) {
            seccion = new Seccion(seccionCode, DEFAULT_NAME);
        }

        Map circuitoTable = seccion.getChilds();
        circuitoTable.put(circuitoCode, circuito);
        seccion.setChilds(circuitoTable);

        Map distritoTable = distrito.getChilds();
        distritoTable.put(seccionCode, seccion);
        distrito.setChilds(distritoTable);
    }
}
