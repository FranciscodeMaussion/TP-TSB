package services.strategies.impl;

import negocio.Distrito;
import negocio.Seccion;
import services.strategies.RegionStrategy;

import java.util.Map;

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
    public void process(String[] campos, Map table) {
        String distritoCode = campos[CODIGO_REGION].substring(0, LENGTH_DISTRITO-1);
        String seccionCode = campos[CODIGO_REGION].substring(LENGTH_DISTRITO);

        Seccion seccion = (Seccion) table.get(seccionCode);
        if (seccion == null) {
            seccion = new Seccion(seccionCode, campos[NOMBRE_REGION]);
        } else {
            seccion.setDescripcion(campos[NOMBRE_REGION]);
        }

        Distrito distrito = (Distrito) table.get(distritoCode);
        if (distrito == null) {
            distrito = new Distrito(distritoCode, DEFAULT_NAME);
            table.put(distritoCode, distrito);
        }

        Map distritoTable = distrito.getChilds();
        distritoTable.put(seccionCode, seccion);
        distrito.setChilds(distritoTable);
    }

}
