package com.mycompany.Poker.Entidad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mazo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Carta> cartas;

    public Mazo() {
        cartas = new ArrayList<>();
        String[] palos = {"CORAZONES", "DIAMANTES", "TREBOLES", "PICAS"};
        
        // Llenamos el mazo usando tu lógica del 1 al 13
        for (String palo : palos) {
            for (int v = 1; v <= 13; v++) {
                // Instanciamos tu clase con (valor entero, palo String)
                cartas.add(new Carta(v, palo));
            }
        }
    }

    // Revuelve las cartas de forma aleatoria
    public void barajar() {
        Collections.shuffle(cartas);
    }

    // Entrega la carta de arriba y la remueve del mazo
    public Carta robarCarta() {
        if (!cartas.isEmpty()) {
            return cartas.remove(0);
        }
        return null;
    }

    public int cartasRestantes() {
        return cartas.size();
    }
}