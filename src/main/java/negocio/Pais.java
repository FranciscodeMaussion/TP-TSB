package negocio;

import services.MesasTextFileService;
import services.PostulacionesTextFileService;
import services.RegionesTextFileService;

import java.util.Hashtable;

import static constants.Constants.*;


public class Pais {
    private Hashtable resultados;
    private Hashtable postulaciones;
    private Hashtable regiones;
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
        regiones = descripcionPostulaciones.getPostulaciones();
    }

    public void cargarResultados() {
        resultados = mesasTotalesAgrupacion.sumarPorAgrupacion();
    }


    @Override
    public String toString() {
        return "Pais{" +
                "mesasTotalesAgrupacion=" + mesasTotalesAgrupacion +
                '}';
    }
}
