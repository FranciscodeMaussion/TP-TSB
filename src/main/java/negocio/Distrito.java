package negocio;

import soporte.Acumulador;
import soporte.TSBHashtableDA;

import java.util.Map;

public class Distrito implements Votable{
    private Acumulador cantidadVotos;
    private String codigo;
    private String descripcion;
    private Map<Integer, Seccion> secciones;

    public Distrito(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.secciones = new TSBHashtableDA<>();
        this.cantidadVotos = new Acumulador(0);
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Map getChilds() {
        return secciones;
    }

    public void setChilds(Map secciones) {
        this.secciones = secciones;
    }

    public int getSize() {
        return 0;
    }

    @Override
    public String toString() {
        return "Distrito{" +
                "codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", secciones=" + secciones.toString() +
                '}';
    }

    @Override
    public Acumulador getAcumulador() {
        return cantidadVotos;
    }
}
