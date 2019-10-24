package negocio;

import soporte.TextFile;

import java.util.Hashtable;

import static constants.Constants.*;


public class Pais {
    private Hashtable resultados;
    private Hashtable postulaciones;
    private Hashtable regiones;
    private TextFile mesasTotalesAgrupacion;
    private TextFile descripcionPostulaciones;
    private TextFile descripcionRegiones;

    public Pais(String path) {
        this.mesasTotalesAgrupacion = new TextFile(path+ FILE_MESAS);
        this.descripcionPostulaciones = new TextFile(path+FILE_POSTULACIONES);
        this.descripcionRegiones = new TextFile(path+FILE_REGIONES);
    }

    public void cargarResultados() {
        resultados = mesasTotalesAgrupacion.sumarPorAgrupacion();
    }

    public void cargarDescripcionPostulaciones(){
        postulaciones = descripcionPostulaciones.getPostulaciones();
    }

    public void cargarDescripcionRegiones(){
        regiones = descripcionPostulaciones.getPostulaciones();
    }


    @Override
    public String toString() {
        return "Pais{" +
                "mesasTotalesAgrupacion=" + mesasTotalesAgrupacion +
                '}';
    }
}
