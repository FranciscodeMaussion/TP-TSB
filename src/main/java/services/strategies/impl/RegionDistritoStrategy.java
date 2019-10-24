package services.strategies.impl;

import negocio.Distrito;
import services.strategies.RegionStrategy;

import java.util.Hashtable;

import static constants.Constants.CODIGO_REGION;
import static constants.Constants.NOMBRE_REGION;

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
    public void process(String[] campos, Hashtable table) {
        Distrito distrito;
        distrito = (Distrito) table.get(campos[CODIGO_REGION]);
        if (distrito == null) {
            distrito = new Distrito(campos[CODIGO_REGION], campos[NOMBRE_REGION]);
            table.put(campos[CODIGO_REGION], distrito);
        } else {
            distrito.setDescripcion(campos[NOMBRE_REGION]);
        }
    }
}
