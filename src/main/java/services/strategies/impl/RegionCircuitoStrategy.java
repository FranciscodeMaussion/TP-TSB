package services.strategies.impl;

import negocio.Circuito;
import negocio.Distrito;
import negocio.Seccion;
import services.strategies.RegionStrategy;

import java.util.Hashtable;

import static constants.Constants.*;

public class RegionCircuitoStrategy implements RegionStrategy {

    private static RegionCircuitoStrategy instance;

    private RegionCircuitoStrategy() {
    }

    public static RegionCircuitoStrategy getInstance(){
        if(instance == null) {
            instance = new RegionCircuitoStrategy();
        }
        return instance;
    }

    @Override
    public void process(String[] campos, Hashtable table) {
        Distrito distrito;
        Seccion seccion;
        Circuito circuito;
        String distritoCode;
        String seccionCode;
        Hashtable distritoTable;

        distritoCode = campos[CODIGO_REGION].substring(0, 1);
        seccionCode = campos[CODIGO_REGION].substring(2, 4);
        distrito = (Distrito) table.get(distritoCode);
        if (distrito == null) {
            distrito = new Distrito(distritoCode, DEFAULT_NAME);
            table.put(campos[CODIGO_REGION], distrito);
            seccion = null;
        } else {
            seccion = (Seccion) table.get(seccionCode);
        }
        if (seccion == null) {
            seccion = new Seccion(seccionCode, DEFAULT_NAME);
        }
        circuito = new Circuito(campos[CODIGO_REGION].substring(5), campos[NOMBRE_REGION]);

        Hashtable circuitoTable = seccion.getCircuitos();
        circuitoTable.put(campos[CODIGO_REGION].substring(5), circuito);
        seccion.setCircuitos(circuitoTable);

        distritoTable = distrito.getSecciones();
        distritoTable.put(seccionCode, seccion);
        distrito.setSecciones(distritoTable);
    }
}
