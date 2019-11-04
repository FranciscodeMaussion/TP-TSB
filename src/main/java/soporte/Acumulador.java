package soporte;

public class Acumulador {
    public int getCantidad() {
        return cantidad;
    }

    private int cantidad;

    public Acumulador(int inicial) {
        this.cantidad = inicial;
    }

    public void sumar(int valor) {
        cantidad += valor;
    }

    @Override
    public String toString() {
        return "Acumulador{" +
                "cantidad=" + cantidad +
                '}';
    }
}
