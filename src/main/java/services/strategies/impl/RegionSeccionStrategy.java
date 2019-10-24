package services.strategies.impl;

import negocio.Distrito;
import negocio.Seccion;
import services.strategies.RegionStrategy;

import java.util.Hashtable;

import static constants.Constants.*;

public class RegionSeccionStrategy implements RegionStrategy {

    private static RegionSeccionStrategy instance;

    private RegionSeccionStrategy() {
    }

    public static RegionSeccionStrategy getInstance(){
        if(instance == null){
            instance = new RegionSeccionStrategy();
        }
        return instance;
    }

    @Override
    public void process(String[] campos, Hashtable table) {
        String distritoCode;
        Distrito distrito;
        Seccion seccion;
        Hashtable distritoTable;

        distritoCode = campos[CODIGO_REGION].substring(0, 1);
        distrito = (Distrito) table.get(distritoCode);
        if (distrito == null) {
            distrito = new Distrito(distritoCode, DEFAULT_NAME);
            table.put(campos[CODIGO_REGION], distrito);
        }
        seccion = new Seccion(campos[CODIGO_REGION].substring(2), campos[NOMBRE_REGION]);
        distritoTable = distrito.getSecciones();
        distritoTable.put(campos[CODIGO_REGION].substring(2), seccion);
        distrito.setSecciones(distritoTable);
    }

}
