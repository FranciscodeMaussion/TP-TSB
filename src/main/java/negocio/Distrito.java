package negocio;

import soporte.TSBHashtableDA;

import java.util.Map;

public class Distrito {
    private String codigo;
    private String descripcion;
    private Map<Integer, Seccion> secciones;

    public Distrito(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.secciones = new TSBHashtableDA<>();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Map getChilds() {
        return secciones;
    }

    public void setChilds(Map secciones) {
        this.secciones = secciones;
    }

    @Override
    public String toString() {
        return "Distrito{" +
                "codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", secciones=" + secciones.toString() +
                '}';
    }
}
