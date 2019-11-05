package com.tsb.negocio;

import java.util.Iterator;
import java.util.Map;


public class Pais {

    private Map<String, Agrupacion> postulaciones;
    private Map<String, Distrito> regiones;
    private int[] regionesCount;


    public int getAgrupacionesSize() {
        return postulaciones.size();
    }

    public Iterator<Map.Entry<String, Agrupacion>> mostrarResultadosXAgrupacion() {
        return postulaciones.entrySet().iterator();
    }

    public Iterator<Map.Entry<String, Distrito>> mostrarResultadosXDistrito() {
        return regiones.entrySet().iterator();
    }

    public Map<String, Agrupacion> getPostulaciones() {
        return postulaciones;
    }

    public void setPostulaciones(Map<String, Agrupacion> postulaciones) {
        this.postulaciones = postulaciones;
    }

    public Map<String, Distrito> getRegiones() {
        return regiones;
    }

    public void setRegiones(Map<String, Distrito> regiones) {
        this.regiones = regiones;
    }

    public int getDistritosSize() {
        return regionesCount[0];
    }

    public int getCircuitosSize() {
        return regionesCount[1];
    }

    public int getSeccionesSize() {
        return regionesCount[2];
    }

    public int getMesasSize() {
        return regionesCount[3];
    }

    public void setMesasSize(int count) {
        regionesCount[3] = count;
    }

    public void setRegionesCount(int[] regionesCount) {
        this.regionesCount = regionesCount;
    }

}
