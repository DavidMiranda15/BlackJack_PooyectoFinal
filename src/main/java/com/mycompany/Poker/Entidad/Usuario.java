package com.mycompany.Poker.Entidad;

import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 2L;

    private String nombre;
    private double saldo;

    public Usuario() {
    }

    public Usuario(String nombre, double saldo) {
        this.nombre = nombre;
        this.saldo = saldo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    @Override
    public String toString() {
        return "Usuario: " + nombre + " | Saldo Actual: $" + saldo;
    }
}