package negocio;

import soporte.Acumulador;

public class Agrupacion implements Votable{
    private String codigoCategoria;
    private int codigoAgrupacion;
    private String nombreAgrupacion;
    private Acumulador cantidadVotos;

    public Agrupacion(String codigoCategoria, int codigoAgrupacion, String nombreAgrupacion) {
        this.codigoCategoria = codigoCategoria;
        this.codigoAgrupacion = codigoAgrupacion;
        this.nombreAgrupacion = nombreAgrupacion;
        this.cantidadVotos = new Acumulador(0);
    }

    @Override
    public String toString() {
        return "Agrupacion{" +
                "codigoCategoria='" + codigoCategoria + '\'' +
                ", codigoAgrupacion=" + codigoAgrupacion +
                ", nombreAgrupacion='" + nombreAgrupacion + '\'' +
                '}';
    }

    @Override
    public Acumulador getAcumulador() {
        return cantidadVotos;
    }
}
