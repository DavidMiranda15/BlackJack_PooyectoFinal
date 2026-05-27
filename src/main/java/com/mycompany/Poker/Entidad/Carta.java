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
    
    // LÓGICA DE PUNTOS EXCLUSIVA PARA EL BLACKJACK 21
    public int getPuntosBlackjack() {
        // Si es J (11), Q (12) o K (13), en Blackjack valen 10 puntos
        if (this.valor > 10) {
            return 10;
        }
        // Si es un As (1), por regla general inicia valiendo 11 puntos
        if (this.valor == 1) {
            return 11;
        }
        // Para los números del 2 al 10, su puntuación es su mismo valor
        return this.valor;
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