package com.tsb.negocio;

import com.tsb.soporte.Acumulador;

import java.util.Iterator;
import java.util.Map;


public class Pais {

    private Map<String, Acumulador> resultados;
    private Map<String, Agrupacion> postulaciones;
    private Map<String, Distrito> regiones;

    public int getAgrupacionesCargadas() {
        return postulaciones.size();
    }

    public int getRegionesCargadas() {
        return regiones.size();
    }


    public Iterator<Map.Entry<String, Agrupacion>> mostrarResultadosXAgrupacion() {
        return postulaciones.entrySet().iterator();
    }

    public Iterator<Map.Entry<String, Distrito>> mostrarResultadosXDistrito() {
        return regiones.entrySet().iterator();
    }

    public Map<String, Acumulador> getResultados() {
        return resultados;
    }

    public void setResultados(Map<String, Acumulador> resultados) {
        this.resultados = resultados;
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
}
