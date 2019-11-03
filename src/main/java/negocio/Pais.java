package negocio;

import services.MesasTextFileService;
import services.PostulacionesTextFileService;
import services.RegionesTextFileService;
import soporte.Acumulador;

import java.util.Iterator;
import java.util.Map;

import static constants.Constants.*;


public class Pais {

    private Map<String, Acumulador> resultados;
    private Map<String, Agrupacion> postulaciones;
    private Map<String, Distrito> regiones;
    private MesasTextFileService mesasTotalesAgrupacion;
    private PostulacionesTextFileService descripcionPostulaciones;
    private RegionesTextFileService descripcionRegiones;

    public Pais(String path) {
        this.descripcionPostulaciones = new PostulacionesTextFileService(path + FILE_POSTULACIONES);
        this.descripcionRegiones = new RegionesTextFileService(path + FILE_REGIONES);
        this.mesasTotalesAgrupacion = new MesasTextFileService(path + FILE_MESAS);
    }

    public void cargarDescripcionPostulaciones() {
        postulaciones = descripcionPostulaciones.getPostulaciones();
    }

    public void cargarDescripcionRegiones() {
        regiones = descripcionRegiones.getRegiones();
    }

    public void cargarResultados() {
        resultados = mesasTotalesAgrupacion.sumarVotos(regiones, postulaciones);
    }

    public int getAgrupacionesCargadas() {
        return postulaciones.size();
    }

    public int getRegionesCargadas() {
        return regiones.size();
    }

    public Iterator<Map.Entry<String, Acumulador>> printResultados() {
        return resultados.entrySet().iterator();
    }


    @Override
    public String toString() {
        return "Pais{" +
                "mesasTotalesAgrupacion=" + mesasTotalesAgrupacion +
                '}';
    }
}
