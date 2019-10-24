package negocio;

import java.util.Hashtable;

public class Distrito {
    private String codigo;
    private String descripcion;
    private Hashtable secciones;

    public Distrito(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.secciones = new Hashtable();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Hashtable getSecciones() {
        return secciones;
    }

    public void setSecciones(Hashtable secciones) {
        this.secciones = secciones;
    }
}
