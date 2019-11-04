package com.tsb.services.strategies.impl;

import com.tsb.negocio.Circuito;
import com.tsb.negocio.Distrito;
import com.tsb.negocio.Seccion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tsb.services.strategies.RegionStrategy;

import java.util.Map;

import static com.tsb.constants.Constants.*;

public class RegionCircuitoStrategy implements RegionStrategy {

    private static RegionCircuitoStrategy instance;
    private static final Logger LOG = LoggerFactory.getLogger(RegionCircuitoStrategy.class);


    private RegionCircuitoStrategy() {
    }

    public static RegionCircuitoStrategy getInstance() {
        if (instance == null) {
            instance = new RegionCircuitoStrategy();
        }
        return instance;
    }

    @Override
    public void process(String[] campos, Map<String, Distrito> table) {
        Seccion seccion = null;
        String distritoCode = campos[CODIGO_REGION].substring(0, LENGTH_DISTRITO);
        String seccionCode = campos[CODIGO_REGION].substring(LENGTH_DISTRITO, LENGTH_SECCION);
        String circuitoCode = campos[CODIGO_REGION].substring(LENGTH_SECCION);
        LOG.debug("Distrito: " + distritoCode + " Seccion: " + seccionCode + " Circuito: " + circuitoCode);

        Distrito distrito = (Distrito) table.get(distritoCode);
        if (distrito == null) {
            LOG.error("Distrito default: " + distritoCode);
            distrito = new Distrito(distritoCode, DEFAULT_NAME);
        }

        Map distritoTable = distrito.getChilds();
        seccion = (Seccion) distritoTable.get(seccionCode);
        if (seccion == null) {
            LOG.error("Seccion default: " + seccionCode);
            seccion = new Seccion(seccionCode, DEFAULT_NAME);
        }

        Map seccionTable = seccion.getChilds();
        Circuito circuito = new Circuito(circuitoCode, campos[NOMBRE_REGION]);

        seccionTable.put(circuitoCode, circuito);
        //seccion.setChilds(seccionTable);

        distritoTable.put(seccionCode, seccion);
        //distrito.setChilds(distritoTable);

        table.put(distritoCode, distrito);
    }
}
