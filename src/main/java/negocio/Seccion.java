package negocio;

import soporte.Acumulador;
import soporte.TSBHashtableDA;

import java.util.Map;

public class Seccion implements Votable{
    private Acumulador cantidadVotos;
    private String codigo;
    private String descripcion;
    private Map<Integer, Circuito> circuitos;


    public Seccion(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.circuitos = new TSBHashtableDA<>();
        this.cantidadVotos = new Acumulador(0);
    }

    public Map getChilds() {
        return circuitos;
    }

    public void setChilds(Map circuitos) {
        this.circuitos = circuitos;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Seccion{" +
                "codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", circuitos=" + circuitos +
                '}';
    }

    @Override
    public Acumulador getAcumulador() {
        return cantidadVotos;
    }
}
