package com.mycompany.Poker.Entidad;

import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 2L;

    private String nombre;
    private String contrasena; 
    private double saldo;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}
