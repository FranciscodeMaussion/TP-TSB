package negocio;

import soporte.OAHashtable;

import java.util.Map;

public class Distrito{
    private String codigo;
    private String descripcion;
    private Map secciones;

    public Distrito(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.secciones = new OAHashtable();
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
}
