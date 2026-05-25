package com.mycompany.Poker.Entidad;

import java.io.Serializable;


public class Carta implements Serializable {
    private static final long serialVersionUID = 1L;

    private int valor;    
    private String palo;  

    // notas de los requisitos solicitados, constructor vacio
    public Carta() {
    }

    public Carta(int valor, String palo) {
        this.valor = valor;
        this.palo = palo;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getPalo() {
        return palo;
    }

    public void setPalo(String palo) {
        this.palo = palo;
    }

    @Override
    public String toString() {
        String nombreValor = String.valueOf(valor);
        if (valor == 1) nombreValor = "As";
        else if (valor == 11) nombreValor = "J";
        else if (valor == 12) nombreValor = "Q";
        else if (valor == 13) nombreValor = "K";
        
        return nombreValor + " de " + palo;
    }
}