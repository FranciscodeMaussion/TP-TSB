package negocio;

public class Agrupacion {
    private String codigoCategoria;
    private int codigoAgrupacion;
    private String nombreAgrupacion;

    public Agrupacion(String codigoCategoria, int codigoAgrupacion, String nombreAgrupacion) {
        this.codigoCategoria = codigoCategoria;
        this.codigoAgrupacion = codigoAgrupacion;
        this.nombreAgrupacion = nombreAgrupacion;
    }

    @Override
    public String toString() {
        return "Agrupacion{" +
                "codigoCategoria='" + codigoCategoria + '\'' +
                ", codigoAgrupacion=" + codigoAgrupacion +
                ", nombreAgrupacion='" + nombreAgrupacion + '\'' +
                '}';
    }
}
