package com.tsb.services.strategies.impl;

import com.tsb.negocio.Distrito;
import com.tsb.services.strategies.RegionStrategy;

import java.util.Map;

import static com.tsb.constants.Constants.*;

public class RegionDistritoStrategy implements RegionStrategy {

    private static RegionDistritoStrategy instance;

    private RegionDistritoStrategy() {
    }

    public static RegionDistritoStrategy getInstance() {
        if (instance == null) {
            instance = new RegionDistritoStrategy();
        }
        return instance;
    }

    @Override
    public void process(String[] campos, Map<String, Distrito> table) {
        Distrito distrito = table.get(campos[CODIGO_REGION]);
        if (distrito == null) {
            distrito = new Distrito(campos[CODIGO_REGION], campos[NOMBRE_REGION]);
            table.put(campos[CODIGO_REGION], distrito);
        } else {
            // May verify default name
            if (distrito.getDescripcion().equals(DEFAULT_NAME)) {
                distrito.setDescripcion(campos[NOMBRE_REGION]);
                table.put(campos[CODIGO_REGION], distrito);
            }
        }
    }
}
