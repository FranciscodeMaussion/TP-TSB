package com.tsb.services.strategies.impl;

import com.tsb.negocio.Distrito;
import com.tsb.negocio.Seccion;
import com.tsb.services.strategies.RegionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.tsb.constants.Constants.*;

public class RegionSeccionStrategy implements RegionStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(RegionSeccionStrategy.class);
    private static RegionSeccionStrategy instance;


    private RegionSeccionStrategy() {
    }

    public static RegionSeccionStrategy getInstance() {
        if (instance == null) {
            instance = new RegionSeccionStrategy();
        }
        return instance;
    }

    @Override
    public void process(String[] campos, Map<String, Distrito> table) {
        String distritoCode = campos[CODIGO_REGION].substring(0, LENGTH_DISTRITO);
        String seccionCode = campos[CODIGO_REGION].substring(LENGTH_DISTRITO);
        LOG.debug("Distrito: " + distritoCode + " Seccion: " + seccionCode);

        Distrito distrito = table.get(distritoCode);
        if (distrito == null) {
            LOG.error("Distrito default: " + distritoCode);
            distrito = new Distrito(distritoCode, DEFAULT_NAME);
        }
        Map distritoTable = distrito.getSecciones();

        Seccion seccion = (Seccion) distritoTable.get(seccionCode);
        if (seccion == null) {
            seccion = new Seccion(seccionCode, campos[NOMBRE_REGION]);
        } else {
            seccion.setDescripcion(campos[NOMBRE_REGION]);
        }

        distritoTable.put(seccionCode, seccion);
        //distrito.setChilds(distritoTable);

        table.put(distritoCode, distrito);
    }

}
