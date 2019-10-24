package negocio;

import java.util.Hashtable;

public class Seccion{
    private String codigo;
    private String descripcion;
    private Hashtable circuitos;


    public Seccion(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.circuitos = new Hashtable();
    }

    public Hashtable getChilds() {
        return circuitos;
    }

    public void setChilds(Hashtable circuitos) {
        this.circuitos = circuitos;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
