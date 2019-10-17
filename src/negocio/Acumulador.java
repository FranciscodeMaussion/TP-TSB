package negocio;

public class Acumulador {
    private int cantidad;

    public Acumulador(int inicial) {
        this.cantidad = inicial;
    }

    public void sumar(int valor){
        cantidad += valor;
    }
}
