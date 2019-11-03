package negocio;

public class Circuito {
    private String codigo;
    private String descripcion;

    public Circuito(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Circuito{" +
                "codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
