package com.tsb.soporte;

public class Acumulador {
    private int cantidad;

    public Acumulador(int inicial) {
        this.cantidad = inicial;
    }

    public int getCantidad() {
        return cantidad;
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
