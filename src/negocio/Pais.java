package negocio;

import soporte.TSBHashTable;
import soporte.TextFile;


public class Pais {
    private TSBHashTable resultados;
    private TextFile mesasTotalesAgrupacion;

    public Pais(String path) {
        this.mesasTotalesAgrupacion = new TextFile(path+"/mesas_totales_agrp_politica.dsv");
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
