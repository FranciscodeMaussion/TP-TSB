package negocio;

import soporte.TSBHashtableDA;

import java.util.Map;

public class Seccion {
    private String codigo;
    private String descripcion;
    private Map circuitos;


    public Seccion(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.circuitos = new TSBHashtableDA();
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

}
