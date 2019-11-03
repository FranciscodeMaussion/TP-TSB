package negocio;

import soporte.Acumulador;

public class Circuito implements Votable{
    private String codigo;
    private String descripcion;
    private Acumulador cantidadVotos;

    public Circuito(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidadVotos = new Acumulador(0);
    }

    @Override
    public String toString() {
        return "Circuito{" +
                "codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }

    @Override
    public Acumulador getAcumulador() {
        return cantidadVotos;
    }
}
